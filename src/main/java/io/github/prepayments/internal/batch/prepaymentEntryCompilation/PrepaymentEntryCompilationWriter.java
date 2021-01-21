package io.github.prepayments.internal.batch.prepaymentEntryCompilation;

import com.google.common.collect.ImmutableList;
import io.github.prepayments.internal.service.BatchService;
import io.github.prepayments.service.dto.PrepaymentEntryDTO;
import org.springframework.batch.item.ItemWriter;

import java.util.Collection;
import java.util.List;

/**
 * This writer takes a list of entries to persist in the DB and persists them in batches using the batch-service implementation
 */
public class PrepaymentEntryCompilationWriter implements ItemWriter<List<PrepaymentEntryDTO>> {

    private final BatchService<PrepaymentEntryDTO> prepaymentEntryBatchService;

    public PrepaymentEntryCompilationWriter(final BatchService<PrepaymentEntryDTO> prepaymentEntryBatchService) {
        this.prepaymentEntryBatchService = prepaymentEntryBatchService;
    }

    @Override
    public void write(final List<? extends List<PrepaymentEntryDTO>> deserializedEntries) throws Exception {

        prepaymentEntryBatchService.save(deserializedEntries.stream().flatMap(Collection::stream).collect(ImmutableList.toImmutableList()));
    }
}
