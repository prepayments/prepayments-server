package io.github.prepayments.internal.fileProcessing;

import io.github.prepayments.internal.model.FileNotification;
import io.github.prepayments.internal.service.HandlingService;
import io.github.prepayments.service.dto.PrepsFileUploadDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static io.github.prepayments.domain.enumeration.PrepsFileModelType.PREPAYMENT_DATA_DELETE;

@Slf4j
@Service("prepaymentsDataDeletionProcessor")
public class PrepaymentsDataDeletionProcessor  implements FileDeletionProcessor<PrepsFileUploadDTO>  {

    private final HandlingService<Long> prepaymentsDataDeletionService;


    public PrepaymentsDataDeletionProcessor(final @Qualifier("prepaymentsDataDeletionService") HandlingService<Long> prepaymentsDataDeletionService) {
        this.prepaymentsDataDeletionService = prepaymentsDataDeletionService;
    }

    @Override
    public PrepsFileUploadDTO processFileDeletion(final PrepsFileUploadDTO payload) {
        // TODO GET DATA FROM RESOURCE
        return null;
    }

    @Override
    public PrepsFileUploadDTO processFileUpload(final PrepsFileUploadDTO fileUpload) {

        // processing only for prepayment_data_delete types
        if (fileNotification.getprepsfileModelType() == PREPAYMENT_DATA_DELETE) {

            prepaymentsDataDeletionService.handle(fileUpload.getId());

        } else {

            log.debug("File upload inconsistent with the data model supported by this processor");
        }

        return fileUpload;
    }
}
