package io.github.prepayments.internal.batch.amortizationEntryCompilation;

import io.github.prepayments.internal.service.BatchService;
import io.github.prepayments.service.dto.AmortizationEntryDTO;
import org.springframework.batch.item.ItemWriter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AmortizationEntryCompilationWriter implements ItemWriter<List<AmortizationEntryDTO>> {

    private final BatchService<AmortizationEntryDTO> amortizationEntryDTOBatchService;

    public AmortizationEntryCompilationWriter(final BatchService<AmortizationEntryDTO> amortizationEntryDTOBatchService) {
        this.amortizationEntryDTOBatchService = amortizationEntryDTOBatchService;
    }

    @Override
    public void write(final List<? extends List<AmortizationEntryDTO>> entities) throws Exception {

        amortizationEntryDTOBatchService.save(entities.stream().flatMap(Collection::stream).collect(Collectors.toList()));
        amortizationEntryDTOBatchService.index(entities.stream().flatMap(Collection::stream).collect(Collectors.toList()));
    }
}
