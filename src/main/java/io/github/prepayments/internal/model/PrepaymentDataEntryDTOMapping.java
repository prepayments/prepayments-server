package io.github.prepayments.internal.model;

import io.github.prepayments.internal.Mapping;
import io.github.prepayments.service.dto.PrepaymentDataDTO;
import io.github.prepayments.service.dto.PrepaymentEntryDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {})
public interface PrepaymentDataEntryDTOMapping extends Mapping<PrepaymentDataDTO, PrepaymentEntryDTO> {

}
