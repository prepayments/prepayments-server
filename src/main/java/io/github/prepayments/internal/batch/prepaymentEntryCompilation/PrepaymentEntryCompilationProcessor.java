package io.github.prepayments.internal.batch.prepaymentEntryCompilation;

import io.github.prepayments.internal.service.PrepaymentDataCompilationDeserializer;
import io.github.prepayments.service.dto.PrepaymentDataDTO;
import io.github.prepayments.service.dto.PrepaymentEntryDTO;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;

/**
 * This compilation processor takes a list of prepayment-data and returns a list of prepayment-dto
 */
public class PrepaymentEntryCompilationProcessor implements ItemProcessor<List<PrepaymentDataDTO>, List<PrepaymentEntryDTO>> {

    private final PrepaymentDataCompilationDeserializer<PrepaymentEntryDTO> prepaymentDataCompilationDeserializer;

    public PrepaymentEntryCompilationProcessor(final PrepaymentDataCompilationDeserializer<PrepaymentEntryDTO> prepaymentDataCompilationDeserializer) {
        this.prepaymentDataCompilationDeserializer = prepaymentDataCompilationDeserializer;
    }

    @Override
    public List<PrepaymentEntryDTO> process(final List<PrepaymentDataDTO> prepaymentData) throws Exception {
        return prepaymentDataCompilationDeserializer.deserialize(prepaymentData);
    }
}
