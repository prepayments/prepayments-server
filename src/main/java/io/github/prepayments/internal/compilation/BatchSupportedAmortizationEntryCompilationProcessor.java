package io.github.prepayments.internal.compilation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

/**
 * It's is expected that we will configure different processes for prepayment-entry and amortization-entry
 */
@Slf4j
public class BatchSupportedAmortizationEntryCompilationProcessor
    implements AmortizationEntryCompilationProcessor<AmortizationEntryCompilationNotice> {

    private final JobLauncher jobLauncher;
    public final Job batchJob;
    private final AmortizationEntryCompilationType amortizationEntryCompilationType;

    public BatchSupportedAmortizationEntryCompilationProcessor(final JobLauncher jobLauncher, final Job batchJob, final AmortizationEntryCompilationType amortizationEntryCompilationType) {
        this.jobLauncher = jobLauncher;
        this.batchJob = batchJob;
        this.amortizationEntryCompilationType = amortizationEntryCompilationType;
    }

    @Override
    public AmortizationEntryCompilationNotice compile(AmortizationEntryCompilationNotice notification) {

        if (notification.getAmortizationEntryCompilationType() == amortizationEntryCompilationType) {
            log.debug("Compilation method confirmed for file Id: {}; message-token: {}; Compilation in startup...", notification.getFileId(), notification.getUploadToken());

            JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();

            jobParametersBuilder.addLong("fileId", notification.getFileId());
            jobParametersBuilder.addLong("startUpTime", notification.getTimestamp());
            jobParametersBuilder.addString("messageToken", notification.getUploadToken());
            jobParametersBuilder.addString("fileName", notification.getFileName());

            notification.setCompilationStatus(CompilationStatus.STARTING);

            try {
                notification.setCompilationStatus(CompilationStatus.IN_PROGRESS);
                jobLauncher.run(batchJob, jobParametersBuilder.toJobParameters());
            } catch (JobExecutionAlreadyRunningException | JobRestartException | JobParametersInvalidException | JobInstanceAlreadyCompleteException e) {
                notification.setCompilationStatus(CompilationStatus.FAILED);
            }
            notification.setCompilationStatus(CompilationStatus.COMPLETE);
        } else {

            notification.setCompilationStatus(CompilationStatus.STARTING);
            log.debug("File upload inconsistent with the data model supported by this processor");
        }

        notification.setCompilationStatus(CompilationStatus.UN_DEFINED);

        return notification;
    }
}
