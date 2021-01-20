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

import io.github.prepayments.domain.CompilationRequest;
import io.github.prepayments.domain.*; // for static metamodels
import io.github.prepayments.repository.CompilationRequestRepository;
import io.github.prepayments.repository.search.CompilationRequestSearchRepository;
import io.github.prepayments.service.dto.CompilationRequestCriteria;
import io.github.prepayments.service.dto.CompilationRequestDTO;
import io.github.prepayments.service.mapper.CompilationRequestMapper;

/**
 * Service for executing complex queries for {@link CompilationRequest} entities in the database.
 * The main input is a {@link CompilationRequestCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CompilationRequestDTO} or a {@link Page} of {@link CompilationRequestDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CompilationRequestQueryService extends QueryService<CompilationRequest> {

    private final Logger log = LoggerFactory.getLogger(CompilationRequestQueryService.class);

    private final CompilationRequestRepository compilationRequestRepository;

    private final CompilationRequestMapper compilationRequestMapper;

    private final CompilationRequestSearchRepository compilationRequestSearchRepository;

    public CompilationRequestQueryService(CompilationRequestRepository compilationRequestRepository, CompilationRequestMapper compilationRequestMapper, CompilationRequestSearchRepository compilationRequestSearchRepository) {
        this.compilationRequestRepository = compilationRequestRepository;
        this.compilationRequestMapper = compilationRequestMapper;
        this.compilationRequestSearchRepository = compilationRequestSearchRepository;
    }

    /**
     * Return a {@link List} of {@link CompilationRequestDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CompilationRequestDTO> findByCriteria(CompilationRequestCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<CompilationRequest> specification = createSpecification(criteria);
        return compilationRequestMapper.toDto(compilationRequestRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link CompilationRequestDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CompilationRequestDTO> findByCriteria(CompilationRequestCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CompilationRequest> specification = createSpecification(criteria);
        return compilationRequestRepository.findAll(specification, page)
            .map(compilationRequestMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CompilationRequestCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<CompilationRequest> specification = createSpecification(criteria);
        return compilationRequestRepository.count(specification);
    }

    /**
     * Function to convert {@link CompilationRequestCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CompilationRequest> createSpecification(CompilationRequestCriteria criteria) {
        Specification<CompilationRequest> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), CompilationRequest_.id));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), CompilationRequest_.description));
            }
            if (criteria.getFileUploadId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFileUploadId(), CompilationRequest_.fileUploadId));
            }
            if (criteria.getCompilationStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getCompilationStatus(), CompilationRequest_.compilationStatus));
            }
            if (criteria.getCompilationType() != null) {
                specification = specification.and(buildSpecification(criteria.getCompilationType(), CompilationRequest_.compilationType));
            }
            if (criteria.getCompilationToken() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCompilationToken(), CompilationRequest_.compilationToken));
            }
        }
        return specification;
    }
}
