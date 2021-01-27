package io.github.prepayments.internal.batch.prepaymentEntryCompilation;

import io.github.prepayments.internal.service.PrepaymentDataCompilationDeserializer;
import io.github.prepayments.service.dto.PrepaymentDataDTO;
import io.github.prepayments.service.dto.PrepaymentEntryDTO;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This compilation processor takes a list of prepayment-data and returns a list of prepayment-dto
 */
@Scope("job")
public class PrepaymentEntryCompilationProcessor implements ItemProcessor<List<PrepaymentDataDTO>, List<PrepaymentEntryDTO>> {

    private final String compilationToken;
    private final PrepaymentDataCompilationDeserializer<PrepaymentEntryDTO> prepaymentDataCompilationDeserializer;

    public PrepaymentEntryCompilationProcessor(final PrepaymentDataCompilationDeserializer<PrepaymentEntryDTO> prepaymentDataCompilationDeserializer,
                                               final @Value("#{jobParameters['compilationToken']}") String compilationToken) {
        this.prepaymentDataCompilationDeserializer = prepaymentDataCompilationDeserializer;
        this.compilationToken = compilationToken;
    }

    @Override
    public List<PrepaymentEntryDTO> process(final List<PrepaymentDataDTO> prepaymentData) throws Exception {
        return prepaymentDataCompilationDeserializer.deserialize(prepaymentData)
            .stream()
            .peek(dto -> dto.setCompilationToken(compilationToken))
            .collect(Collectors.toList());
    }
}
