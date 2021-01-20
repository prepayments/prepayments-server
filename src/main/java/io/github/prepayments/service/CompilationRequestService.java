package io.github.prepayments.service;

import io.github.prepayments.service.dto.CompilationRequestDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link io.github.prepayments.domain.CompilationRequest}.
 */
public interface CompilationRequestService {

    /**
     * Save a compilationRequest.
     *
     * @param compilationRequestDTO the entity to save.
     * @return the persisted entity.
     */
    CompilationRequestDTO save(CompilationRequestDTO compilationRequestDTO);

    /**
     * Get all the compilationRequests.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CompilationRequestDTO> findAll(Pageable pageable);


    /**
     * Get the "id" compilationRequest.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CompilationRequestDTO> findOne(Long id);

    /**
     * Delete the "id" compilationRequest.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the compilationRequest corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CompilationRequestDTO> search(String query, Pageable pageable);
}
