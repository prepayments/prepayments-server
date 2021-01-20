package io.github.prepayments.internal.fileProcessing;

import io.github.prepayments.domain.enumeration.PrepsFileDeleteProcessType;
import io.github.prepayments.internal.model.FileDeleteNotification;
import io.github.prepayments.service.dto.PrepsFileUploadDTO;
import org.slf4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

/**
 * This is a general configuration for a batch data deletion job
 */
public class BatchSupportedFileDeletionProcessor implements FileDeletionProcessor<PrepsFileUploadDTO, FileDeleteNotification> {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BatchSupportedFileUploadProcessor.class);

    private final JobLauncher jobLauncher;
    public final Job batchJob;
    private final PrepsFileDeleteProcessType fileModelType;

    public BatchSupportedFileDeletionProcessor(final JobLauncher jobLauncher, final Job batchJob, final PrepsFileDeleteProcessType fileModelType) {
        this.jobLauncher = jobLauncher;
        this.batchJob = batchJob;
        this.fileModelType = fileModelType;
    }

    /**
     * This method only runs if the file-delete-process-type is the same the file-upload's DTO
     */
    @Override
    public FileDeleteNotification processFileDeletion(final PrepsFileUploadDTO fileUpload, final FileDeleteNotification fileDeleteNotification) {

        if (fileDeleteNotification.getPrepsfileDeleteProcessType() == fileModelType) {
            log.debug("File-upload type confirmed commencing process...");

            JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();

            jobParametersBuilder.addLong("fileId", fileUpload.getId());
            jobParametersBuilder.addLong("startUpTime", fileDeleteNotification.getTimestamp());
            jobParametersBuilder.addString("messageToken", fileUpload.getUploadToken());
            jobParametersBuilder.addString("fileName", fileUpload.getFileName());

            try {
                jobLauncher.run(batchJob, jobParametersBuilder.toJobParameters());
            } catch (JobExecutionAlreadyRunningException | JobRestartException | JobParametersInvalidException | JobInstanceAlreadyCompleteException e) {
                e.printStackTrace();
            }
        } else {

            log.debug("File upload inconsistent with the data model supported by this processor");
        }

        return fileDeleteNotification;
    }
}
