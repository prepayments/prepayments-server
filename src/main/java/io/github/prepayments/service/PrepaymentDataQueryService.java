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

import io.github.prepayments.domain.PrepaymentData;
import io.github.prepayments.domain.*; // for static metamodels
import io.github.prepayments.repository.PrepaymentDataRepository;
import io.github.prepayments.repository.search.PrepaymentDataSearchRepository;
import io.github.prepayments.service.dto.PrepaymentDataCriteria;
import io.github.prepayments.service.dto.PrepaymentDataDTO;
import io.github.prepayments.service.mapper.PrepaymentDataMapper;

/**
 * Service for executing complex queries for {@link PrepaymentData} entities in the database.
 * The main input is a {@link PrepaymentDataCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PrepaymentDataDTO} or a {@link Page} of {@link PrepaymentDataDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PrepaymentDataQueryService extends QueryService<PrepaymentData> {

    private final Logger log = LoggerFactory.getLogger(PrepaymentDataQueryService.class);

    private final PrepaymentDataRepository prepaymentDataRepository;

    private final PrepaymentDataMapper prepaymentDataMapper;

    private final PrepaymentDataSearchRepository prepaymentDataSearchRepository;

    public PrepaymentDataQueryService(PrepaymentDataRepository prepaymentDataRepository, PrepaymentDataMapper prepaymentDataMapper, PrepaymentDataSearchRepository prepaymentDataSearchRepository) {
        this.prepaymentDataRepository = prepaymentDataRepository;
        this.prepaymentDataMapper = prepaymentDataMapper;
        this.prepaymentDataSearchRepository = prepaymentDataSearchRepository;
    }

    /**
     * Return a {@link List} of {@link PrepaymentDataDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PrepaymentDataDTO> findByCriteria(PrepaymentDataCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<PrepaymentData> specification = createSpecification(criteria);
        return prepaymentDataMapper.toDto(prepaymentDataRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PrepaymentDataDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PrepaymentDataDTO> findByCriteria(PrepaymentDataCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PrepaymentData> specification = createSpecification(criteria);
        return prepaymentDataRepository.findAll(specification, page)
            .map(prepaymentDataMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PrepaymentDataCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<PrepaymentData> specification = createSpecification(criteria);
        return prepaymentDataRepository.count(specification);
    }

    /**
     * Function to convert {@link PrepaymentDataCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PrepaymentData> createSpecification(PrepaymentDataCriteria criteria) {
        Specification<PrepaymentData> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), PrepaymentData_.id));
            }
            if (criteria.getAccountName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAccountName(), PrepaymentData_.accountName));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), PrepaymentData_.description));
            }
            if (criteria.getAccountNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAccountNumber(), PrepaymentData_.accountNumber));
            }
            if (criteria.getExpenseAccountNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getExpenseAccountNumber(), PrepaymentData_.expenseAccountNumber));
            }
            if (criteria.getPrepaymentNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPrepaymentNumber(), PrepaymentData_.prepaymentNumber));
            }
            if (criteria.getPrepaymentDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrepaymentDate(), PrepaymentData_.prepaymentDate));
            }
            if (criteria.getPrepaymentAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrepaymentAmount(), PrepaymentData_.prepaymentAmount));
            }
            if (criteria.getPrepaymentPeriods() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrepaymentPeriods(), PrepaymentData_.prepaymentPeriods));
            }
        }
        return specification;
    }
}
