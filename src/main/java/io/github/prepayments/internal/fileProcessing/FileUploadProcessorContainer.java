package io.github.prepayments.internal.fileProcessing;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// todo loop for every file model type
import static io.github.prepayments.domain.enumeration.PrepsFileModelType.CURRENCY_LIST;
import static io.github.prepayments.domain.enumeration.PrepsFileModelType.PREPAYMENT_DATA;

/**
 * This object maintains a list of all existing processors. This is a shot in the dark about automatically configuring the chain at start up
 */
@Configuration
public class FileUploadProcessorContainer {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("currencyTablePersistenceJob")
    private Job currencyTablePersistenceJob;

    @Autowired
    @Qualifier("prepaymentDataListPersistenceJob")
    private Job prepaymentDataListPersistenceJob;

    @Bean("fileUploadProcessorChain")
    public FileUploadProcessorChain fileUploadProcessorChain() {

        FileUploadProcessorChain theChain = new FileUploadProcessorChain();

        // Create the chain, each should match against it's specific key of data-model type
        theChain.addProcessor(new BatchSupportedFileUploadProcessor(jobLauncher, currencyTablePersistenceJob, CURRENCY_LIST));
        theChain.addProcessor(new BatchSupportedFileUploadProcessor(jobLauncher, prepaymentDataListPersistenceJob, PREPAYMENT_DATA));

        return theChain;
    }

}
