package io.github.prepayments.internal.fileProcessing;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FileDeletionProcessorChain<FU, FN> {

    private final List<FileDeletionProcessor<FU, FN>> fileDeletionProcessors;

    public FileDeletionProcessorChain() {
        fileDeletionProcessors = new CopyOnWriteArrayList<>();
    }

    public void addProcessor(final FileDeletionProcessor<FU, FN> dataDeletionProcessor) {

        this.fileDeletionProcessors.add(dataDeletionProcessor);
    }

    public void apply(FU fileUploadDTO, FN fileDeleteNotification) {

        fileDeletionProcessors.forEach(processor -> {
            processor.processFileDeletion(fileUploadDTO, fileDeleteNotification);
        });
    }
}
