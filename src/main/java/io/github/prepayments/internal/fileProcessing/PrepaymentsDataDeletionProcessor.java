package io.github.prepayments.internal.fileProcessing;

import io.github.prepayments.domain.enumeration.PrepsFileDeleteProcessType;
import io.github.prepayments.internal.model.FileDeleteNotification;
import io.github.prepayments.internal.service.HandlingService;
import io.github.prepayments.service.dto.PrepsFileUploadDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service("prepaymentsDataDeletionProcessor")
public class PrepaymentsDataDeletionProcessor  implements FileDeletionProcessor<PrepsFileUploadDTO, FileDeleteNotification>  {

    private final HandlingService<PrepsFileUploadDTO> prepaymentsDataDeletionService;


    public PrepaymentsDataDeletionProcessor(final @Qualifier("prepaymentsDataDeletionService") HandlingService<PrepsFileUploadDTO> prepaymentsDataDeletionService) {
        this.prepaymentsDataDeletionService = prepaymentsDataDeletionService;
    }

    @Override
    public FileDeleteNotification processFileDeletion(final PrepsFileUploadDTO fileUploadDTO, FileDeleteNotification fileDeleteNotification) {
        if (fileDeleteNotification.getPrepsfileDeleteProcessType() == PrepsFileDeleteProcessType.DELETE_PREPAYMENT_DATA) {
            prepaymentsDataDeletionService.handle(fileUploadDTO);
        } else {

            log.debug("File upload inconsistent with the data model supported by this processor");
        }
        return fileDeleteNotification;
    }
}
