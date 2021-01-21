package io.github.prepayments.internal.service;

import io.github.prepayments.repository.PrepaymentEntryRepository;
import io.github.prepayments.repository.search.PrepaymentEntrySearchRepository;
import io.github.prepayments.service.dto.PrepaymentEntryDTO;
import io.github.prepayments.service.mapper.PrepaymentEntryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("prepaymentEntryBatchService")
public class PrepaymentEntryBatchService implements BatchService<PrepaymentEntryDTO> {

    private final PrepaymentEntryMapper prepaymentEntryMapper;
    private final PrepaymentEntryRepository prepaymentEntryRepository;
    private final PrepaymentEntrySearchRepository prepaymentEntrySearchRepository;

    public PrepaymentEntryBatchService(final PrepaymentEntryMapper prepaymentEntryMapper, final PrepaymentEntryRepository prepaymentEntryRepository,
                                       final PrepaymentEntrySearchRepository prepaymentEntrySearchRepository) {
        this.prepaymentEntryMapper = prepaymentEntryMapper;
        this.prepaymentEntryRepository = prepaymentEntryRepository;
        this.prepaymentEntrySearchRepository = prepaymentEntrySearchRepository;
    }

    @Override
    public List<PrepaymentEntryDTO> save(final List<PrepaymentEntryDTO> entities) {
        return prepaymentEntryMapper.toDto(prepaymentEntryRepository.saveAll(prepaymentEntryMapper.toEntity(entities)));
    }

    @Override
    public void index(final List<PrepaymentEntryDTO> entities) {

        prepaymentEntrySearchRepository.saveAll(prepaymentEntryMapper.toEntity(entities));
    }
}
