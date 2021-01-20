package io.github.prepayments.internal.fileProcessing;

import io.github.prepayments.domain.enumeration.PrepsFileDeleteProcessType;
import io.github.prepayments.internal.model.FileDeleteNotification;
import io.github.prepayments.service.dto.PrepsFileUploadDTO;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.github.prepayments.domain.enumeration.PrepsFileDeleteProcessType.DELETE_PREPAYMENT_DATA;

@Configuration
public class FileDeletionProcessorContainment {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("prepaymentDataDeletionJob")
    private Job prepaymentDataDeletionJob;

    @Bean
    public FileDeletionProcessorChain<PrepsFileUploadDTO, FileDeleteNotification> fileDeletionProcessorChain() {

        FileDeletionProcessorChain<PrepsFileUploadDTO, FileDeleteNotification> theChain = new FileDeletionProcessorChain<>();

        theChain.addProcessor(new BatchSupportedFileDeletionProcessor(jobLauncher, prepaymentDataDeletionJob, DELETE_PREPAYMENT_DATA));
        // theChain.addProcessor(new BatchSupportedFileDeletionProcessor(jobLauncher, prepaymenteEntryDeletionJob, DELETE_PREPAYMENT_ENTRY));
        // theChain.addProcessor(new BatchSupportedFileDeletionProcessor(jobLauncher, amortizationEntryDeletionJob, DELETE_AMORTIZATION_ENTRY));

        return theChain;
    }
}
