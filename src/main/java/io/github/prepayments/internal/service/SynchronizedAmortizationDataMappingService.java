package io.github.prepayments.internal.service;

import io.github.prepayments.internal.Mapping;
import io.github.prepayments.service.dto.AmortizationEntryDTO;
import io.github.prepayments.service.dto.PrepaymentDataDTO;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.math.RoundingMode.HALF_EVEN;

/**
 * This service takes a single prepayment-data entity and with its number of periods advances the amortization date in the amortization-entries generated from it, in a date by date increment on the
 * number of periods while at the same time updating the amortization amount with the division of the prepayment amount by the number of periods. The process can be slow or time consuming for large
 * data sets. However designing an asynchronous process will not be without serious amounts of technical debt, so until such a time that a case can definitely be made for a faster mapping service it
 * is hoped that this simple implementation will suffice.
 */
@Service("synchronizedAmortizationDataMappingService")
public class SynchronizedAmortizationDataMappingService implements AmortizationDataMappingService<PrepaymentDataDTO, AmortizationEntryDTO> {

    private final Mapping<PrepaymentDataDTO, AmortizationEntryDTO> amortizationDataEntryDTOMapping;
    private final int amortization_date;

    public SynchronizedAmortizationDataMappingService(final Mapping<PrepaymentDataDTO, AmortizationEntryDTO> amortizationDataEntryDTOMapping, final Environment env) {
        this.amortizationDataEntryDTOMapping = amortizationDataEntryDTOMapping;
        this.amortization_date = Integer.parseInt(Objects.requireNonNull(env.getProperty("AMORTIZATION_DATE")));
    }

    @Override
    public List<AmortizationEntryDTO> map(final List<PrepaymentDataDTO> prepaymentDataEntities) {

        final List<AmortizationEntryDTO> amortizationEntries = new CopyOnWriteArrayList<>();

        prepaymentDataEntities.forEach(entity -> {

            int monthlyAmortizationPeriods = entity.getPrepaymentPeriods();

            for (int period = 1; period <= monthlyAmortizationPeriods; period++) {

                AmortizationEntryDTO amortized = amortizationDataEntryDTOMapping.toValue2(entity);

                // set amount of amortization
                amortized.setTransactionAmount(entity.getPrepaymentAmount().divide(BigDecimal.valueOf(monthlyAmortizationPeriods), HALF_EVEN));

                // @formatter:off
                LocalDate amortizationDate = entity.getPrepaymentDate()
                                                   .withDayOfMonth(1) // revert to first day of current month
                                                   .plusMonths(period) // add the months to the date to correct period
                                                   .withDayOfMonth(amortization_date); // Set exact amortization data
                // @formatter:off

                amortized.setAmortizationDate(amortizationDate);

                // set prepayment-data-id
                amortized.setPrepaymentDataId(entity.getId());

                amortizationEntries.add(amortized);
            }

        });

        return amortizationEntries;
    }
}
