package io.github.prepayments.internal.service;

import io.github.prepayments.repository.AmortizationEntryRepository;
import io.github.prepayments.repository.search.AmortizationEntrySearchRepository;
import io.github.prepayments.service.dto.AmortizationEntryDTO;
import io.github.prepayments.service.mapper.AmortizationEntryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("amortizationEntryBatchService")
public class AmortizationEntryBatchService implements BatchService<AmortizationEntryDTO> {

    private final AmortizationEntryMapper amortizationEntryMapper;
    private final AmortizationEntryRepository amortizationEntryRepository;
    private final AmortizationEntrySearchRepository amortizationEntrySearchRepository;

    public AmortizationEntryBatchService(final AmortizationEntryMapper amortizationEntryMapper, final AmortizationEntryRepository amortizationEntryRepository,
                                         final AmortizationEntrySearchRepository amortizationEntrySearchRepository) {
        this.amortizationEntryMapper = amortizationEntryMapper;
        this.amortizationEntryRepository = amortizationEntryRepository;
        this.amortizationEntrySearchRepository = amortizationEntrySearchRepository;
    }

    @Override
    public List<AmortizationEntryDTO> save(final List<AmortizationEntryDTO> entities) {

        return amortizationEntryMapper.toDto(
            amortizationEntryRepository.saveAll(amortizationEntryMapper.toEntity(entities)));
    }

    @Override
    public void index(final List<AmortizationEntryDTO> entities) {

        amortizationEntrySearchRepository.saveAll(amortizationEntryMapper.toEntity(entities));
    }
}
