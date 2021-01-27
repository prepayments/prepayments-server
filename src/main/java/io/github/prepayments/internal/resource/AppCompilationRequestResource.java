package io.github.prepayments.internal.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.prepayments.domain.enumeration.CompilationStatus;
import io.github.prepayments.internal.compilation.AmortizationEntryCompilationNotice;
import io.github.prepayments.internal.resource.decorator.CompilationRequestResourceDecorator;
import io.github.prepayments.internal.resource.decorator.ICompilationRequestResourceDecorator;
import io.github.prepayments.internal.service.HandlingService;
import io.github.prepayments.internal.service.StatusUpdateService;
import io.github.prepayments.internal.util.TokenGenerator;
import io.github.prepayments.service.CompilationRequestService;
import io.github.prepayments.service.PrepsFileUploadService;
import io.github.prepayments.service.dto.CompilationRequestCriteria;
import io.github.prepayments.service.dto.CompilationRequestDTO;
import io.github.prepayments.web.rest.CompilationRequestResource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
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

/**
 * This controller uses the underlying implementation for the compilation-request entity both to create an instance
 * in the db and also to trigger a batch process for compilation of prepayment-data instances to prepayment-entry
 * and amortization-entry.
 *
 * The upload-token is increasingly crucial here because it's representative of the initial file-upload that
 * brings data to life within the system, and in this particular flow of logic we use it to extract the data
 * that was created using that file-ID so that it can be used in a reader to begin processing the compilation.
 *
 * Another token is the compilation-token; this is a unique token that is associated with the precess that
 * leads to existence of instances of entities created as a result of a compilation-request
 */
@RestController
@RequestMapping("/api/app")
public class AppCompilationRequestResource extends CompilationRequestResourceDecorator implements ICompilationRequestResourceDecorator {

    private final HandlingService<AmortizationEntryCompilationNotice> compilationNoticeHandlingService;

    private final StatusUpdateService<CompilationRequestDTO> compilationRequestStatusUpdateService;

    private final PrepsFileUploadService fileUploadService;

    private final TokenGenerator tokenGenerator;

    private final CompilationRequestService compilationRequestService;

    public AppCompilationRequestResource(final CompilationRequestResource compilationRequestResource,
                                         final @Qualifier("amortizationEntryCompilationNoticeHandlingService") HandlingService<AmortizationEntryCompilationNotice> compilationNoticeHandlingService,
                                         final StatusUpdateService<CompilationRequestDTO> compilationRequestStatusUpdateService,
                                         final PrepsFileUploadService fileUploadService,
                                         final TokenGenerator tokenGenerator,
                                         final CompilationRequestService compilationRequestService) {
        super(compilationRequestResource);
        this.compilationNoticeHandlingService = compilationNoticeHandlingService;
        this.compilationRequestStatusUpdateService = compilationRequestStatusUpdateService;
        this.fileUploadService = fileUploadService;
        this.tokenGenerator = tokenGenerator;
        this.compilationRequestService = compilationRequestService;
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
    public ResponseEntity<CompilationRequestDTO> createCompilationRequest(@RequestBody CompilationRequestDTO compilationRequestDTO) throws URISyntaxException, IllegalArgumentException {
        ResponseEntity<CompilationRequestDTO> response = super.createCompilationRequest(compilationRequestDTO);

        fileUploadService.findOne(compilationRequestDTO.getFileUploadId()).ifPresent(foundIt -> {
            try {
                String compilationToken = tokenGenerator.md5Digest(foundIt);

                // Use async service to update for quicker response to client
                updateCompilationStatusToken(response, compilationToken);

                compilationNoticeHandlingService.handle(AmortizationEntryCompilationNotice.builder()
                                                                                          .fileId(compilationRequestDTO.getFileUploadId())
                                                                                          .timestamp(System.currentTimeMillis())
                                                                                          .fileName(foundIt.getFileName())
                                                                                          .uploadToken(foundIt.getUploadToken())
                                                                                          .compilationToken(compilationToken)
                                                                                          .compilationType(compilationRequestDTO.getCompilationType())
                                                                                          .compilationRequestId(Objects.requireNonNull(response.getBody()).getId())
                                                                                          .compilationStatus(CompilationStatus.IN_PROGRESS)
                                                                                          .build());
            } catch (JsonProcessingException e) {
                compilationRequestStatusUpdateService.updateStatusFailed(response.getBody());
            }
        });

        // TODO do this in the job-listener compilationRequestStatusUpdateService.updateInProgress(response.getBody());

        return response;
    }

    @Async
    protected void updateCompilationStatusToken(ResponseEntity<CompilationRequestDTO> response, String token) {

        compilationRequestService.findOne(Objects.requireNonNull(response.getBody()).getId()).ifPresent(request -> {
            request.setCompilationToken(token);

            // TODO must be explicit for my comfort
            compilationRequestService.save(request);
        });

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
