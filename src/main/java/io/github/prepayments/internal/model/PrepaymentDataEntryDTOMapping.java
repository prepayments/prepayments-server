package io.github.prepayments.internal.model;

import io.github.prepayments.internal.Mapping;
import io.github.prepayments.service.dto.PrepaymentDataDTO;
import io.github.prepayments.service.dto.PrepaymentEntryDTO;
import org.apache.commons.lang3.math.NumberUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.math.BigDecimal;


public interface PrepaymentDataEntryDTOMapping extends Mapping<PrepaymentDataDTO, PrepaymentEntryDTO> {

    @Mappings(
        value = {
            @org.mapstruct.Mapping(target = "transactionAmount", source = "prepaymentAmount"),
        }
    )
    default BigDecimal toBigDecimalMap(BigDecimal doublePrecisionAmount) {
        return doublePrecisionAmount;
    }
}
