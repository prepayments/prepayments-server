package io.github.prepayments.internal.service;

import com.google.common.collect.ImmutableList;
import io.github.jhipster.service.filter.StringFilter;
import io.github.prepayments.domain.PrepaymentData;
import io.github.prepayments.repository.PrepaymentDataRepository;
import io.github.prepayments.repository.search.PrepaymentDataSearchRepository;
import io.github.prepayments.repository.search.PrepaymentEntrySearchRepository;
import io.github.prepayments.service.PrepaymentDataQueryService;
import io.github.prepayments.service.dto.PrepaymentDataCriteria;
import io.github.prepayments.service.dto.PrepsFileUploadDTO;
import io.github.prepayments.service.mapper.PrepaymentDataMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This service deletes prepayments-data that has been updated with the file-upload of a given Id
 */
@Service("prepaymentsDataDeletionBatchService")
public class PrepaymentsDataDeletionBatchService implements BatchService<PrepaymentData> {

    private final PrepaymentDataRepository prepaymentDataRepository;
    private final PrepaymentDataSearchRepository prepaymentDataSearchRepository;

    public PrepaymentsDataDeletionBatchService(final PrepaymentDataRepository prepaymentDataRepository, final PrepaymentDataSearchRepository prepaymentDataSearchRepository) {
        this.prepaymentDataRepository = prepaymentDataRepository;
        this.prepaymentDataSearchRepository = prepaymentDataSearchRepository;
    }

    /**
     * Deletes the items in the stream in a batch
     *
     * @param entities entity to save.
     * @return
     */
    @Override
    public List<PrepaymentData> save(final List<PrepaymentData> entities) {

        prepaymentDataRepository.deleteAll(entities);

        return ImmutableList.copyOf(entities);
    }

    /**
     * Removes items in the list from the search index in a batch
     * @param entities
     */
    @Override
    public void index(final List<PrepaymentData> entities) {

        prepaymentDataSearchRepository.deleteAll(entities);
    }
}
