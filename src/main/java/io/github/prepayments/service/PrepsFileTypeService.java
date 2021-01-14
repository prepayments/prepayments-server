package io.github.prepayments.service;

import io.github.prepayments.domain.PrepsFileType;
import io.github.prepayments.repository.PrepsFileTypeRepository;
import io.github.prepayments.repository.search.PrepsFileTypeSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link PrepsFileType}.
 */
@Service
@Transactional
public class PrepsFileTypeService {

    private final Logger log = LoggerFactory.getLogger(PrepsFileTypeService.class);

    private final PrepsFileTypeRepository prepsFileTypeRepository;

    private final PrepsFileTypeSearchRepository prepsFileTypeSearchRepository;

    public PrepsFileTypeService(PrepsFileTypeRepository prepsFileTypeRepository, PrepsFileTypeSearchRepository prepsFileTypeSearchRepository) {
        this.prepsFileTypeRepository = prepsFileTypeRepository;
        this.prepsFileTypeSearchRepository = prepsFileTypeSearchRepository;
    }

    /**
     * Save a prepsFileType.
     *
     * @param prepsFileType the entity to save.
     * @return the persisted entity.
     */
    public PrepsFileType save(PrepsFileType prepsFileType) {
        log.debug("Request to save PrepsFileType : {}", prepsFileType);
        PrepsFileType result = prepsFileTypeRepository.save(prepsFileType);
        prepsFileTypeSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the prepsFileTypes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PrepsFileType> findAll(Pageable pageable) {
        log.debug("Request to get all PrepsFileTypes");
        return prepsFileTypeRepository.findAll(pageable);
    }


    /**
     * Get one prepsFileType by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PrepsFileType> findOne(Long id) {
        log.debug("Request to get PrepsFileType : {}", id);
        return prepsFileTypeRepository.findById(id);
    }

    /**
     * Delete the prepsFileType by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete PrepsFileType : {}", id);
        prepsFileTypeRepository.deleteById(id);
        prepsFileTypeSearchRepository.deleteById(id);
    }

    /**
     * Search for the prepsFileType corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PrepsFileType> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of PrepsFileTypes for query {}", query);
        return prepsFileTypeSearchRepository.search(queryStringQuery(query), pageable);    }
}
