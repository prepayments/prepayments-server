package io.github.prepayments.internal.resource;

import io.github.prepayments.internal.model.FileDeleteNotification;
import io.github.prepayments.internal.model.FileNotification;
import io.github.prepayments.internal.resource.decorator.IFileUploadResource;
import io.github.prepayments.internal.service.HandlingService;
import io.github.prepayments.domain.PrepsFileType;
import io.github.prepayments.service.PrepsFileTypeService;
import io.github.prepayments.service.PrepsFileUploadService;
import io.github.prepayments.service.dto.PrepsFileUploadCriteria;
import io.github.prepayments.service.dto.PrepsFileUploadDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * This resource contains custom file-uploads code. In particular when we receive a POST request from the
 * client, we save the file as usual but then shortly after trigger file processing services that need to run in
 * a batch, and are therefore called from an asynchronous service
 */
@RestController
@RequestMapping("/api/app")
public class AppFileUploadResource implements IFileUploadResource {

    private final Logger log = LoggerFactory.getLogger(AppFileUploadResource.class);

    private final PrepsFileUploadService fileUploadService;
    private final IFileUploadResource fileUploadResource;
    private final HandlingService<FileNotification> fileNotificationHandlingService;
    private final HandlingService<FileDeleteNotification> fileDeleteNotificationHandlingService;
    private final PrepsFileTypeService fileTypeService;

    public AppFileUploadResource(final IFileUploadResource fileUploadResourceDecorator, final HandlingService<FileNotification> fileNotificationHandlingService,
                                 final PrepsFileTypeService fileTypeService,
                                 final PrepsFileUploadService fileUploadService,
                                 final HandlingService<FileDeleteNotification> fileDeleteNotificationHandlingService) {
        this.fileUploadResource = fileUploadResourceDecorator;
        this.fileNotificationHandlingService = fileNotificationHandlingService;
        this.fileTypeService = fileTypeService;
        this.fileUploadService = fileUploadService;
        this.fileDeleteNotificationHandlingService = fileDeleteNotificationHandlingService;
    }

    /**
     * {@code POST  /file-uploads} : Create a new fileUpload.
     *
     * @param fileUploadDTO the fileUploadDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fileUploadDTO, or with status {@code 400 (Bad Request)} if the fileUpload has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/file-uploads")
    public ResponseEntity<PrepsFileUploadDTO> createFileUpload(@Valid @RequestBody PrepsFileUploadDTO fileUploadDTO) throws URISyntaxException {

        log.debug("Request received for file-upload processing with internal API : {}", fileUploadDTO);

        ResponseEntity<PrepsFileUploadDTO> responseEntity = fileUploadResource.createFileUpload(fileUploadDTO);

        PrepsFileType fileType = fileTypeService.findOne(fileUploadDTO.getPrepsFileTypeId())
            .orElseThrow(() -> new NoSuchElementException("FileType of ID : " + fileUploadDTO.getPrepsFileTypeId()+ " not found"));

        log.debug("Invoking token-processing for file-type of id : {}", fileType.getId());

        fileNotificationHandlingService.handle(FileNotification.builder()
                                                               .filename(fileUploadDTO.getFileName())
                                                               .description(fileUploadDTO.getDescription())
                                                               .prepsfileModelType(fileType.getPrepsfileType())
                                                               .fileId(String.valueOf(Objects.requireNonNull(responseEntity.getBody()).getId()))
                                                               .build()
        );
        return responseEntity;
    }

    /**
     * {@code PUT  /file-uploads} : Updates an existing fileUpload.
     *
     * @param fileUploadDTO the fileUploadDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fileUploadDTO, or with status {@code 400 (Bad Request)} if the fileUploadDTO is not valid, or with
     * status {@code 500 (Internal Server Error)} if the fileUploadDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/file-uploads")
    public ResponseEntity<PrepsFileUploadDTO> updateFileUpload(@Valid @RequestBody PrepsFileUploadDTO fileUploadDTO) throws URISyntaxException {

        return fileUploadResource.updateFileUpload(fileUploadDTO);
    }

    /**
     * {@code GET  /file-uploads} : get all the fileUploads.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fileUploads in body.
     */
    @GetMapping("/file-uploads")
    public ResponseEntity<List<PrepsFileUploadDTO>> getAllFileUploads (PrepsFileUploadCriteria criteria, Pageable pageable) {

        return fileUploadResource.getAllFileUploads(criteria, pageable);
    }

    /**
     * {@code GET  /file-uploads/count} : count all the fileUploads.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/file-uploads/count")
    public ResponseEntity<Long> countFileUploads (PrepsFileUploadCriteria criteria) {

        return fileUploadResource.countFileUploads(criteria);
    }

    /**
     * {@code GET  /file-uploads/:id} : get the "id" fileUpload.
     *
     * @param id the id of the fileUploadDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fileUploadDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/file-uploads/{id}")
    public ResponseEntity<PrepsFileUploadDTO> getFileUpload(@PathVariable Long id) {

        return fileUploadResource.getFileUpload(id);
    }

    /**
     * {@code DELETE  /file-uploads/:id} : delete the "id" fileUpload.
     *
     * This method will also delete entities which have been updated with data from the file upload
     *
     * @param id the id of the fileUploadDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/file-uploads/{id}")
    public ResponseEntity<Void> deleteFileUpload(@PathVariable Long id) {

        PrepsFileUploadDTO fileUploadDTO = fileUploadService.findOne(id)
                                                            .orElseThrow(() -> new IllegalArgumentException("File id: " + id + " could not be found"));

        PrepsFileType fileType = fileTypeService.findOne(fileUploadDTO.getPrepsFileTypeId())
                                                .orElseThrow(() -> new NoSuchElementException("FileType of ID : " + fileUploadDTO.getPrepsFileTypeId()+ " not found"));

        // @formatter:off delete related information
        fileDeleteNotificationHandlingService.handle(FileDeleteNotification.builder()
                                                                     .filename(fileUploadDTO.getFileName())
                                                                     .description(fileUploadDTO.getDescription())
                                                                     .prepsfileDeleteProcessType(fileType.getPrepsfileDeleteProcessType())
                                                                     .fileId(String.valueOf(id))
                                                                     .build()
        );
        // @formatter:on

        return fileUploadResource.deleteFileUpload(id);
    }
}
