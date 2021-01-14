package io.github.prepayments.web.rest;

import io.github.prepayments.service.PrepsMessageTokenService;
import io.github.prepayments.web.rest.errors.BadRequestAlertException;
import io.github.prepayments.service.dto.PrepsMessageTokenDTO;
import io.github.prepayments.service.dto.PrepsMessageTokenCriteria;
import io.github.prepayments.service.PrepsMessageTokenQueryService;

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
 * REST controller for managing {@link io.github.prepayments.domain.PrepsMessageToken}.
 */
@RestController
@RequestMapping("/api")
public class PrepsMessageTokenResource {

    private final Logger log = LoggerFactory.getLogger(PrepsMessageTokenResource.class);

    private static final String ENTITY_NAME = "prepsPrepsMessageToken";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PrepsMessageTokenService prepsMessageTokenService;

    private final PrepsMessageTokenQueryService prepsMessageTokenQueryService;

    public PrepsMessageTokenResource(PrepsMessageTokenService prepsMessageTokenService, PrepsMessageTokenQueryService prepsMessageTokenQueryService) {
        this.prepsMessageTokenService = prepsMessageTokenService;
        this.prepsMessageTokenQueryService = prepsMessageTokenQueryService;
    }

    /**
     * {@code POST  /preps-message-tokens} : Create a new prepsMessageToken.
     *
     * @param prepsMessageTokenDTO the prepsMessageTokenDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new prepsMessageTokenDTO, or with status {@code 400 (Bad Request)} if the prepsMessageToken has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/preps-message-tokens")
    public ResponseEntity<PrepsMessageTokenDTO> createPrepsMessageToken(@Valid @RequestBody PrepsMessageTokenDTO prepsMessageTokenDTO) throws URISyntaxException {
        log.debug("REST request to save PrepsMessageToken : {}", prepsMessageTokenDTO);
        if (prepsMessageTokenDTO.getId() != null) {
            throw new BadRequestAlertException("A new prepsMessageToken cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PrepsMessageTokenDTO result = prepsMessageTokenService.save(prepsMessageTokenDTO);
        return ResponseEntity.created(new URI("/api/preps-message-tokens/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /preps-message-tokens} : Updates an existing prepsMessageToken.
     *
     * @param prepsMessageTokenDTO the prepsMessageTokenDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated prepsMessageTokenDTO,
     * or with status {@code 400 (Bad Request)} if the prepsMessageTokenDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the prepsMessageTokenDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/preps-message-tokens")
    public ResponseEntity<PrepsMessageTokenDTO> updatePrepsMessageToken(@Valid @RequestBody PrepsMessageTokenDTO prepsMessageTokenDTO) throws URISyntaxException {
        log.debug("REST request to update PrepsMessageToken : {}", prepsMessageTokenDTO);
        if (prepsMessageTokenDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        PrepsMessageTokenDTO result = prepsMessageTokenService.save(prepsMessageTokenDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, prepsMessageTokenDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /preps-message-tokens} : get all the prepsMessageTokens.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of prepsMessageTokens in body.
     */
    @GetMapping("/preps-message-tokens")
    public ResponseEntity<List<PrepsMessageTokenDTO>> getAllPrepsMessageTokens(PrepsMessageTokenCriteria criteria, Pageable pageable) {
        log.debug("REST request to get PrepsMessageTokens by criteria: {}", criteria);
        Page<PrepsMessageTokenDTO> page = prepsMessageTokenQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /preps-message-tokens/count} : count all the prepsMessageTokens.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/preps-message-tokens/count")
    public ResponseEntity<Long> countPrepsMessageTokens(PrepsMessageTokenCriteria criteria) {
        log.debug("REST request to count PrepsMessageTokens by criteria: {}", criteria);
        return ResponseEntity.ok().body(prepsMessageTokenQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /preps-message-tokens/:id} : get the "id" prepsMessageToken.
     *
     * @param id the id of the prepsMessageTokenDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the prepsMessageTokenDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/preps-message-tokens/{id}")
    public ResponseEntity<PrepsMessageTokenDTO> getPrepsMessageToken(@PathVariable Long id) {
        log.debug("REST request to get PrepsMessageToken : {}", id);
        Optional<PrepsMessageTokenDTO> prepsMessageTokenDTO = prepsMessageTokenService.findOne(id);
        return ResponseUtil.wrapOrNotFound(prepsMessageTokenDTO);
    }

    /**
     * {@code DELETE  /preps-message-tokens/:id} : delete the "id" prepsMessageToken.
     *
     * @param id the id of the prepsMessageTokenDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/preps-message-tokens/{id}")
    public ResponseEntity<Void> deletePrepsMessageToken(@PathVariable Long id) {
        log.debug("REST request to delete PrepsMessageToken : {}", id);
        prepsMessageTokenService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/preps-message-tokens?query=:query} : search for the prepsMessageToken corresponding
     * to the query.
     *
     * @param query the query of the prepsMessageToken search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/preps-message-tokens")
    public ResponseEntity<List<PrepsMessageTokenDTO>> searchPrepsMessageTokens(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of PrepsMessageTokens for query {}", query);
        Page<PrepsMessageTokenDTO> page = prepsMessageTokenService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
}
