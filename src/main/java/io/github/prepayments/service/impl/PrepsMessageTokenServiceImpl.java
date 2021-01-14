package io.github.prepayments.service.impl;

import io.github.prepayments.service.PrepsMessageTokenService;
import io.github.prepayments.domain.PrepsMessageToken;
import io.github.prepayments.repository.PrepsMessageTokenRepository;
import io.github.prepayments.repository.search.PrepsMessageTokenSearchRepository;
import io.github.prepayments.service.dto.PrepsMessageTokenDTO;
import io.github.prepayments.service.mapper.PrepsMessageTokenMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link PrepsMessageToken}.
 */
@Service
@Transactional
public class PrepsMessageTokenServiceImpl implements PrepsMessageTokenService {

    private final Logger log = LoggerFactory.getLogger(PrepsMessageTokenServiceImpl.class);

    private final PrepsMessageTokenRepository prepsMessageTokenRepository;

    private final PrepsMessageTokenMapper prepsMessageTokenMapper;

    private final PrepsMessageTokenSearchRepository prepsMessageTokenSearchRepository;

    public PrepsMessageTokenServiceImpl(PrepsMessageTokenRepository prepsMessageTokenRepository, PrepsMessageTokenMapper prepsMessageTokenMapper, PrepsMessageTokenSearchRepository prepsMessageTokenSearchRepository) {
        this.prepsMessageTokenRepository = prepsMessageTokenRepository;
        this.prepsMessageTokenMapper = prepsMessageTokenMapper;
        this.prepsMessageTokenSearchRepository = prepsMessageTokenSearchRepository;
    }

    @Override
    public PrepsMessageTokenDTO save(PrepsMessageTokenDTO prepsMessageTokenDTO) {
        log.debug("Request to save PrepsMessageToken : {}", prepsMessageTokenDTO);
        PrepsMessageToken prepsMessageToken = prepsMessageTokenMapper.toEntity(prepsMessageTokenDTO);
        prepsMessageToken = prepsMessageTokenRepository.save(prepsMessageToken);
        PrepsMessageTokenDTO result = prepsMessageTokenMapper.toDto(prepsMessageToken);
        prepsMessageTokenSearchRepository.save(prepsMessageToken);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PrepsMessageTokenDTO> findAll(Pageable pageable) {
        log.debug("Request to get all PrepsMessageTokens");
        return prepsMessageTokenRepository.findAll(pageable)
            .map(prepsMessageTokenMapper::toDto);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<PrepsMessageTokenDTO> findOne(Long id) {
        log.debug("Request to get PrepsMessageToken : {}", id);
        return prepsMessageTokenRepository.findById(id)
            .map(prepsMessageTokenMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete PrepsMessageToken : {}", id);
        prepsMessageTokenRepository.deleteById(id);
        prepsMessageTokenSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PrepsMessageTokenDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of PrepsMessageTokens for query {}", query);
        return prepsMessageTokenSearchRepository.search(queryStringQuery(query), pageable)
            .map(prepsMessageTokenMapper::toDto);
    }
}
