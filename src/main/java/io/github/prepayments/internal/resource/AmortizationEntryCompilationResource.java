package io.github.prepayments.internal.resource;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.prepayments.domain.PrepsFileType;
import io.github.prepayments.internal.compilation.AmortizationEntryCompilationNotice;
import io.github.prepayments.internal.service.AmortizationEntryCompilationNoticeHandlingService;
import io.github.prepayments.service.PrepsFileUploadService;
import io.github.prepayments.service.dto.PrepsFileUploadDTO;
import io.netty.handler.codec.http.multipart.FileUpload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

/**
 * This resource is designed to trigger compilation procedures
 */
@Slf4j
@RestController
@RequestMapping("/api/app/compilation")
public class AmortizationEntryCompilationResource {

    private PrepsFileUploadService fileUploadService;

    @Autowired
    @Qualifier("amortizationEntryCompilationNoticeHandlingService")
    private AmortizationEntryCompilationNoticeHandlingService amortizationEntryCompilationNoticeHandlingService;

    /**
     * {@code POST  /amortization-entry} : Initialize amortization-entry compilation procedures.
     *
     * @param compilationRequest the object containing compilation processes to initiate and their parameters.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} or with status {@code 400 (Bad Request)} if the processing for that file is in progress
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/amortization-entry")
    public ResponseEntity<PrepsFileUploadDTO> createFileUpload(@Valid @RequestBody CompilationRequestDTO compilationRequest) throws URISyntaxException {

        log.debug("Request received for amortization-entry compilation processing with internal API : {}", fileUploadDTO);



        PrepsFileUploadDTO fileUpload = fileUploadService.findOne(compilationRequest.getFileId());

        amortizationEntryCompilationNoticeHandlingService.handle(AmortizationEntryCompilationNotice.builder()
                                                                                                   .fileId(compilationRequest.getFileId())
                                                                                                   .timestamp(System.currentTimeMillis())
                                                                                                   .fileName(/* TODO get fileName from services*/)
                                                                                                   .uploadToken(/*TODO get file upload token from service*/)
                                                                                                   // TODO remove this
                                                                                                   .amortizationEntryCompilationType(compilationRequest)
                                                                                                   .build());

        // TODO create compilation-request entity to handle this via resource
        return ResponseEntity.created(new URI("/api/app/compilation/amortization-entry/" + compilationRequest.getId()))
                             .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                             .body(result);
    }
}
