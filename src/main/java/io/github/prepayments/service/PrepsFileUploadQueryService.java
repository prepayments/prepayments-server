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

import io.github.prepayments.domain.PrepsFileUpload;
import io.github.prepayments.domain.*; // for static metamodels
import io.github.prepayments.repository.PrepsFileUploadRepository;
import io.github.prepayments.repository.search.PrepsFileUploadSearchRepository;
import io.github.prepayments.service.dto.PrepsFileUploadCriteria;
import io.github.prepayments.service.dto.PrepsFileUploadDTO;
import io.github.prepayments.service.mapper.PrepsFileUploadMapper;

/**
 * Service for executing complex queries for {@link PrepsFileUpload} entities in the database.
 * The main input is a {@link PrepsFileUploadCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PrepsFileUploadDTO} or a {@link Page} of {@link PrepsFileUploadDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PrepsFileUploadQueryService extends QueryService<PrepsFileUpload> {

    private final Logger log = LoggerFactory.getLogger(PrepsFileUploadQueryService.class);

    private final PrepsFileUploadRepository prepsFileUploadRepository;

    private final PrepsFileUploadMapper prepsFileUploadMapper;

    private final PrepsFileUploadSearchRepository prepsFileUploadSearchRepository;

    public PrepsFileUploadQueryService(PrepsFileUploadRepository prepsFileUploadRepository, PrepsFileUploadMapper prepsFileUploadMapper, PrepsFileUploadSearchRepository prepsFileUploadSearchRepository) {
        this.prepsFileUploadRepository = prepsFileUploadRepository;
        this.prepsFileUploadMapper = prepsFileUploadMapper;
        this.prepsFileUploadSearchRepository = prepsFileUploadSearchRepository;
    }

    /**
     * Return a {@link List} of {@link PrepsFileUploadDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PrepsFileUploadDTO> findByCriteria(PrepsFileUploadCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<PrepsFileUpload> specification = createSpecification(criteria);
        return prepsFileUploadMapper.toDto(prepsFileUploadRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PrepsFileUploadDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PrepsFileUploadDTO> findByCriteria(PrepsFileUploadCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PrepsFileUpload> specification = createSpecification(criteria);
        return prepsFileUploadRepository.findAll(specification, page)
            .map(prepsFileUploadMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PrepsFileUploadCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<PrepsFileUpload> specification = createSpecification(criteria);
        return prepsFileUploadRepository.count(specification);
    }

    /**
     * Function to convert {@link PrepsFileUploadCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PrepsFileUpload> createSpecification(PrepsFileUploadCriteria criteria) {
        Specification<PrepsFileUpload> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), PrepsFileUpload_.id));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), PrepsFileUpload_.description));
            }
            if (criteria.getFileName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFileName(), PrepsFileUpload_.fileName));
            }
            if (criteria.getPeriodFrom() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPeriodFrom(), PrepsFileUpload_.periodFrom));
            }
            if (criteria.getPeriodTo() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPeriodTo(), PrepsFileUpload_.periodTo));
            }
            if (criteria.getPrepsFileTypeId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrepsFileTypeId(), PrepsFileUpload_.prepsFileTypeId));
            }
            if (criteria.getUploadSuccessful() != null) {
                specification = specification.and(buildSpecification(criteria.getUploadSuccessful(), PrepsFileUpload_.uploadSuccessful));
            }
            if (criteria.getUploadProcessed() != null) {
                specification = specification.and(buildSpecification(criteria.getUploadProcessed(), PrepsFileUpload_.uploadProcessed));
            }
            if (criteria.getUploadToken() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUploadToken(), PrepsFileUpload_.uploadToken));
            }
        }
        return specification;
    }
}
