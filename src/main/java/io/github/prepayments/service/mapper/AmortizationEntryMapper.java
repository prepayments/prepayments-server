package io.github.prepayments.service.mapper;


import io.github.prepayments.domain.*;
import io.github.prepayments.service.dto.AmortizationEntryDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link AmortizationEntry} and its DTO {@link AmortizationEntryDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface AmortizationEntryMapper extends EntityMapper<AmortizationEntryDTO, AmortizationEntry> {



    default AmortizationEntry fromId(Long id) {
        if (id == null) {
            return null;
        }
        AmortizationEntry amortizationEntry = new AmortizationEntry();
        amortizationEntry.setId(id);
        return amortizationEntry;
    }
}
