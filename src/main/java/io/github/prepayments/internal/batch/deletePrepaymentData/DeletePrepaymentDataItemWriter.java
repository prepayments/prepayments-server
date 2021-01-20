package io.github.prepayments.internal.batch.deletePrepaymentData;

import io.github.prepayments.domain.PrepaymentData;
import io.github.prepayments.repository.PrepaymentDataRepository;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * This class receives a list of items for deletion and will then proceed to delete them
 */
public class DeletePrepaymentDataItemWriter implements ItemWriter<List<PrepaymentData>> {

    private final PrepaymentDataRepository prepaymentDataRepository;

    public DeletePrepaymentDataItemWriter(final PrepaymentDataRepository prepaymentDataRepository) {
        this.prepaymentDataRepository = prepaymentDataRepository;
    }

    @Override
    public void write(final List<? extends List<PrepaymentData>> deletable) throws Exception {

        deletable.forEach(prepaymentDataRepository::deleteAll);
    }
}
