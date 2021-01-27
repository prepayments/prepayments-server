package io.github.prepayments.internal.service;

import io.github.prepayments.service.dto.CompilationRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Used spring to configure all the services making them fully unit testable, and why not
 *
 */
@Configuration
public class StatusUpdateServicesChainConfig {

    @Autowired
    @Qualifier("amortizationCompilationStatusUpdateService")
    private StatusUpdateService<CompilationRequestDTO> amortizationCompilationStatusUpdateService;

    @Autowired
    @Qualifier("prepaymentCompilationStatusUpdateService")
    private StatusUpdateService<CompilationRequestDTO> prepaymentCompilationStatusUpdateService;

    @Bean("statusUpdateServicesChain")
    public StatusUpdateServicesChain<CompilationRequestDTO> statusUpdateServicesChain() {

        StatusUpdateServicesChain<CompilationRequestDTO> statusUpdateServicesChain =
            new StatusUpdateServicesChain<>(new CopyOnWriteArrayList<>());

        // Add services to the chain
        statusUpdateServicesChain.addService(amortizationCompilationStatusUpdateService);
        statusUpdateServicesChain.addService(prepaymentCompilationStatusUpdateService);

        return statusUpdateServicesChain;
    }
}
