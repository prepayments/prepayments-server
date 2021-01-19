package io.github.prepayments.internal.fileProcessing;

import io.github.prepayments.service.dto.PrepsFileUploadDTO;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FileDeletionProcessorChain {


    private final List<FileUploadProcessor<PrepsFileUploadDTO>> fileDeletionProcessors;

    public FileDeletionProcessorChain() {
        fileDeletionProcessors = new CopyOnWriteArrayList<>();
    }

    public void addProcessor(final FileUploadProcessor<PrepsFileUploadDTO> dataDeletionProcessor) {

        this.fileDeletionProcessors.add(dataDeletionProcessor);
    }

    public void apply() {

        // todo configure data to be processed by processor
        fileDeletionProcessors.forEach(processor -> {
            processor.processFileUpload();
        });
    }
}
