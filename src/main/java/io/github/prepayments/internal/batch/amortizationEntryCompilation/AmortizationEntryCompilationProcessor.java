package io.github.prepayments.internal.batch.amortizationEntryCompilation;

import io.github.prepayments.internal.service.AmortizationDataMappingService;
import io.github.prepayments.service.dto.AmortizationEntryDTO;
import io.github.prepayments.service.dto.PrepaymentDataDTO;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;

public class AmortizationEntryCompilationProcessor
    implements ItemProcessor<List<PrepaymentDataDTO>, List<AmortizationEntryDTO>> {

    private final AmortizationDataMappingService<PrepaymentDataDTO, AmortizationEntryDTO> amortizationDataMappingService;

    public AmortizationEntryCompilationProcessor(final AmortizationDataMappingService<PrepaymentDataDTO, AmortizationEntryDTO> amortizationDataMappingService) {
        this.amortizationDataMappingService = amortizationDataMappingService;
    }

    @Override
    public List<AmortizationEntryDTO> process(final List<PrepaymentDataDTO> prepaymentDataList) throws Exception {

        return amortizationDataMappingService.map(prepaymentDataList);
    }
}
