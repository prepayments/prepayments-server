package io.github.prepayments.internal.batch.amortizationEntryCompilation;

import io.github.prepayments.internal.service.AmortizationDataMappingService;
import io.github.prepayments.service.dto.AmortizationEntryDTO;
import io.github.prepayments.service.dto.PrepaymentDataDTO;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This processor takes prepayment-data DTO and translates that into constituent amortization-entries
 */
public class AmortizationEntryCompilationProcessor implements ItemProcessor<List<PrepaymentDataDTO>, List<AmortizationEntryDTO>> {

    private final String compilationToken;
    private final AmortizationDataMappingService<PrepaymentDataDTO, AmortizationEntryDTO> amortizationDataMappingService;

    public AmortizationEntryCompilationProcessor(final AmortizationDataMappingService<PrepaymentDataDTO, AmortizationEntryDTO> amortizationDataMappingService,
                                                 final @Value("#{jobParameters['compilationToken']}") String compilationToken) {
        this.amortizationDataMappingService = amortizationDataMappingService;
        this.compilationToken = compilationToken;
    }

    @Override
    public List<AmortizationEntryDTO> process(final List<PrepaymentDataDTO> prepaymentDataList) throws Exception {

        return amortizationDataMappingService.map(prepaymentDataList)
            .stream()
            .peek(dto -> dto.setCompilationToken(compilationToken))
            .collect(Collectors.toList());
    }
}
