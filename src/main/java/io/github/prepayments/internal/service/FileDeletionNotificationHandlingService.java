package io.github.prepayments.internal.service;

import io.github.prepayments.internal.fileProcessing.FileDeletionProcessorChain;
import io.github.prepayments.internal.model.FileDeleteNotification;
import io.github.prepayments.service.PrepsFileUploadService;
import io.github.prepayments.service.dto.PrepsFileUploadDTO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * This service handles the deletion notice of a given file notification to cascade the effects in the rest of the data space
 */
@Service("fileDeletionNotificationHandlingService")
public class FileDeletionNotificationHandlingService implements HandlingService<FileDeleteNotification> {

    private final PrepsFileUploadService fileUploadService;
    private final FileDeletionProcessorChain fileDeletionProcessorChain;

    public FileDeletionNotificationHandlingService(final PrepsFileUploadService fileUploadService, final FileDeletionProcessorChain fileDeletionProcessorChain) {
        this.fileUploadService = fileUploadService;
        this.fileDeletionProcessorChain = fileDeletionProcessorChain;
    }

    @Override
    @Async
    public void handle(final FileDeleteNotification fileDeleteNotification) {

        PrepsFileUploadDTO fileUploadDTO = fileUploadService.findOne(Long.valueOf(fileDeleteNotification.getFileId()))
                                                            .orElseThrow(() -> new IllegalArgumentException("File id : " + fileDeleteNotification.getFileId() + " could not be found"));

        // TODO Add generics
        fileDeletionProcessorChain.apply(fileUploadDTO, fileDeleteNotification);
    }
}
