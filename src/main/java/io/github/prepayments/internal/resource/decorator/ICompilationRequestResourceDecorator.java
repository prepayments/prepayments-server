package io.github.prepayments.internal.resource.decorator;

import io.github.prepayments.service.dto.CompilationRequestCriteria;
import io.github.prepayments.service.dto.CompilationRequestDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.net.URISyntaxException;
import java.util.List;

public interface ICompilationRequestResourceDecorator {
    /**
     * {@code POST  /compilation-requests} : Create a new compilationRequest.
     *
     * @param compilationRequestDTO the compilationRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new compilationRequestDTO, or with status {@code 400 (Bad Request)} if the compilationRequest has already
     * an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    ResponseEntity<CompilationRequestDTO> createCompilationRequest(CompilationRequestDTO compilationRequestDTO) throws URISyntaxException;

    /**
     * {@code PUT  /compilation-requests} : Updates an existing compilationRequest.
     *
     * @param compilationRequestDTO the compilationRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated compilationRequestDTO, or with status {@code 400 (Bad Request)} if the compilationRequestDTO is not
     * valid, or with status {@code 500 (Internal Server Error)} if the compilationRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    ResponseEntity<CompilationRequestDTO> updateCompilationRequest(CompilationRequestDTO compilationRequestDTO) throws URISyntaxException;

    /**
     * {@code GET  /compilation-requests} : get all the compilationRequests.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of compilationRequests in body.
     */
    ResponseEntity<List<CompilationRequestDTO>> getAllCompilationRequests(CompilationRequestCriteria criteria, Pageable pageable);

    /**
     * {@code GET  /compilation-requests/count} : count all the compilationRequests.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    ResponseEntity<Long> countCompilationRequests(CompilationRequestCriteria criteria);

    /**
     * {@code GET  /compilation-requests/:id} : get the "id" compilationRequest.
     *
     * @param id the id of the compilationRequestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the compilationRequestDTO, or with status {@code 404 (Not Found)}.
     */
    ResponseEntity<CompilationRequestDTO> getCompilationRequest(Long id);

    /**
     * {@code DELETE  /compilation-requests/:id} : delete the "id" compilationRequest.
     *
     * @param id the id of the compilationRequestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    ResponseEntity<Void> deleteCompilationRequest(Long id);

    /**
     * {@code SEARCH  /_search/compilation-requests?query=:query} : search for the compilationRequest corresponding to the query.
     *
     * @param query    the query of the compilationRequest search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    ResponseEntity<List<CompilationRequestDTO>> searchCompilationRequests(String query, Pageable pageable);
}
