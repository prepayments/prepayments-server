package io.github.prepayments.service;

import io.github.prepayments.service.dto.PrepaymentDataDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link io.github.prepayments.domain.PrepaymentData}.
 */
public interface PrepaymentDataService {

    /**
     * Save a prepaymentData.
     *
     * @param prepaymentDataDTO the entity to save.
     * @return the persisted entity.
     */
    PrepaymentDataDTO save(PrepaymentDataDTO prepaymentDataDTO);

    /**
     * Get all the prepaymentData.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PrepaymentDataDTO> findAll(Pageable pageable);


    /**
     * Get the "id" prepaymentData.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PrepaymentDataDTO> findOne(Long id);

    /**
     * Delete the "id" prepaymentData.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the prepaymentData corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PrepaymentDataDTO> search(String query, Pageable pageable);
}
