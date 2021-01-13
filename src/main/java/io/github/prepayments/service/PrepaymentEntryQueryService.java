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

import io.github.prepayments.domain.PrepaymentEntry;
import io.github.prepayments.domain.*; // for static metamodels
import io.github.prepayments.repository.PrepaymentEntryRepository;
import io.github.prepayments.repository.search.PrepaymentEntrySearchRepository;
import io.github.prepayments.service.dto.PrepaymentEntryCriteria;
import io.github.prepayments.service.dto.PrepaymentEntryDTO;
import io.github.prepayments.service.mapper.PrepaymentEntryMapper;

/**
 * Service for executing complex queries for {@link PrepaymentEntry} entities in the database.
 * The main input is a {@link PrepaymentEntryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PrepaymentEntryDTO} or a {@link Page} of {@link PrepaymentEntryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PrepaymentEntryQueryService extends QueryService<PrepaymentEntry> {

    private final Logger log = LoggerFactory.getLogger(PrepaymentEntryQueryService.class);

    private final PrepaymentEntryRepository prepaymentEntryRepository;

    private final PrepaymentEntryMapper prepaymentEntryMapper;

    private final PrepaymentEntrySearchRepository prepaymentEntrySearchRepository;

    public PrepaymentEntryQueryService(PrepaymentEntryRepository prepaymentEntryRepository, PrepaymentEntryMapper prepaymentEntryMapper, PrepaymentEntrySearchRepository prepaymentEntrySearchRepository) {
        this.prepaymentEntryRepository = prepaymentEntryRepository;
        this.prepaymentEntryMapper = prepaymentEntryMapper;
        this.prepaymentEntrySearchRepository = prepaymentEntrySearchRepository;
    }

    /**
     * Return a {@link List} of {@link PrepaymentEntryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PrepaymentEntryDTO> findByCriteria(PrepaymentEntryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<PrepaymentEntry> specification = createSpecification(criteria);
        return prepaymentEntryMapper.toDto(prepaymentEntryRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PrepaymentEntryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PrepaymentEntryDTO> findByCriteria(PrepaymentEntryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PrepaymentEntry> specification = createSpecification(criteria);
        return prepaymentEntryRepository.findAll(specification, page)
            .map(prepaymentEntryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PrepaymentEntryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<PrepaymentEntry> specification = createSpecification(criteria);
        return prepaymentEntryRepository.count(specification);
    }

    /**
     * Function to convert {@link PrepaymentEntryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PrepaymentEntry> createSpecification(PrepaymentEntryCriteria criteria) {
        Specification<PrepaymentEntry> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), PrepaymentEntry_.id));
            }
            if (criteria.getAccountName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAccountName(), PrepaymentEntry_.accountName));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), PrepaymentEntry_.description));
            }
            if (criteria.getAccountNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAccountNumber(), PrepaymentEntry_.accountNumber));
            }
            if (criteria.getPrepaymentNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPrepaymentNumber(), PrepaymentEntry_.prepaymentNumber));
            }
            if (criteria.getPrepaymentDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrepaymentDate(), PrepaymentEntry_.prepaymentDate));
            }
            if (criteria.getTransactionAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTransactionAmount(), PrepaymentEntry_.transactionAmount));
            }
        }
        return specification;
    }
}
