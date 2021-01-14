package io.github.prepayments.service;

import io.github.prepayments.service.dto.PrepsFileUploadDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link io.github.prepayments.domain.PrepsFileUpload}.
 */
public interface PrepsFileUploadService {

    /**
     * Save a prepsFileUpload.
     *
     * @param prepsFileUploadDTO the entity to save.
     * @return the persisted entity.
     */
    PrepsFileUploadDTO save(PrepsFileUploadDTO prepsFileUploadDTO);

    /**
     * Get all the prepsFileUploads.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PrepsFileUploadDTO> findAll(Pageable pageable);


    /**
     * Get the "id" prepsFileUpload.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PrepsFileUploadDTO> findOne(Long id);

    /**
     * Delete the "id" prepsFileUpload.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the prepsFileUpload corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PrepsFileUploadDTO> search(String query, Pageable pageable);
}
