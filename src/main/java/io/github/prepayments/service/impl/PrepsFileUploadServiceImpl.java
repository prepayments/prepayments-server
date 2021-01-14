package io.github.prepayments.service.impl;

import io.github.prepayments.service.PrepsFileUploadService;
import io.github.prepayments.domain.PrepsFileUpload;
import io.github.prepayments.repository.PrepsFileUploadRepository;
import io.github.prepayments.repository.search.PrepsFileUploadSearchRepository;
import io.github.prepayments.service.dto.PrepsFileUploadDTO;
import io.github.prepayments.service.mapper.PrepsFileUploadMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link PrepsFileUpload}.
 */
@Service
@Transactional
public class PrepsFileUploadServiceImpl implements PrepsFileUploadService {

    private final Logger log = LoggerFactory.getLogger(PrepsFileUploadServiceImpl.class);

    private final PrepsFileUploadRepository prepsFileUploadRepository;

    private final PrepsFileUploadMapper prepsFileUploadMapper;

    private final PrepsFileUploadSearchRepository prepsFileUploadSearchRepository;

    public PrepsFileUploadServiceImpl(PrepsFileUploadRepository prepsFileUploadRepository, PrepsFileUploadMapper prepsFileUploadMapper, PrepsFileUploadSearchRepository prepsFileUploadSearchRepository) {
        this.prepsFileUploadRepository = prepsFileUploadRepository;
        this.prepsFileUploadMapper = prepsFileUploadMapper;
        this.prepsFileUploadSearchRepository = prepsFileUploadSearchRepository;
    }

    @Override
    public PrepsFileUploadDTO save(PrepsFileUploadDTO prepsFileUploadDTO) {
        log.debug("Request to save PrepsFileUpload : {}", prepsFileUploadDTO);
        PrepsFileUpload prepsFileUpload = prepsFileUploadMapper.toEntity(prepsFileUploadDTO);
        prepsFileUpload = prepsFileUploadRepository.save(prepsFileUpload);
        PrepsFileUploadDTO result = prepsFileUploadMapper.toDto(prepsFileUpload);
        prepsFileUploadSearchRepository.save(prepsFileUpload);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PrepsFileUploadDTO> findAll(Pageable pageable) {
        log.debug("Request to get all PrepsFileUploads");
        return prepsFileUploadRepository.findAll(pageable)
            .map(prepsFileUploadMapper::toDto);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<PrepsFileUploadDTO> findOne(Long id) {
        log.debug("Request to get PrepsFileUpload : {}", id);
        return prepsFileUploadRepository.findById(id)
            .map(prepsFileUploadMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete PrepsFileUpload : {}", id);
        prepsFileUploadRepository.deleteById(id);
        prepsFileUploadSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PrepsFileUploadDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of PrepsFileUploads for query {}", query);
        return prepsFileUploadSearchRepository.search(queryStringQuery(query), pageable)
            .map(prepsFileUploadMapper::toDto);
    }
}
