package io.github.prepayments.web.rest;

import io.github.prepayments.service.PrepsFileUploadService;
import io.github.prepayments.web.rest.errors.BadRequestAlertException;
import io.github.prepayments.service.dto.PrepsFileUploadDTO;
import io.github.prepayments.service.dto.PrepsFileUploadCriteria;
import io.github.prepayments.service.PrepsFileUploadQueryService;

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
 * REST controller for managing {@link io.github.prepayments.domain.PrepsFileUpload}.
 */
@RestController
@RequestMapping("/api")
public class PrepsFileUploadResource {

    private final Logger log = LoggerFactory.getLogger(PrepsFileUploadResource.class);

    private static final String ENTITY_NAME = "prepsPrepsFileUpload";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PrepsFileUploadService prepsFileUploadService;

    private final PrepsFileUploadQueryService prepsFileUploadQueryService;

    public PrepsFileUploadResource(PrepsFileUploadService prepsFileUploadService, PrepsFileUploadQueryService prepsFileUploadQueryService) {
        this.prepsFileUploadService = prepsFileUploadService;
        this.prepsFileUploadQueryService = prepsFileUploadQueryService;
    }

    /**
     * {@code POST  /preps-file-uploads} : Create a new prepsFileUpload.
     *
     * @param prepsFileUploadDTO the prepsFileUploadDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new prepsFileUploadDTO, or with status {@code 400 (Bad Request)} if the prepsFileUpload has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/preps-file-uploads")
    public ResponseEntity<PrepsFileUploadDTO> createPrepsFileUpload(@Valid @RequestBody PrepsFileUploadDTO prepsFileUploadDTO) throws URISyntaxException {
        log.debug("REST request to save PrepsFileUpload : {}", prepsFileUploadDTO);
        if (prepsFileUploadDTO.getId() != null) {
            throw new BadRequestAlertException("A new prepsFileUpload cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PrepsFileUploadDTO result = prepsFileUploadService.save(prepsFileUploadDTO);
        return ResponseEntity.created(new URI("/api/preps-file-uploads/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /preps-file-uploads} : Updates an existing prepsFileUpload.
     *
     * @param prepsFileUploadDTO the prepsFileUploadDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated prepsFileUploadDTO,
     * or with status {@code 400 (Bad Request)} if the prepsFileUploadDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the prepsFileUploadDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/preps-file-uploads")
    public ResponseEntity<PrepsFileUploadDTO> updatePrepsFileUpload(@Valid @RequestBody PrepsFileUploadDTO prepsFileUploadDTO) throws URISyntaxException {
        log.debug("REST request to update PrepsFileUpload : {}", prepsFileUploadDTO);
        if (prepsFileUploadDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        PrepsFileUploadDTO result = prepsFileUploadService.save(prepsFileUploadDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, prepsFileUploadDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /preps-file-uploads} : get all the prepsFileUploads.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of prepsFileUploads in body.
     */
    @GetMapping("/preps-file-uploads")
    public ResponseEntity<List<PrepsFileUploadDTO>> getAllPrepsFileUploads(PrepsFileUploadCriteria criteria, Pageable pageable) {
        log.debug("REST request to get PrepsFileUploads by criteria: {}", criteria);
        Page<PrepsFileUploadDTO> page = prepsFileUploadQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /preps-file-uploads/count} : count all the prepsFileUploads.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/preps-file-uploads/count")
    public ResponseEntity<Long> countPrepsFileUploads(PrepsFileUploadCriteria criteria) {
        log.debug("REST request to count PrepsFileUploads by criteria: {}", criteria);
        return ResponseEntity.ok().body(prepsFileUploadQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /preps-file-uploads/:id} : get the "id" prepsFileUpload.
     *
     * @param id the id of the prepsFileUploadDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the prepsFileUploadDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/preps-file-uploads/{id}")
    public ResponseEntity<PrepsFileUploadDTO> getPrepsFileUpload(@PathVariable Long id) {
        log.debug("REST request to get PrepsFileUpload : {}", id);
        Optional<PrepsFileUploadDTO> prepsFileUploadDTO = prepsFileUploadService.findOne(id);
        return ResponseUtil.wrapOrNotFound(prepsFileUploadDTO);
    }

    /**
     * {@code DELETE  /preps-file-uploads/:id} : delete the "id" prepsFileUpload.
     *
     * @param id the id of the prepsFileUploadDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/preps-file-uploads/{id}")
    public ResponseEntity<Void> deletePrepsFileUpload(@PathVariable Long id) {
        log.debug("REST request to delete PrepsFileUpload : {}", id);
        prepsFileUploadService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/preps-file-uploads?query=:query} : search for the prepsFileUpload corresponding
     * to the query.
     *
     * @param query the query of the prepsFileUpload search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/preps-file-uploads")
    public ResponseEntity<List<PrepsFileUploadDTO>> searchPrepsFileUploads(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of PrepsFileUploads for query {}", query);
        Page<PrepsFileUploadDTO> page = prepsFileUploadService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
}
