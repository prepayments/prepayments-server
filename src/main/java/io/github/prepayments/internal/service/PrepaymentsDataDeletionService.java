package io.github.prepayments.internal.service;

import com.google.common.collect.ImmutableList;
import io.github.jhipster.service.filter.StringFilter;
import io.github.prepayments.service.PrepaymentDataQueryService;
import io.github.prepayments.service.PrepaymentDataService;
import io.github.prepayments.service.PrepsFileUploadService;
import io.github.prepayments.service.dto.PrepaymentDataCriteria;
import io.github.prepayments.service.dto.PrepaymentDataDTO;
import io.github.prepayments.service.dto.PrepsFileUploadDTO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This service deletes prepayments-data that has been updated with the file-upload of a given Id
 */
@Service("prepaymentsDataDeletionService")
public class PrepaymentsDataDeletionService implements HandlingService<PrepsFileUploadDTO> {

    private final PrepaymentDataService prepaymentDataService;
    private final PrepaymentDataQueryService prepaymentDataQueryService;

    public PrepaymentsDataDeletionService(final PrepaymentDataService prepaymentDataService, final PrepaymentDataQueryService prepaymentDataQueryService) {
        this.prepaymentDataService = prepaymentDataService;
        this.prepaymentDataQueryService = prepaymentDataQueryService;
    }

    /**
     *
     * @param fileUpload file-upload containing data to be deleted
     */
    @Async
    @Override
    public void handle(final PrepsFileUploadDTO fileUpload) {

        String uploadToken = fileUpload.getUploadToken();

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
