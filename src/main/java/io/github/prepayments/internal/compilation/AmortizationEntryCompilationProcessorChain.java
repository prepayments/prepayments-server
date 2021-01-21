package io.github.prepayments.internal.compilation;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AmortizationEntryCompilationProcessorChain<N> {

    private final List<AmortizationEntryCompilationProcessor<N>> compilationProcessors;

    public AmortizationEntryCompilationProcessorChain() {
        compilationProcessors = new CopyOnWriteArrayList<>();
    }

    public void addProcessor(final AmortizationEntryCompilationProcessor<N> compilationProcessor) {

        this.compilationProcessors.add(compilationProcessor);
    }

    public void apply(N compilationNotice) {

        compilationProcessors.forEach(processor -> {
            processor.compile(compilationNotice);
        });
    }
}
