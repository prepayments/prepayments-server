package io.github.prepayments.internal.service;

import java.util.List;

/**
 * This is a chain of status update services that runs several requests through a chain of instances. Each instance is assumed to bear appropriate enumeration to skip through requests it does not
 * handle
 */
public class StatusUpdateServicesChain<C> implements StatusUpdateService<C> {

    private final List<StatusUpdateService<C>> statusUpdateServices;

    public StatusUpdateServicesChain(final List<StatusUpdateService<C>> statusUpdateServices) {
        this.statusUpdateServices = statusUpdateServices;
    }

    void addService(StatusUpdateService<C> service) {

        statusUpdateServices.add(service);
    }

    @Override
    public void updateInProgress(final C requestEntity) {

        statusUpdateServices.forEach(service -> service.updateInProgress(requestEntity));
    }

    @Override
    public void updateStatusComplete(final C requestEntity) {

        statusUpdateServices.forEach(service -> service.updateStatusComplete(requestEntity));
    }

    @Override
    public void updateStatusFailed(final C requestEntity) {

        statusUpdateServices.forEach(service -> service.updateStatusFailed(requestEntity));
    }

    @Override
    public void updateStatusNullified(final C requestEntity) {

        statusUpdateServices.forEach(service -> service.updateStatusNullified(requestEntity));
    }
}
