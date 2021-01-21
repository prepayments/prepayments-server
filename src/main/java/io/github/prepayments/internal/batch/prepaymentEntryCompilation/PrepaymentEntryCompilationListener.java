package io.github.prepayments.internal.batch.prepaymentEntryCompilation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Slf4j
public class PrepaymentEntryCompilationListener implements JobExecutionListener {


    private final long fileId;
    private final long startUpTime;
    private String fileName;
    private String messageToken;

    public PrepaymentEntryCompilationListener(final long fileId, final long startUpTime, final String fileName, final String messageToken) {
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

        log.info("New compilation job id : {} has started for file id : {}, with start time : {} for message-token {}; bearing the file-name {}", jobExecution.getJobId(), fileId, startUpTime,
                 messageToken, fileName);

    }

    /**
     * Callback after completion of a job. Called after both both successful and failed executions. To perform logic on a particular status, use "if (jobExecution.getStatus() == BatchStatus.X)".
     *
     * @param jobExecution the current {@link JobExecution}
     */
    @Override
    public void afterJob(final JobExecution jobExecution) {

        // TODO Update message-token status
        // TODO Update Compilation-request status
        // TODO Update Compilation-request compilation-token
        String exitStatus = jobExecution.getExitStatus().getExitCode();

        long executionTime = System.currentTimeMillis() - startUpTime;

        log.info("Job Id {}, for file-id : {} for message-token: {}; bearing the file-name: {} completed in : {}ms with status {}", jobExecution.getJobId(), fileId, messageToken, fileName, executionTime, exitStatus);
    }
}
