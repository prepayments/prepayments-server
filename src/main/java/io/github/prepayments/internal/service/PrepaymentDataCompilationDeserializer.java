package io.github.prepayments.internal.service;

import com.google.common.collect.ImmutableList;
import io.github.prepayments.service.dto.PrepaymentDataDTO;

import java.util.Collection;
import java.util.List;

/**
 * This interface picks data from prepayment-data and creates corresponding list of entities
 */
public interface PrepaymentDataCompilationDeserializer<C> {

    List<C> deserialize(PrepaymentDataDTO prepaymentData);

    default List<C> deserialize(List<PrepaymentDataDTO> prepaymentData) {

        return prepaymentData.stream()
                      .map(this::deserialize)
                      .flatMap(Collection::stream)
                      .collect(ImmutableList.toImmutableList());
    }
}
