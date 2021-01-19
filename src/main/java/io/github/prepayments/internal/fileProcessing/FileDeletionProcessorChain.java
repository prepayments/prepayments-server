package io.github.prepayments.internal.fileProcessing;

import io.github.prepayments.internal.model.FileDeleteNotification;
import io.github.prepayments.service.dto.PrepsFileUploadDTO;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FileDeletionProcessorChain {

    private final List<FileDeletionProcessor<PrepsFileUploadDTO, FileDeleteNotification>> fileDeletionProcessors;

    public FileDeletionProcessorChain() {
        fileDeletionProcessors = new CopyOnWriteArrayList<>();
    }

    public void addProcessor(final FileDeletionProcessor<PrepsFileUploadDTO, FileDeleteNotification> dataDeletionProcessor) {

        this.fileDeletionProcessors.add(dataDeletionProcessor);
    }

    public void apply(PrepsFileUploadDTO fileUploadDTO, FileDeleteNotification fileDeleteNotification) {

        // todo configure data to be processed by processor
        fileDeletionProcessors.forEach(processor -> {
            processor.processFileDeletion(fileUploadDTO, fileDeleteNotification);
        });
    }
}
