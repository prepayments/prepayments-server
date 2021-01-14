package io.github.prepayments.internal.service;

import io.github.prepayments.repository.PrepaymentDataRepository;
import io.github.prepayments.repository.search.PrepaymentDataSearchRepository;
import io.github.prepayments.service.dto.PrepaymentDataDTO;
import io.github.prepayments.service.mapper.PrepaymentDataMapper;

import java.util.List;

/**
 * BatchService implementation for prepayments-data model
 */
public class PrepaymentDataBatchService implements BatchService<PrepaymentDataDTO> {

    private final PrepaymentDataMapper prepaymentDataMapper;
    private final PrepaymentDataRepository prepaymentDataRepository;
    private final PrepaymentDataSearchRepository prepaymentDataSearchRepository;

    public PrepaymentDataBatchService(PrepaymentDataMapper prepaymentDataMapper, PrepaymentDataRepository prepaymentDataRepository, PrepaymentDataSearchRepository prepaymentDataSearchRepository) {
        this.prepaymentDataMapper = prepaymentDataMapper;
        this.prepaymentDataRepository = prepaymentDataRepository;
        this.prepaymentDataSearchRepository = prepaymentDataSearchRepository;
    }

    /**
     * Save an entity.
     *
     * @param entities entity to save.
     * @return the persisted entity.
     */
    @Override
    public List<PrepaymentDataDTO> save(List<PrepaymentDataDTO> entities) {
        return prepaymentDataMapper.toDto(prepaymentDataRepository.saveAll(prepaymentDataMapper.toEntity(entities)));
    }

    /**
     * The above call only persists entities to the relations db repository
     * for efficiency sake.
     * Therefore to have it all in an index one needs to call this function
     *
     * @param entities
     * @return
     */
    @Override
    public void index(List<PrepaymentDataDTO> entities) {

        prepaymentDataSearchRepository.saveAll(prepaymentDataMapper.toEntity(entities));
    }
}
