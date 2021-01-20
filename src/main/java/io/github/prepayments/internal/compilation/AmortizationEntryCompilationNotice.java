package io.github.prepayments.internal.compilation;

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

    private String uploadToken;

    private String fileName;

    private AmortizationEntryCompilationType amortizationEntryCompilationType;

    private CompilationStatus compilationStatus = CompilationStatus.UN_DEFINED;
}
