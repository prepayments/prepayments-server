package io.github.prepayments.service.mapper;


import io.github.prepayments.domain.*;
import io.github.prepayments.service.dto.CompilationRequestDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link CompilationRequest} and its DTO {@link CompilationRequestDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface CompilationRequestMapper extends EntityMapper<CompilationRequestDTO, CompilationRequest> {



    default CompilationRequest fromId(Long id) {
        if (id == null) {
            return null;
        }
        CompilationRequest compilationRequest = new CompilationRequest();
        compilationRequest.setId(id);
        return compilationRequest;
    }
}
