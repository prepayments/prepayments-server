package io.github.prepayments.service.impl;

import io.github.prepayments.service.CompilationRequestService;
import io.github.prepayments.domain.CompilationRequest;
import io.github.prepayments.repository.CompilationRequestRepository;
import io.github.prepayments.repository.search.CompilationRequestSearchRepository;
import io.github.prepayments.service.dto.CompilationRequestDTO;
import io.github.prepayments.service.mapper.CompilationRequestMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link CompilationRequest}.
 */
@Service
@Transactional
public class CompilationRequestServiceImpl implements CompilationRequestService {

    private final Logger log = LoggerFactory.getLogger(CompilationRequestServiceImpl.class);

    private final CompilationRequestRepository compilationRequestRepository;

    private final CompilationRequestMapper compilationRequestMapper;

    private final CompilationRequestSearchRepository compilationRequestSearchRepository;

    public CompilationRequestServiceImpl(CompilationRequestRepository compilationRequestRepository, CompilationRequestMapper compilationRequestMapper, CompilationRequestSearchRepository compilationRequestSearchRepository) {
        this.compilationRequestRepository = compilationRequestRepository;
        this.compilationRequestMapper = compilationRequestMapper;
        this.compilationRequestSearchRepository = compilationRequestSearchRepository;
    }

    @Override
    public CompilationRequestDTO save(CompilationRequestDTO compilationRequestDTO) {
        log.debug("Request to save CompilationRequest : {}", compilationRequestDTO);
        CompilationRequest compilationRequest = compilationRequestMapper.toEntity(compilationRequestDTO);
        compilationRequest = compilationRequestRepository.save(compilationRequest);
        CompilationRequestDTO result = compilationRequestMapper.toDto(compilationRequest);
        compilationRequestSearchRepository.save(compilationRequest);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompilationRequestDTO> findAll(Pageable pageable) {
        log.debug("Request to get all CompilationRequests");
        return compilationRequestRepository.findAll(pageable)
            .map(compilationRequestMapper::toDto);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<CompilationRequestDTO> findOne(Long id) {
        log.debug("Request to get CompilationRequest : {}", id);
        return compilationRequestRepository.findById(id)
            .map(compilationRequestMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete CompilationRequest : {}", id);
        compilationRequestRepository.deleteById(id);
        compilationRequestSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompilationRequestDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of CompilationRequests for query {}", query);
        return compilationRequestSearchRepository.search(queryStringQuery(query), pageable)
            .map(compilationRequestMapper::toDto);
    }
}
