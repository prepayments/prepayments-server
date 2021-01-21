package io.github.prepayments.internal.service;

/**
 * This interface takes an Id of a job and creates a tag and status update for the completion of the job
 */
public interface CompilationJobTag {

    void tag(long compilationRequestId);
}
