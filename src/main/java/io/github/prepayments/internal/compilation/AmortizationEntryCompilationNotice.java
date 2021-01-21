package io.github.prepayments.internal.compilation;

import io.github.prepayments.domain.enumeration.CompilationStatus;
import io.github.prepayments.domain.enumeration.CompilationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Data
public class AmortizationEntryCompilationNotice {

    private long fileId;

    private long timestamp;

    private long compilationRequestId;

    private String uploadToken;

    private String fileName;

    private CompilationType compilationType;

    private CompilationStatus compilationStatus;
}
