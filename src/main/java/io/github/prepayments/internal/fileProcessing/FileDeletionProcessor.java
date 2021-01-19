package io.github.prepayments.internal.fileProcessing;

/**
 * Interface for a service that is used to delete file and related file data
 *
 */
public interface FileDeletionProcessor<F, T> {

    T processFileDeletion(F fileUpload, T payload);
}
