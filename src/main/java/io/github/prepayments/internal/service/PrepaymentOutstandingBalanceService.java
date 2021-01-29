package io.github.prepayments.internal.service;

import com.google.common.collect.ImmutableList;
import io.github.jhipster.service.filter.LocalDateFilter;
import io.github.prepayments.internal.model.PrepaymentBalanceDTO;
import io.github.prepayments.service.AmortizationEntryQueryService;
import io.github.prepayments.service.PrepaymentEntryQueryService;
import io.github.prepayments.service.dto.AmortizationEntryCriteria;
import io.github.prepayments.service.dto.AmortizationEntryDTO;
import io.github.prepayments.service.dto.PrepaymentEntryCriteria;
import io.github.prepayments.service.dto.PrepaymentEntryDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

/**
 * This is the implementation for the outstanding-balance-service for prepayment-balance-dto.
 * JPA queries are employed to speed up the request therefore the implementation may appear complex though
 * actually writing out the query might have been simpler. Nevertheless we use java based API on the queries
 * as this might enable IDE-supported refactoring and also the ability to break down the query into a series
 * of building blocks that can be applied at will.
 * Important consideration is how to map the data once we have what we need, however at the same time we have
 * to consider ways to filter off quickly all the data objects that we do not need, in order to speed up
 * the query. Remember this is a real-time request with a client waiting on the other end of the API.
 *
 * The balance-date is an end of line cut off; that is entities that do not begin before the balance date
 * are irrelevant for consideration.
 * At the same time all previously created entities that are fully amortised do not need to enter into the
 * consideration.
 * Logic for filtering off the former is easy, but logic for removing the later is either expensive or difficult
 * to intuit. This is the first iteration of the program and therefore the developer has chosen to cop-out by picking
 * the easiest approach; to get filtered data from the database and process the result using java Stream API.
 */
@Service("prepaymentOutstandingBalanceService")
public class PrepaymentOutstandingBalanceService implements OutstandingBalanceService<PrepaymentBalanceDTO> {

    private final PrepaymentEntryQueryService prepaymentEntryQueryService;
    private final AmortizationEntryQueryService amortizationEntryQueryService;
    private final PrepaymentOutstandingBalanceMapper prepaymentOutstandingBalanceMapper;

    public PrepaymentOutstandingBalanceService(final PrepaymentEntryQueryService prepaymentEntryQueryService, final AmortizationEntryQueryService amortizationEntryQueryService,
                                               final PrepaymentOutstandingBalanceMapper prepaymentOutstandingBalanceMapper) {
        this.prepaymentEntryQueryService = prepaymentEntryQueryService;
        this.amortizationEntryQueryService = amortizationEntryQueryService;
        this.prepaymentOutstandingBalanceMapper = prepaymentOutstandingBalanceMapper;
    }

    @Override
    public List<PrepaymentBalanceDTO> outstandingBalance(final LocalDate balanceDate) {

        List<AmortizationEntryDTO> amortizationEntries =
            amortizationEntryQueryService.findByCriteria(existingAmortizationEntriesCriteria(balanceDate));

        List<PrepaymentEntryDTO> prepaymentEntries =
            prepaymentEntryQueryService.findByCriteria(existingPrepaymentEntriesCriteria(balanceDate));

        return prepaymentEntries.stream()
                         .peek(prep -> {
                             amortizationEntries
                                        .stream()
                                        .filter(amort -> amort.getPrepaymentDataId().equals(prep.getPrepaymentDataId()))
                                        .map(AmortizationEntryDTO::getTransactionAmount)
                                        .reduce(BigDecimal::add)
                                        .ifPresent(amortizationAmount -> prep.setTransactionAmount(prep.getTransactionAmount().subtract(amortizationAmount)));
                         }).map(prepaymentOutstandingBalanceMapper::map)
                                .peek(bal -> {
                                    amortizationEntries
                                        .stream()
                                        .filter(amort -> amort.getPrepaymentDataId().equals(bal.getPrepaymentDataId()))
                                        .map(AmortizationEntryDTO::getExpenseAccountNumber)
                                        .findFirst()
                                        .ifPresent(bal::setExpenseAccountNumber);
                                })
                         .collect(ImmutableList.toImmutableList());
    }


    /**
     * Using the balance date this criteria will remove prepayment-entry entities created after
     * the balance date
     * @param balanceDate
     * @return
     */
    private PrepaymentEntryCriteria existingPrepaymentEntriesCriteria(LocalDate balanceDate) {

        PrepaymentEntryCriteria prepaymentEntryCriteria = new PrepaymentEntryCriteria();

        prepaymentEntryCriteria.setPrepaymentDate(entriesBeforeBalanceDate(balanceDate));

        return prepaymentEntryCriteria;
    }

    /**
     * This criteria filters of entries created after the balance-date
     *
     * @param balanceDate
     * @return
     */
    private AmortizationEntryCriteria existingAmortizationEntriesCriteria(LocalDate balanceDate) {

        AmortizationEntryCriteria amortizationEntryCriteria = new AmortizationEntryCriteria();

        amortizationEntryCriteria.setPrepaymentDate(entriesBeforeBalanceDate(balanceDate));

        return amortizationEntryCriteria;
    }

    private LocalDateFilter entriesBeforeBalanceDate(final LocalDate balanceDate) {
        LocalDateFilter entriesB4BalanceDate = new LocalDateFilter();
        entriesB4BalanceDate.setLessThanOrEqual(balanceDate);
        return entriesB4BalanceDate;
    }
}
