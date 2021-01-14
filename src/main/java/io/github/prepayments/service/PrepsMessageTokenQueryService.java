package io.github.prepayments.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import io.github.prepayments.domain.PrepsMessageToken;
import io.github.prepayments.domain.*; // for static metamodels
import io.github.prepayments.repository.PrepsMessageTokenRepository;
import io.github.prepayments.repository.search.PrepsMessageTokenSearchRepository;
import io.github.prepayments.service.dto.PrepsMessageTokenCriteria;
import io.github.prepayments.service.dto.PrepsMessageTokenDTO;
import io.github.prepayments.service.mapper.PrepsMessageTokenMapper;

/**
 * Service for executing complex queries for {@link PrepsMessageToken} entities in the database.
 * The main input is a {@link PrepsMessageTokenCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PrepsMessageTokenDTO} or a {@link Page} of {@link PrepsMessageTokenDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PrepsMessageTokenQueryService extends QueryService<PrepsMessageToken> {

    private final Logger log = LoggerFactory.getLogger(PrepsMessageTokenQueryService.class);

    private final PrepsMessageTokenRepository prepsMessageTokenRepository;

    private final PrepsMessageTokenMapper prepsMessageTokenMapper;

    private final PrepsMessageTokenSearchRepository prepsMessageTokenSearchRepository;

    public PrepsMessageTokenQueryService(PrepsMessageTokenRepository prepsMessageTokenRepository, PrepsMessageTokenMapper prepsMessageTokenMapper, PrepsMessageTokenSearchRepository prepsMessageTokenSearchRepository) {
        this.prepsMessageTokenRepository = prepsMessageTokenRepository;
        this.prepsMessageTokenMapper = prepsMessageTokenMapper;
        this.prepsMessageTokenSearchRepository = prepsMessageTokenSearchRepository;
    }

    /**
     * Return a {@link List} of {@link PrepsMessageTokenDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PrepsMessageTokenDTO> findByCriteria(PrepsMessageTokenCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<PrepsMessageToken> specification = createSpecification(criteria);
        return prepsMessageTokenMapper.toDto(prepsMessageTokenRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PrepsMessageTokenDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PrepsMessageTokenDTO> findByCriteria(PrepsMessageTokenCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PrepsMessageToken> specification = createSpecification(criteria);
        return prepsMessageTokenRepository.findAll(specification, page)
            .map(prepsMessageTokenMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PrepsMessageTokenCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<PrepsMessageToken> specification = createSpecification(criteria);
        return prepsMessageTokenRepository.count(specification);
    }

    /**
     * Function to convert {@link PrepsMessageTokenCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PrepsMessageToken> createSpecification(PrepsMessageTokenCriteria criteria) {
        Specification<PrepsMessageToken> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), PrepsMessageToken_.id));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), PrepsMessageToken_.description));
            }
            if (criteria.getTimeSent() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTimeSent(), PrepsMessageToken_.timeSent));
            }
            if (criteria.getTokenValue() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTokenValue(), PrepsMessageToken_.tokenValue));
            }
            if (criteria.getReceived() != null) {
                specification = specification.and(buildSpecification(criteria.getReceived(), PrepsMessageToken_.received));
            }
            if (criteria.getActioned() != null) {
                specification = specification.and(buildSpecification(criteria.getActioned(), PrepsMessageToken_.actioned));
            }
            if (criteria.getContentFullyEnqueued() != null) {
                specification = specification.and(buildSpecification(criteria.getContentFullyEnqueued(), PrepsMessageToken_.contentFullyEnqueued));
            }
        }
        return specification;
    }
}
