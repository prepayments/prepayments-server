package io.github.prepayments.internal.compilation;

/**
 * This interface compiles prepayment-data into its factors
 */
public interface AmortizationEntryCompilationProcessor<N> {

    N compile(N notification);
}
