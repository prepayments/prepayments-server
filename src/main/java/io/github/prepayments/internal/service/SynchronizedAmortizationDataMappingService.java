package io.github.prepayments.internal.service;

import io.github.prepayments.internal.Mapping;
import io.github.prepayments.service.dto.AmortizationEntryDTO;
import io.github.prepayments.service.dto.PrepaymentDataDTO;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import static java.math.RoundingMode.HALF_EVEN;

@Service("synchronizedAmortizationDataMappingService")
public class SynchronizedAmortizationDataMappingService implements AmortizationDataMappingService<PrepaymentDataDTO, AmortizationEntryDTO> {

    private final Mapping<PrepaymentDataDTO, AmortizationEntryDTO> amortizationDataEntryDTOMapping;
    private final Environment env;

    public SynchronizedAmortizationDataMappingService(final Mapping<PrepaymentDataDTO, AmortizationEntryDTO> amortizationDataEntryDTOMapping, final Environment env) {
        this.amortizationDataEntryDTOMapping = amortizationDataEntryDTOMapping;
        this.env = env;
    }

    @Override
    public List<AmortizationEntryDTO> map(final PrepaymentDataDTO prepaymentDataEntity) {

        final List<AmortizationEntryDTO> amortizationEntries = new CopyOnWriteArrayList<>();

        IntStream.of(prepaymentDataEntity.getPrepaymentPeriods()).forEach(period -> {

            AmortizationEntryDTO amortized = amortizationDataEntryDTOMapping.toValue2(prepaymentDataEntity);

            // set amount of amortization
            amortized.setTransactionAmount(
                prepaymentDataEntity.getPrepaymentAmount().divide(
                    BigDecimal.valueOf(prepaymentDataEntity.getPrepaymentPeriods()), HALF_EVEN
                )
            );

            // @formatter:off
            LocalDate amortizationDate = prepaymentDataEntity.getPrepaymentDate()
                                                      .withDayOfMonth(1) // revert to first day of current month
                                                      .plusMonths(period) // add the months to the date to correct period
                                                      .withDayOfMonth(Integer.parseInt(Objects.requireNonNull(env.getProperty("AMORTIZATION_DATE")))); // Set exact amortization data
            // @formatter:off

            // set date of amortization date
            amortized.setAmortizationDate(amortizationDate);

            amortizationEntries.add(amortized);
        });

        return amortizationEntries;
    }
}
