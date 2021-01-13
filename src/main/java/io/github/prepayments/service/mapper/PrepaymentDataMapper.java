package io.github.prepayments.service.mapper;


import io.github.prepayments.domain.*;
import io.github.prepayments.service.dto.PrepaymentDataDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link PrepaymentData} and its DTO {@link PrepaymentDataDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PrepaymentDataMapper extends EntityMapper<PrepaymentDataDTO, PrepaymentData> {



    default PrepaymentData fromId(Long id) {
        if (id == null) {
            return null;
        }
        PrepaymentData prepaymentData = new PrepaymentData();
        prepaymentData.setId(id);
        return prepaymentData;
    }
}
