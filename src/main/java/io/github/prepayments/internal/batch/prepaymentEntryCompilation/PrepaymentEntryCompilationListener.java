package io.github.prepayments.internal.batch.prepaymentEntryCompilation;

import io.github.prepayments.internal.service.CompilationJobTag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.context.annotation.Scope;

@Slf4j
@Scope("job")
public class PrepaymentEntryCompilationListener implements JobExecutionListener {


    private final long fileId;
    private final long startUpTime;
    private final String fileName;
    private final String messageToken;
    private final long compilationRequestId;

    private final CompilationJobTag compilationJobTag;

    public PrepaymentEntryCompilationListener(final long fileId, final long startUpTime, final String fileName, final String messageToken, final long compilationRequestId,
                                              final CompilationJobTag compilationJobTag) {
        this.fileId = fileId;
        this.startUpTime = startUpTime;
        this.fileName = fileName;
        this.messageToken = messageToken;
        this.compilationRequestId = compilationRequestId;
        this.compilationJobTag = compilationJobTag;
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

        log.info("Job Id {}, for file-id : {} for message-token: {}; bearing the file-name: {} completed in : {}ms with status {}", jobExecution.getJobId(), fileId, messageToken, fileName,
                 executionTime, exitStatus);

        compilationJobTag.tag(compilationRequestId);
    }
}
