package io.github.prepayments.internal.model;

import io.github.prepayments.internal.Mapping;
import io.github.prepayments.service.dto.AmortizationEntryDTO;
import io.github.prepayments.service.dto.PrepaymentDataDTO;
import org.mapstruct.Mapper;

/**
 * This is not an ordinary mapping job as it entails mapping multiples of amorization-entries to each prepayment-data entity.
 * But nevertheless we let mapstruct do the mapping for the fields which are mapped with similar names
 */
@Mapper(componentModel = "spring", uses = {})
public interface AmortizationDataEntryDTOMapping extends Mapping<PrepaymentDataDTO, AmortizationEntryDTO> {

    // TODO amortization-date
    // TODO transaction-amount
}

