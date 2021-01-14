package io.github.prepayments.internal.fileProcessing;

import io.github.prepayments.internal.model.FileNotification;

public interface FileUploadProcessor<T> {

    T processFileUpload(T fileUpload, FileNotification fileNotification);
}
