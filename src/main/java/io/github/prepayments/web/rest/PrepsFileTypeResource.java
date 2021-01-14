package io.github.prepayments.web.rest;

import io.github.prepayments.domain.PrepsFileType;
import io.github.prepayments.service.PrepsFileTypeService;
import io.github.prepayments.web.rest.errors.BadRequestAlertException;
import io.github.prepayments.service.dto.PrepsFileTypeCriteria;
import io.github.prepayments.service.PrepsFileTypeQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link io.github.prepayments.domain.PrepsFileType}.
 */
@RestController
@RequestMapping("/api")
public class PrepsFileTypeResource {

    private final Logger log = LoggerFactory.getLogger(PrepsFileTypeResource.class);

    private static final String ENTITY_NAME = "prepsPrepsFileType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PrepsFileTypeService prepsFileTypeService;

    private final PrepsFileTypeQueryService prepsFileTypeQueryService;

    public PrepsFileTypeResource(PrepsFileTypeService prepsFileTypeService, PrepsFileTypeQueryService prepsFileTypeQueryService) {
        this.prepsFileTypeService = prepsFileTypeService;
        this.prepsFileTypeQueryService = prepsFileTypeQueryService;
    }

    /**
     * {@code POST  /preps-file-types} : Create a new prepsFileType.
     *
     * @param prepsFileType the prepsFileType to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new prepsFileType, or with status {@code 400 (Bad Request)} if the prepsFileType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/preps-file-types")
    public ResponseEntity<PrepsFileType> createPrepsFileType(@Valid @RequestBody PrepsFileType prepsFileType) throws URISyntaxException {
        log.debug("REST request to save PrepsFileType : {}", prepsFileType);
        if (prepsFileType.getId() != null) {
            throw new BadRequestAlertException("A new prepsFileType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PrepsFileType result = prepsFileTypeService.save(prepsFileType);
        return ResponseEntity.created(new URI("/api/preps-file-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /preps-file-types} : Updates an existing prepsFileType.
     *
     * @param prepsFileType the prepsFileType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated prepsFileType,
     * or with status {@code 400 (Bad Request)} if the prepsFileType is not valid,
     * or with status {@code 500 (Internal Server Error)} if the prepsFileType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/preps-file-types")
    public ResponseEntity<PrepsFileType> updatePrepsFileType(@Valid @RequestBody PrepsFileType prepsFileType) throws URISyntaxException {
        log.debug("REST request to update PrepsFileType : {}", prepsFileType);
        if (prepsFileType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        PrepsFileType result = prepsFileTypeService.save(prepsFileType);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, prepsFileType.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /preps-file-types} : get all the prepsFileTypes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of prepsFileTypes in body.
     */
    @GetMapping("/preps-file-types")
    public ResponseEntity<List<PrepsFileType>> getAllPrepsFileTypes(PrepsFileTypeCriteria criteria, Pageable pageable) {
        log.debug("REST request to get PrepsFileTypes by criteria: {}", criteria);
        Page<PrepsFileType> page = prepsFileTypeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /preps-file-types/count} : count all the prepsFileTypes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/preps-file-types/count")
    public ResponseEntity<Long> countPrepsFileTypes(PrepsFileTypeCriteria criteria) {
        log.debug("REST request to count PrepsFileTypes by criteria: {}", criteria);
        return ResponseEntity.ok().body(prepsFileTypeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /preps-file-types/:id} : get the "id" prepsFileType.
     *
     * @param id the id of the prepsFileType to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the prepsFileType, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/preps-file-types/{id}")
    public ResponseEntity<PrepsFileType> getPrepsFileType(@PathVariable Long id) {
        log.debug("REST request to get PrepsFileType : {}", id);
        Optional<PrepsFileType> prepsFileType = prepsFileTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(prepsFileType);
    }

    /**
     * {@code DELETE  /preps-file-types/:id} : delete the "id" prepsFileType.
     *
     * @param id the id of the prepsFileType to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/preps-file-types/{id}")
    public ResponseEntity<Void> deletePrepsFileType(@PathVariable Long id) {
        log.debug("REST request to delete PrepsFileType : {}", id);
        prepsFileTypeService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/preps-file-types?query=:query} : search for the prepsFileType corresponding
     * to the query.
     *
     * @param query the query of the prepsFileType search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/preps-file-types")
    public ResponseEntity<List<PrepsFileType>> searchPrepsFileTypes(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of PrepsFileTypes for query {}", query);
        Page<PrepsFileType> page = prepsFileTypeService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
}
