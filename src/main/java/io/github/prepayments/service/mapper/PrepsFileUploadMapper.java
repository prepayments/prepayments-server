package io.github.prepayments.service.mapper;


import io.github.prepayments.domain.*;
import io.github.prepayments.service.dto.PrepsFileUploadDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link PrepsFileUpload} and its DTO {@link PrepsFileUploadDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PrepsFileUploadMapper extends EntityMapper<PrepsFileUploadDTO, PrepsFileUpload> {



    default PrepsFileUpload fromId(Long id) {
        if (id == null) {
            return null;
        }
        PrepsFileUpload prepsFileUpload = new PrepsFileUpload();
        prepsFileUpload.setId(id);
        return prepsFileUpload;
    }
}
