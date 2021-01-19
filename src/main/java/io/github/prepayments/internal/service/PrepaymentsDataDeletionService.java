package io.github.prepayments.internal.service;

import com.google.common.collect.ImmutableList;
import io.github.jhipster.service.filter.StringFilter;
import io.github.prepayments.service.PrepaymentDataQueryService;
import io.github.prepayments.service.PrepaymentDataService;
import io.github.prepayments.service.PrepsFileUploadService;
import io.github.prepayments.service.dto.PrepaymentDataCriteria;
import io.github.prepayments.service.dto.PrepaymentDataDTO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This service deletes prepayments-data that has been updated with the file-upload of a given Id
 */
@Service("prepaymentsDataDeletionService")
public class PrepaymentsDataDeletionService implements HandlingService<Long> {

    private final PrepsFileUploadService fileUploadService;
    private final PrepaymentDataService prepaymentDataService;
    private final PrepaymentDataQueryService prepaymentDataQueryService;

    public PrepaymentsDataDeletionService(final PrepsFileUploadService fileUploadService, final PrepaymentDataService prepaymentDataService,
                                          final PrepaymentDataQueryService prepaymentDataQueryService) {
        this.fileUploadService = fileUploadService;
        this.prepaymentDataService = prepaymentDataService;
        this.prepaymentDataQueryService = prepaymentDataQueryService;
    }

    /**
     *
     * @param payload The Id of the file-upload data to be deleted
     */
    @Async
    @Override
    public void handle(final Long payload) {

        String uploadToken = fileUploadService.findOne(payload).get().getUploadToken();

        PrepaymentDataCriteria deleteFileCriteria = new PrepaymentDataCriteria();
        StringFilter uploadTokenFilter = new StringFilter();
        uploadTokenFilter.setEquals(uploadToken);
        deleteFileCriteria.setUploadToken(uploadTokenFilter);

        // @formatter:off  fetch ids for deletion
        List<Long> deletableIds = prepaymentDataQueryService.findByCriteria(deleteFileCriteria)
                                                            .stream()
                                                            .map(PrepaymentDataDTO::getId)
                                                            .collect(ImmutableList.toImmutableList());
        // @formatter:on

        // delete references
        deletableIds.forEach(prepaymentDataService::delete);
    }
}
