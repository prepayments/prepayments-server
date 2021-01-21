package io.github.prepayments.internal.resource.decorator;

import io.github.prepayments.service.dto.CompilationRequestCriteria;
import io.github.prepayments.service.dto.CompilationRequestDTO;
import io.github.prepayments.web.rest.CompilationRequestResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.List;

@Component("compilationRequestResourceDecorator")
public class CompilationRequestResourceDecorator implements ICompilationRequestResourceDecorator {

    private final CompilationRequestResource compilationRequestResource;

    public CompilationRequestResourceDecorator(final CompilationRequestResource compilationRequestResource) {
        this.compilationRequestResource = compilationRequestResource;
    }

    /**
     * {@code POST  /compilation-requests} : Create a new compilationRequest.
     *
     * @param compilationRequestDTO the compilationRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new compilationRequestDTO, or with status {@code 400 (Bad Request)} if the compilationRequest has already
     * an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Override
    public ResponseEntity<CompilationRequestDTO> createCompilationRequest(CompilationRequestDTO compilationRequestDTO) throws URISyntaxException {
        return compilationRequestResource.createCompilationRequest(compilationRequestDTO);
    }

    /**
     * {@code PUT  /compilation-requests} : Updates an existing compilationRequest.
     *
     * @param compilationRequestDTO the compilationRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated compilationRequestDTO, or with status {@code 400 (Bad Request)} if the compilationRequestDTO is not
     * valid, or with status {@code 500 (Internal Server Error)} if the compilationRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Override
    public ResponseEntity<CompilationRequestDTO> updateCompilationRequest(CompilationRequestDTO compilationRequestDTO) throws URISyntaxException {
        return compilationRequestResource.updateCompilationRequest(compilationRequestDTO);
    }

    /**
     * {@code GET  /compilation-requests} : get all the compilationRequests.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of compilationRequests in body.
     */
    @Override
    public ResponseEntity<List<CompilationRequestDTO>> getAllCompilationRequests(CompilationRequestCriteria criteria, Pageable pageable) {
        return compilationRequestResource.getAllCompilationRequests(criteria, pageable);
    }

    /**
     * {@code GET  /compilation-requests/count} : count all the compilationRequests.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @Override
    public ResponseEntity<Long> countCompilationRequests(CompilationRequestCriteria criteria) {
        return compilationRequestResource.countCompilationRequests(criteria);
    }

    /**
     * {@code GET  /compilation-requests/:id} : get the "id" compilationRequest.
     *
     * @param id the id of the compilationRequestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the compilationRequestDTO, or with status {@code 404 (Not Found)}.
     */
    @Override
    public ResponseEntity<CompilationRequestDTO> getCompilationRequest(Long id) {
        return compilationRequestResource.getCompilationRequest(id);
    }

    /**
     * {@code DELETE  /compilation-requests/:id} : delete the "id" compilationRequest.
     *
     * @param id the id of the compilationRequestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @Override
    public ResponseEntity<Void> deleteCompilationRequest(Long id) {
        return compilationRequestResource.deleteCompilationRequest(id);
    }

    /**
     * {@code SEARCH  /_search/compilation-requests?query=:query} : search for the compilationRequest corresponding to the query.
     *
     * @param query    the query of the compilationRequest search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @Override
    public ResponseEntity<List<CompilationRequestDTO>> searchCompilationRequests(String query, Pageable pageable) {
        return compilationRequestResource.searchCompilationRequests(query, pageable);
    }
}
