package io.github.prepayments.internal.fileProcessing;

import io.github.prepayments.service.dto.PrepsFileUploadDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileDeletionProcessorContainment {

    @Autowired
    @Qualifier("prepaymentsDataDeletionProcessor")
    private FileUploadProcessor<PrepsFileUploadDTO> prepaymentsDataDeletionProcessor;

    @Bean
    public FileDeletionProcessorChain fileDeletionProcessorChain() {

        FileDeletionProcessorChain theChain = new FileDeletionProcessorChain();

        theChain.addProcessor(prepaymentsDataDeletionProcessor);

        return theChain;
    }
}
