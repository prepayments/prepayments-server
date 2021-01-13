package io.github.prepayments.service.impl;

import io.github.prepayments.service.PrepaymentDataService;
import io.github.prepayments.domain.PrepaymentData;
import io.github.prepayments.repository.PrepaymentDataRepository;
import io.github.prepayments.repository.search.PrepaymentDataSearchRepository;
import io.github.prepayments.service.dto.PrepaymentDataDTO;
import io.github.prepayments.service.mapper.PrepaymentDataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link PrepaymentData}.
 */
@Service
@Transactional
public class PrepaymentDataServiceImpl implements PrepaymentDataService {

    private final Logger log = LoggerFactory.getLogger(PrepaymentDataServiceImpl.class);

    private final PrepaymentDataRepository prepaymentDataRepository;

    private final PrepaymentDataMapper prepaymentDataMapper;

    private final PrepaymentDataSearchRepository prepaymentDataSearchRepository;

    public PrepaymentDataServiceImpl(PrepaymentDataRepository prepaymentDataRepository, PrepaymentDataMapper prepaymentDataMapper, PrepaymentDataSearchRepository prepaymentDataSearchRepository) {
        this.prepaymentDataRepository = prepaymentDataRepository;
        this.prepaymentDataMapper = prepaymentDataMapper;
        this.prepaymentDataSearchRepository = prepaymentDataSearchRepository;
    }

    @Override
    public PrepaymentDataDTO save(PrepaymentDataDTO prepaymentDataDTO) {
        log.debug("Request to save PrepaymentData : {}", prepaymentDataDTO);
        PrepaymentData prepaymentData = prepaymentDataMapper.toEntity(prepaymentDataDTO);
        prepaymentData = prepaymentDataRepository.save(prepaymentData);
        PrepaymentDataDTO result = prepaymentDataMapper.toDto(prepaymentData);
        prepaymentDataSearchRepository.save(prepaymentData);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PrepaymentDataDTO> findAll(Pageable pageable) {
        log.debug("Request to get all PrepaymentData");
        return prepaymentDataRepository.findAll(pageable)
            .map(prepaymentDataMapper::toDto);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<PrepaymentDataDTO> findOne(Long id) {
        log.debug("Request to get PrepaymentData : {}", id);
        return prepaymentDataRepository.findById(id)
            .map(prepaymentDataMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete PrepaymentData : {}", id);
        prepaymentDataRepository.deleteById(id);
        prepaymentDataSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PrepaymentDataDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of PrepaymentData for query {}", query);
        return prepaymentDataSearchRepository.search(queryStringQuery(query), pageable)
            .map(prepaymentDataMapper::toDto);
    }
}
