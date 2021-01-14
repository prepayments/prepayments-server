package io.github.prepayments.service;

import io.github.prepayments.service.dto.PrepsMessageTokenDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link io.github.prepayments.domain.PrepsMessageToken}.
 */
public interface PrepsMessageTokenService {

    /**
     * Save a prepsMessageToken.
     *
     * @param prepsMessageTokenDTO the entity to save.
     * @return the persisted entity.
     */
    PrepsMessageTokenDTO save(PrepsMessageTokenDTO prepsMessageTokenDTO);

    /**
     * Get all the prepsMessageTokens.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PrepsMessageTokenDTO> findAll(Pageable pageable);


    /**
     * Get the "id" prepsMessageToken.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PrepsMessageTokenDTO> findOne(Long id);

    /**
     * Delete the "id" prepsMessageToken.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the prepsMessageToken corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PrepsMessageTokenDTO> search(String query, Pageable pageable);
}
