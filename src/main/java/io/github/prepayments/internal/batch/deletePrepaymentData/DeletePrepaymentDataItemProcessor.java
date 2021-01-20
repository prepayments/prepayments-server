package io.github.prepayments.internal.batch.deletePrepaymentData;

import io.github.prepayments.domain.PrepaymentData;
import io.github.prepayments.service.dto.PrepaymentDataDTO;
import io.github.prepayments.service.mapper.PrepaymentDataMapper;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;

/**
 * This class takes a list of items to delete them from the database
 */
public class DeletePrepaymentDataItemProcessor implements ItemProcessor<List<PrepaymentDataDTO>, List<PrepaymentData>> {

    private final PrepaymentDataMapper prepaymentDataMapper;

    public DeletePrepaymentDataItemProcessor(final PrepaymentDataMapper prepaymentDataMapper) {
        this.prepaymentDataMapper = prepaymentDataMapper;
    }

    @Override
    public List<PrepaymentData> process(final List<PrepaymentDataDTO> deletable) throws Exception {
        return prepaymentDataMapper.toEntity(deletable);
    }
}
