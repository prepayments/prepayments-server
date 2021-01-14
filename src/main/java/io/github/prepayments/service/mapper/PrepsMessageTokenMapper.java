package io.github.prepayments.service.mapper;


import io.github.prepayments.domain.*;
import io.github.prepayments.service.dto.PrepsMessageTokenDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link PrepsMessageToken} and its DTO {@link PrepsMessageTokenDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PrepsMessageTokenMapper extends EntityMapper<PrepsMessageTokenDTO, PrepsMessageToken> {



    default PrepsMessageToken fromId(Long id) {
        if (id == null) {
            return null;
        }
        PrepsMessageToken prepsMessageToken = new PrepsMessageToken();
        prepsMessageToken.setId(id);
        return prepsMessageToken;
    }
}
