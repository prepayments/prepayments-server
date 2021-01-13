package io.github.prepayments.web.rest;

import io.github.prepayments.service.PrepaymentDataService;
import io.github.prepayments.web.rest.errors.BadRequestAlertException;
import io.github.prepayments.service.dto.PrepaymentDataDTO;
import io.github.prepayments.service.dto.PrepaymentDataCriteria;
import io.github.prepayments.service.PrepaymentDataQueryService;

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
 * REST controller for managing {@link io.github.prepayments.domain.PrepaymentData}.
 */
@RestController
@RequestMapping("/api")
public class PrepaymentDataResource {

    private final Logger log = LoggerFactory.getLogger(PrepaymentDataResource.class);

    private static final String ENTITY_NAME = "prepaymentData";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PrepaymentDataService prepaymentDataService;

    private final PrepaymentDataQueryService prepaymentDataQueryService;

    public PrepaymentDataResource(PrepaymentDataService prepaymentDataService, PrepaymentDataQueryService prepaymentDataQueryService) {
        this.prepaymentDataService = prepaymentDataService;
        this.prepaymentDataQueryService = prepaymentDataQueryService;
    }

    /**
     * {@code POST  /prepayment-data} : Create a new prepaymentData.
     *
     * @param prepaymentDataDTO the prepaymentDataDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new prepaymentDataDTO, or with status {@code 400 (Bad Request)} if the prepaymentData has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/prepayment-data")
    public ResponseEntity<PrepaymentDataDTO> createPrepaymentData(@Valid @RequestBody PrepaymentDataDTO prepaymentDataDTO) throws URISyntaxException {
        log.debug("REST request to save PrepaymentData : {}", prepaymentDataDTO);
        if (prepaymentDataDTO.getId() != null) {
            throw new BadRequestAlertException("A new prepaymentData cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PrepaymentDataDTO result = prepaymentDataService.save(prepaymentDataDTO);
        return ResponseEntity.created(new URI("/api/prepayment-data/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /prepayment-data} : Updates an existing prepaymentData.
     *
     * @param prepaymentDataDTO the prepaymentDataDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated prepaymentDataDTO,
     * or with status {@code 400 (Bad Request)} if the prepaymentDataDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the prepaymentDataDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/prepayment-data")
    public ResponseEntity<PrepaymentDataDTO> updatePrepaymentData(@Valid @RequestBody PrepaymentDataDTO prepaymentDataDTO) throws URISyntaxException {
        log.debug("REST request to update PrepaymentData : {}", prepaymentDataDTO);
        if (prepaymentDataDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        PrepaymentDataDTO result = prepaymentDataService.save(prepaymentDataDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, prepaymentDataDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /prepayment-data} : get all the prepaymentData.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of prepaymentData in body.
     */
    @GetMapping("/prepayment-data")
    public ResponseEntity<List<PrepaymentDataDTO>> getAllPrepaymentData(PrepaymentDataCriteria criteria, Pageable pageable) {
        log.debug("REST request to get PrepaymentData by criteria: {}", criteria);
        Page<PrepaymentDataDTO> page = prepaymentDataQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /prepayment-data/count} : count all the prepaymentData.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/prepayment-data/count")
    public ResponseEntity<Long> countPrepaymentData(PrepaymentDataCriteria criteria) {
        log.debug("REST request to count PrepaymentData by criteria: {}", criteria);
        return ResponseEntity.ok().body(prepaymentDataQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /prepayment-data/:id} : get the "id" prepaymentData.
     *
     * @param id the id of the prepaymentDataDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the prepaymentDataDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/prepayment-data/{id}")
    public ResponseEntity<PrepaymentDataDTO> getPrepaymentData(@PathVariable Long id) {
        log.debug("REST request to get PrepaymentData : {}", id);
        Optional<PrepaymentDataDTO> prepaymentDataDTO = prepaymentDataService.findOne(id);
        return ResponseUtil.wrapOrNotFound(prepaymentDataDTO);
    }

    /**
     * {@code DELETE  /prepayment-data/:id} : delete the "id" prepaymentData.
     *
     * @param id the id of the prepaymentDataDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/prepayment-data/{id}")
    public ResponseEntity<Void> deletePrepaymentData(@PathVariable Long id) {
        log.debug("REST request to delete PrepaymentData : {}", id);
        prepaymentDataService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/prepayment-data?query=:query} : search for the prepaymentData corresponding
     * to the query.
     *
     * @param query the query of the prepaymentData search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/prepayment-data")
    public ResponseEntity<List<PrepaymentDataDTO>> searchPrepaymentData(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of PrepaymentData for query {}", query);
        Page<PrepaymentDataDTO> page = prepaymentDataService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
}
