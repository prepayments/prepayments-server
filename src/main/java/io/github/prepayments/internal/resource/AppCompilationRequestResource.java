package io.github.prepayments.internal.resource;

import io.github.prepayments.internal.compilation.AmortizationEntryCompilationNotice;
import io.github.prepayments.internal.resource.decorator.CompilationRequestResourceDecorator;
import io.github.prepayments.internal.resource.decorator.ICompilationRequestResourceDecorator;
import io.github.prepayments.internal.service.AmortizationEntryCompilationNoticeHandlingService;
import io.github.prepayments.service.PrepsFileUploadService;
import io.github.prepayments.service.dto.CompilationRequestCriteria;
import io.github.prepayments.service.dto.CompilationRequestDTO;
import io.github.prepayments.service.dto.PrepsFileUploadDTO;
import io.github.prepayments.web.rest.CompilationRequestResource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/app")
public class AppCompilationRequestResource extends CompilationRequestResourceDecorator implements ICompilationRequestResourceDecorator {

    private final AmortizationEntryCompilationNoticeHandlingService compilationNoticeHandlingService;

    private final PrepsFileUploadService fileUploadService;

    public AppCompilationRequestResource(final CompilationRequestResource compilationRequestResource,
                                         final @Qualifier("amortizationEntryCompilationNoticeHandlingService") AmortizationEntryCompilationNoticeHandlingService compilationNoticeHandlingService,
                                         final PrepsFileUploadService fileUploadService) {
        super(compilationRequestResource);
        this.compilationNoticeHandlingService = compilationNoticeHandlingService;
        this.fileUploadService = fileUploadService;
    }

    /**
     * {@code POST  /compilation-requests} : Create a new compilationRequest.
     *
     * @param compilationRequestDTO the compilationRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new compilationRequestDTO, or with status {@code 400 (Bad Request)} if the compilationRequest has already
     * an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/compilation-requests")
    public ResponseEntity<CompilationRequestDTO> createCompilationRequest(@RequestBody CompilationRequestDTO compilationRequestDTO) throws URISyntaxException {
        ResponseEntity<CompilationRequestDTO> response = super.createCompilationRequest(compilationRequestDTO);

        PrepsFileUploadDTO fileUpload = fileUploadService.findOne(compilationRequestDTO.getFileUploadId()).orElseThrow(() -> {
            throw new IllegalArgumentException("Could not find a file with the id : " + compilationRequestDTO.getFileUploadId());
        });

        compilationNoticeHandlingService.handle(AmortizationEntryCompilationNotice.builder()
                                                                                  .fileId(compilationRequestDTO.getFileUploadId())
                                                                                  .timestamp(System.currentTimeMillis())
                                                                                  .fileName(fileUpload.getFileName())
                                                                                  .uploadToken(fileUpload.getUploadToken())
                                                                                  .compilationType(compilationRequestDTO.getCompilationType())
                                                                                  .compilationRequestId(Objects.requireNonNull(response.getBody()).getId())
                                                                                  .build());

        return response;
    }

    /**
     * {@code PUT  /compilation-requests} : Updates an existing compilationRequest.
     *
     * @param compilationRequestDTO the compilationRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated compilationRequestDTO, or with status {@code 400 (Bad Request)} if the compilationRequestDTO is not
     * valid, or with status {@code 500 (Internal Server Error)} if the compilationRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/compilation-requests")
    public ResponseEntity<CompilationRequestDTO> updateCompilationRequest(@RequestBody CompilationRequestDTO compilationRequestDTO) throws URISyntaxException {
        return super.updateCompilationRequest(compilationRequestDTO);
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
        return super.getAllCompilationRequests(criteria, pageable);
    }

    /**
     * {@code GET  /compilation-requests/count} : count all the compilationRequests.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/compilation-requests/count")
    public ResponseEntity<Long> countCompilationRequests(CompilationRequestCriteria criteria) {
        return super.countCompilationRequests(criteria);
    }

    /**
     * {@code GET  /compilation-requests/:id} : get the "id" compilationRequest.
     *
     * @param id the id of the compilationRequestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the compilationRequestDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/compilation-requests/{id}")
    public ResponseEntity<CompilationRequestDTO> getCompilationRequest(@PathVariable Long id) {
        return super.getCompilationRequest(id);
    }

    /**
     * {@code DELETE  /compilation-requests/:id} : delete the "id" compilationRequest.
     *
     * @param id the id of the compilationRequestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/compilation-requests/{id}")
    public ResponseEntity<Void> deleteCompilationRequest(@PathVariable Long id) {
        return super.deleteCompilationRequest(id);
    }

    /**
     * {@code SEARCH  /_search/compilation-requests?query=:query} : search for the compilationRequest corresponding to the query.
     *
     * @param query    the query of the compilationRequest search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/compilation-requests")
    public ResponseEntity<List<CompilationRequestDTO>> searchCompilationRequests(@RequestParam String query, Pageable pageable) {
        return super.searchCompilationRequests(query, pageable);
    }

}
