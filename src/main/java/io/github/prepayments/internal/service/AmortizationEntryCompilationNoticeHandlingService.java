package io.github.prepayments.internal.service;

import io.github.prepayments.internal.compilation.AmortizationEntryCompilationNotice;
import io.github.prepayments.internal.compilation.AmortizationEntryCompilationProcessorChain;
import io.github.prepayments.internal.compilation.CompilationStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service("amortizationEntryCompilationNoticeHandlingService")
public class AmortizationEntryCompilationNoticeHandlingService implements HandlingService<AmortizationEntryCompilationNotice> {

    public final AmortizationEntryCompilationProcessorChain<AmortizationEntryCompilationNotice> amortizationEntryCompilationChain;

    public AmortizationEntryCompilationNoticeHandlingService(final @Qualifier("amortizationEntryCompilationChain") AmortizationEntryCompilationProcessorChain<AmortizationEntryCompilationNotice> amortizationEntryCompilationChain) {
        this.amortizationEntryCompilationChain = amortizationEntryCompilationChain;
    }

    @Async
    @Override
    public void handle(final AmortizationEntryCompilationNotice payload) {

        payload.setCompilationStatus(CompilationStatus.STARTING);

        this.amortizationEntryCompilationChain.apply(payload);
    }
}
