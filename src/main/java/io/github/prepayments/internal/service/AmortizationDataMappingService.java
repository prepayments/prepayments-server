package io.github.prepayments.internal.service;

import java.util.List;

/**
 * This service converts a condensed data model into its respective entities representation in the system. In particular we take prepayment-data and break down into constituent amortization-entries
 *
 * @param <P> Prepayment-Data entity containing condensed representation of amortization-entries
 * @param <A> Amortization-Entry data
 */
public interface AmortizationDataMappingService<P, A> {

    /**
     * @param prepaymentDataEntity Condensed entity from which we create amortization entries
     */
    List<A> map(List<P> prepaymentDataEntity);

}
