package io.github.prepayments.internal.service;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;

/**
 * This service converts a condensed data model into its respective entities representation in the system.
 * In particular we take prepayment-data and break down into constituent amortization-entries
 *
 * @param <P> Prepayment-Data entity containing condensed representation of amortization-entries
 * @param <A> Amortization-Entry data
 */
public interface AmortizationDataMappingService<P, A> {

    /**
     *
     * @param prepaymentDataEntity Condensed entity from which we create amortization entries
     * @return
     */
    List<A> map(P prepaymentDataEntity);

    /**
     * Converts a list of condensed entities into respective factored down amortization-entries
     *
     * @param prepaymentEntities  Condensed entities from which we create amortization entries
     * @return
     */
    default List<A> map(List<P> prepaymentEntities) {

        return prepaymentEntities
            .stream()
            .map(this::map)
            .flatMap(Collection::stream)
            .collect(ImmutableList.toImmutableList());
    }
}
