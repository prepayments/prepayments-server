package io.github.prepayments.internal.batch.deletePrepaymentData;

import com.google.common.collect.ImmutableList;
import io.github.prepayments.domain.PrepaymentData;
import io.github.prepayments.internal.service.BatchService;
import org.springframework.batch.item.ItemWriter;

import java.util.Collection;
import java.util.List;

/**
 * This class receives a list of items for deletion and will then proceed to delete them
 */
public class DeletePrepaymentDataItemWriter implements ItemWriter<List<PrepaymentData>> {

    private final BatchService<PrepaymentData> prepaymentsDataDeletionBatchService;

    public DeletePrepaymentDataItemWriter(final BatchService<PrepaymentData> prepaymentsDataDeletionBatchService) {
        this.prepaymentsDataDeletionBatchService = prepaymentsDataDeletionBatchService;
    }

    @Override
    public void write(final List<? extends List<PrepaymentData>> deletable) throws Exception {

        prepaymentsDataDeletionBatchService.save(deletable.stream().flatMap(Collection::stream).collect(ImmutableList.toImmutableList()));

        prepaymentsDataDeletionBatchService.index(deletable.stream().flatMap(Collection::stream).collect(ImmutableList.toImmutableList()));
    }
}
