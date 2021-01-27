package io.github.prepayments.internal.service;

import io.github.prepayments.domain.enumeration.CompilationStatus;
import io.github.prepayments.domain.enumeration.CompilationType;
import io.github.prepayments.service.CompilationRequestService;
import io.github.prepayments.service.dto.CompilationRequestDTO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Implements the status-update-service for amortization-compilation request jobs. Here we can call services specific to amortization-entry and do stuff like
 * <p>
 * TODO add statistic of affected rows
 * TODO add statistic of time-taken
 * TODO add statistic of when the job started
 * TODO if possible add data about who did the job
 */
@Service("amortizationCompilationStatusUpdateService")
public class AmortizationCompilationStatusUpdateService implements StatusUpdateService<CompilationRequestDTO> {

    // todo private final AmortizationEntryQueryService amortizationEntryQueryService;
    private final CompilationRequestService compilationRequestService;


    public AmortizationCompilationStatusUpdateService(final CompilationRequestService compilationRequestService) {
        this.compilationRequestService = compilationRequestService;
    }

    @Async
    @Override
    public void updateInProgress(final CompilationRequestDTO requestEntity) {

        if (requestEntity.getCompilationType() == CompilationType.AMORTIZATION_ENTRY_COMPILATION) {

            // TODO LocalDateTime when = LocalDateTime.now();

            compilationRequestService.findOne(requestEntity.getId()).ifPresent(found -> {

                found.setCompilationStatus(CompilationStatus.IN_PROGRESS);

                // TODO found.setWhenJobStarted(when);

                // feeling compelled to be explicit
                compilationRequestService.save(found);
            });
        }
    }

    @Async
    @Override
    public void updateStatusComplete(final CompilationRequestDTO requestEntity) {

        if (requestEntity.getCompilationType() == CompilationType.AMORTIZATION_ENTRY_COMPILATION) {

            // TODO StringFilter compilationTokenFilter = new StringFilter();
            // TODO compilationTokenFilter.setEquals(requestEntity.getCompilationToken());
            // TODO PrepaymentEntryCriteria prepaymentEntryCriteria = new PrepaymentEntryCriteria();
            // TODO prepaymentEntryCriteria.setCompilationToken(compilationTokenFilter);
            // TODO long rowsAffected = amortizationEntryQueryService.countByCriteria(prepaymentEntryCriteria);

            compilationRequestService.findOne(requestEntity.getId()).ifPresent(found -> {

                found.setCompilationStatus(CompilationStatus.COMPLETE);

                // TODO int SecondsTaken = LocalDateTime.now().minus(found.getWhenJobStarted(), ChronoUnit.SECONDS).getSecond();

                // TODO found.timeTaken(secondsTaken)

                // TODO found.setRowsAffected(rowsAffected);

                // feeling compelled to be explicit
                compilationRequestService.save(found);
            });
        }
    }

    @Async
    @Override
    public void updateStatusFailed(final CompilationRequestDTO requestEntity) {

        if (requestEntity.getCompilationType() == CompilationType.AMORTIZATION_ENTRY_COMPILATION) {

            // todo update in error logging services

            compilationRequestService.findOne(requestEntity.getId()).ifPresent(found -> {

                found.setCompilationStatus(CompilationStatus.FAILED);

                // feeling compelled to be explicit
                compilationRequestService.save(found);
            });
        }

    }

    @Async
    @Override
    public void updateStatusNullified(final CompilationRequestDTO requestEntity) {

        if (requestEntity.getCompilationType() == CompilationType.AMORTIZATION_ENTRY_COMPILATION) {

            // TODO StringFilter compilationTokenFilter = new StringFilter();
            // TODO compilationTokenFilter.setEquals(requestEntity.getCompilationToken());
            // TODO PrepaymentEntryCriteria prepaymentEntryCriteria = new PrepaymentEntryCriteria();
            // TODO prepaymentEntryCriteria.setCompilationToken(compilationTokenFilter);
            // TODO long rowsAffected = amortizationEntryQueryService.countByCriteria(prepaymentEntryCriteria);

            compilationRequestService.findOne(requestEntity.getId()).ifPresent(found -> {

                found.setCompilationStatus(CompilationStatus.NULLIFIED);

                // TODO int SecondsTaken = LocalDateTime.now().minus(found.getWhenJobStarted(), ChronoUnit.SECONDS).getSecond();

                // TODO found.timeTaken(secondsTaken)

                // TODO found.setRowsAffected(rowsAffected);

                // feeling compelled to be explicit
                compilationRequestService.save(found);
            });
        }
    }
}
