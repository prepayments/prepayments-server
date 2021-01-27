package io.github.prepayments.internal.service;

import io.github.prepayments.service.dto.CompilationRequestDTO;
import org.springframework.stereotype.Service;

/**
 * This service uses a series of chained entities to process the request picking up a tag if the
 * tag is relevant. Concretely this service tags both prepayment-entry and amortization-entry compilation
 * requests. But these two require different services. So in order to keep services lean, we'll implement them
 * separately each with its own service.
 */
@Service("compilationRequestStatusUpdateService")
public class CompilationRequestStatusUpdateService implements StatusUpdateService<CompilationRequestDTO> {

    private final StatusUpdateServicesChain<CompilationRequestDTO> statusUpdateServicesChain;

    public CompilationRequestStatusUpdateService(final StatusUpdateServicesChain<CompilationRequestDTO> statusUpdateServicesChain) {
        this.statusUpdateServicesChain = statusUpdateServicesChain;
    }

    @Override
    public void updateInProgress(final CompilationRequestDTO requestEntity) {

        statusUpdateServicesChain.updateInProgress(requestEntity);
    }

    @Override
    public void updateStatusComplete(final CompilationRequestDTO requestEntity) {

        statusUpdateServicesChain.updateStatusComplete(requestEntity);
    }

    @Override
    public void updateStatusFailed(final CompilationRequestDTO requestEntity) {

        statusUpdateServicesChain.updateStatusFailed(requestEntity);
    }

    @Override
    public void updateStatusNullified(final CompilationRequestDTO requestEntity) {

        statusUpdateServicesChain.updateStatusNullified(requestEntity);
    }
}
