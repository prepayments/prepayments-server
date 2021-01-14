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

import io.github.prepayments.domain.PrepsFileType;
import io.github.prepayments.domain.*; // for static metamodels
import io.github.prepayments.repository.PrepsFileTypeRepository;
import io.github.prepayments.repository.search.PrepsFileTypeSearchRepository;
import io.github.prepayments.service.dto.PrepsFileTypeCriteria;

/**
 * Service for executing complex queries for {@link PrepsFileType} entities in the database.
 * The main input is a {@link PrepsFileTypeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PrepsFileType} or a {@link Page} of {@link PrepsFileType} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PrepsFileTypeQueryService extends QueryService<PrepsFileType> {

    private final Logger log = LoggerFactory.getLogger(PrepsFileTypeQueryService.class);

    private final PrepsFileTypeRepository prepsFileTypeRepository;

    private final PrepsFileTypeSearchRepository prepsFileTypeSearchRepository;

    public PrepsFileTypeQueryService(PrepsFileTypeRepository prepsFileTypeRepository, PrepsFileTypeSearchRepository prepsFileTypeSearchRepository) {
        this.prepsFileTypeRepository = prepsFileTypeRepository;
        this.prepsFileTypeSearchRepository = prepsFileTypeSearchRepository;
    }

    /**
     * Return a {@link List} of {@link PrepsFileType} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PrepsFileType> findByCriteria(PrepsFileTypeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<PrepsFileType> specification = createSpecification(criteria);
        return prepsFileTypeRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link PrepsFileType} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PrepsFileType> findByCriteria(PrepsFileTypeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PrepsFileType> specification = createSpecification(criteria);
        return prepsFileTypeRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PrepsFileTypeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<PrepsFileType> specification = createSpecification(criteria);
        return prepsFileTypeRepository.count(specification);
    }

    /**
     * Function to convert {@link PrepsFileTypeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PrepsFileType> createSpecification(PrepsFileTypeCriteria criteria) {
        Specification<PrepsFileType> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), PrepsFileType_.id));
            }
            if (criteria.getPrepsFileTypeName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPrepsFileTypeName(), PrepsFileType_.prepsFileTypeName));
            }
            if (criteria.getPrepsFileMediumType() != null) {
                specification = specification.and(buildSpecification(criteria.getPrepsFileMediumType(), PrepsFileType_.prepsFileMediumType));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), PrepsFileType_.description));
            }
            if (criteria.getPrepsfileType() != null) {
                specification = specification.and(buildSpecification(criteria.getPrepsfileType(), PrepsFileType_.prepsfileType));
            }
        }
        return specification;
    }
}
