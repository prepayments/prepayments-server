package io.github.prepayments.internal.service;

import java.time.LocalDate;
import java.util.List;

/**
 * This is a service that computes the balance amount for a given prepayment given the amortization-entries
 * that are applicable by a given date.
 *
 * The list returned contains a list of entities who transaction amount still contains a balance as at a given date
 */
public interface OutstandingBalanceService<T> {

    List<T> outstandingBalance(LocalDate balanceDate);
}
