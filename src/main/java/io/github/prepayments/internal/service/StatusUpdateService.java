package io.github.prepayments.internal.service;

/**
 * This is a service for updating the status of the process when any of the methods is called with the
 * persistent entity in question.
 * It's abstract inorder to support multiple types of requests which might be compilation requests
 * reconciliation requests of reporting requests
 *
 * @param <E> Type of request-bearing entity
 */
public interface StatusUpdateService<E> {

    /**
     * This when the request process is still running
     *
     * @param requestEntity Type of request-bearing entity
     */
    void updateInProgress(E requestEntity);

    /**
     * This is when the request has ran to successful completion
     *
     * @param requestEntity Type of request-bearing entity
     */
    void updateStatusComplete(E requestEntity);

    /**
     * This is when there has been an error in processing the requist
     *
     * @param requestEntity Type of request-bearing entity
     */
    void updateStatusFailed(E requestEntity);

    /**
     * This is when the entity's compilation artifacts have been deleted or
     * made of none-effect
     *
     * @param requestEntity Type of request-bearing entity
     */
    void updateStatusNullified(E requestEntity);
}
