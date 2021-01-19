package io.github.prepayments.internal.fileProcessing;

/**
 * Interface for a service that is used to delete file and file data
 *
 */
public interface FileDeletionProcessor<T> {

    T processFileDeletion(T payload);
}
