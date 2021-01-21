package io.github.prepayments.internal.compilation;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.github.prepayments.domain.enumeration.CompilationType.AMORTIZATION_ENTRY_COMPILATION;
import static io.github.prepayments.domain.enumeration.CompilationType.PREPAYMENT_ENTRY_COMPILATION;

@Configuration
public class CompilationProcessorContainment {

    @Autowired
    private JobLauncher jobLauncher;

    // TODO Implement amortization-entry-compilation-job
    @Autowired
    @Qualifier("amortizationEntryCompilationJob")
    private Job amortizationEntryCompilationJob;

    // TODO Implement amortization-entry-prepayment-compilation-job
    @Autowired
    @Qualifier("prepaymentEntryCompilationJob")
    private Job prepaymentEntryCompilationJob;

    @Bean
    public AmortizationEntryCompilationProcessorChain<AmortizationEntryCompilationNotice> amortizationEntryCompilationChain() {

        AmortizationEntryCompilationProcessorChain<AmortizationEntryCompilationNotice> theChain = new AmortizationEntryCompilationProcessorChain<>();

        theChain.addProcessor(new BatchSupportedAmortizationEntryCompilationProcessor(jobLauncher, amortizationEntryCompilationJob, AMORTIZATION_ENTRY_COMPILATION));
        theChain.addProcessor(new BatchSupportedAmortizationEntryCompilationProcessor(jobLauncher, prepaymentEntryCompilationJob, PREPAYMENT_ENTRY_COMPILATION));

        return theChain;
    }
}
