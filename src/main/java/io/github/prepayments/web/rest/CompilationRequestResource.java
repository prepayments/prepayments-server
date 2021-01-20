package io.github.prepayments.web.rest;

import io.github.prepayments.service.CompilationRequestService;
import io.github.prepayments.web.rest.errors.BadRequestAlertException;
import io.github.prepayments.service.dto.CompilationRequestDTO;
import io.github.prepayments.service.dto.CompilationRequestCriteria;
import io.github.prepayments.service.CompilationRequestQueryService;

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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link io.github.prepayments.domain.CompilationRequest}.
 */
@RestController
@RequestMapping("/api")
public class CompilationRequestResource {

    private final Logger log = LoggerFactory.getLogger(CompilationRequestResource.class);

    private static final String ENTITY_NAME = "compilationRequest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CompilationRequestService compilationRequestService;

    private final CompilationRequestQueryService compilationRequestQueryService;

    public CompilationRequestResource(CompilationRequestService compilationRequestService, CompilationRequestQueryService compilationRequestQueryService) {
        this.compilationRequestService = compilationRequestService;
        this.compilationRequestQueryService = compilationRequestQueryService;
    }

    /**
     * {@code POST  /compilation-requests} : Create a new compilationRequest.
     *
     * @param compilationRequestDTO the compilationRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new compilationRequestDTO, or with status {@code 400 (Bad Request)} if the compilationRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/compilation-requests")
    public ResponseEntity<CompilationRequestDTO> createCompilationRequest(@RequestBody CompilationRequestDTO compilationRequestDTO) throws URISyntaxException {
        log.debug("REST request to save CompilationRequest : {}", compilationRequestDTO);
        if (compilationRequestDTO.getId() != null) {
            throw new BadRequestAlertException("A new compilationRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CompilationRequestDTO result = compilationRequestService.save(compilationRequestDTO);
        return ResponseEntity.created(new URI("/api/compilation-requests/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /compilation-requests} : Updates an existing compilationRequest.
     *
     * @param compilationRequestDTO the compilationRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated compilationRequestDTO,
     * or with status {@code 400 (Bad Request)} if the compilationRequestDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the compilationRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/compilation-requests")
    public ResponseEntity<CompilationRequestDTO> updateCompilationRequest(@RequestBody CompilationRequestDTO compilationRequestDTO) throws URISyntaxException {
        log.debug("REST request to update CompilationRequest : {}", compilationRequestDTO);
        if (compilationRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        CompilationRequestDTO result = compilationRequestService.save(compilationRequestDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, compilationRequestDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /compilation-requests} : get all the compilationRequests.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of compilationRequests in body.
     */
    @GetMapping("/compilation-requests")
    public ResponseEntity<List<CompilationRequestDTO>> getAllCompilationRequests(CompilationRequestCriteria criteria, Pageable pageable) {
        log.debug("REST request to get CompilationRequests by criteria: {}", criteria);
        Page<CompilationRequestDTO> page = compilationRequestQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /compilation-requests/count} : count all the compilationRequests.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/compilation-requests/count")
    public ResponseEntity<Long> countCompilationRequests(CompilationRequestCriteria criteria) {
        log.debug("REST request to count CompilationRequests by criteria: {}", criteria);
        return ResponseEntity.ok().body(compilationRequestQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /compilation-requests/:id} : get the "id" compilationRequest.
     *
     * @param id the id of the compilationRequestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the compilationRequestDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/compilation-requests/{id}")
    public ResponseEntity<CompilationRequestDTO> getCompilationRequest(@PathVariable Long id) {
        log.debug("REST request to get CompilationRequest : {}", id);
        Optional<CompilationRequestDTO> compilationRequestDTO = compilationRequestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(compilationRequestDTO);
    }

    /**
     * {@code DELETE  /compilation-requests/:id} : delete the "id" compilationRequest.
     *
     * @param id the id of the compilationRequestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/compilation-requests/{id}")
    public ResponseEntity<Void> deleteCompilationRequest(@PathVariable Long id) {
        log.debug("REST request to delete CompilationRequest : {}", id);
        compilationRequestService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/compilation-requests?query=:query} : search for the compilationRequest corresponding
     * to the query.
     *
     * @param query the query of the compilationRequest search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/compilation-requests")
    public ResponseEntity<List<CompilationRequestDTO>> searchCompilationRequests(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of CompilationRequests for query {}", query);
        Page<CompilationRequestDTO> page = compilationRequestService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
}
