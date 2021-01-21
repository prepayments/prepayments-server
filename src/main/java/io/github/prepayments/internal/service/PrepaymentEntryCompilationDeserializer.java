package io.github.prepayments.internal.service;

import io.github.prepayments.internal.Mapping;
import io.github.prepayments.service.dto.PrepaymentDataDTO;
import io.github.prepayments.service.dto.PrepaymentEntryDTO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Supposed to convert condensed prepayment-data into deserialized prepayment-entry entities, though internally it's just a matter
 * of using a mapper for the two entities
 */
@Service("prepaymentEntryCompilationDeserializer")
public class PrepaymentEntryCompilationDeserializer implements PrepaymentDataCompilationDeserializer<PrepaymentEntryDTO> {

    private final Mapping<PrepaymentDataDTO, PrepaymentEntryDTO> prepaymentDataEntryDTOMapping;

    public PrepaymentEntryCompilationDeserializer(final Mapping<PrepaymentDataDTO, PrepaymentEntryDTO> prepaymentDataEntryDTOMapping) {
        this.prepaymentDataEntryDTOMapping = prepaymentDataEntryDTOMapping;
    }

    @Override
    public List<PrepaymentEntryDTO> deserialize(final PrepaymentDataDTO prepaymentData) {

        // Just realized a single prepayment-data entity corresponds to one prepayment-entry entity
        return Collections.singletonList(prepaymentDataEntryDTOMapping.toValue2(prepaymentData));
    }
}
