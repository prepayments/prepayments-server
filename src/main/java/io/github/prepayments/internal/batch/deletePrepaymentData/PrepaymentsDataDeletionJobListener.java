package io.github.prepayments.internal.batch.deletePrepaymentData;

import io.github.prepayments.internal.batch.PersistenceJobListener;
import org.slf4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * This clss is designed to do a couple of things for instance if desired to mark a flag that a particular message token
 * has been deleted, or processed
 */
@Scope("job")
public class PrepaymentsDataDeletionJobListener implements JobExecutionListener {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(PersistenceJobListener.class);

    private final long fileId;
    private final long startUpTime;
    private String fileName;
    private String messageToken;

    public PrepaymentsDataDeletionJobListener(final long fileId, final long startUpTime, final String fileName, final String messageToken) {
        this.fileId = fileId;
        this.startUpTime = startUpTime;
        this.fileName = fileName;
        this.messageToken = messageToken;
    }

    /**
     * Callback before a job executes.
     *
     * @param jobExecution the current {@link JobExecution}
     */
    @Override
    public void beforeJob(final JobExecution jobExecution) {

        log.info("New deletion job id : {} has started for file id : {}, with start time : {} for message-token {}; bearing the file-name {}", jobExecution.getJobId(), fileId, startUpTime,
                 messageToken, fileName);

    }

    /**
     * Callback after completion of a job. Called after both both successful and failed executions. To perform logic on a particular status, use "if (jobExecution.getStatus() == BatchStatus.X)".
     *
     * @param jobExecution the current {@link JobExecution}
     */
    @Override
    public void afterJob(final JobExecution jobExecution) {

        String exitStatus = jobExecution.getExitStatus().getExitCode();

        long executionTime = System.currentTimeMillis() - startUpTime;

        log.info("Job Id {}, for file-id : {} for message-token: {}; bearing the file-name: {} completed in : {}ms with status {}", jobExecution.getJobId(), fileId, messageToken, fileName, executionTime, exitStatus);
    }
}
