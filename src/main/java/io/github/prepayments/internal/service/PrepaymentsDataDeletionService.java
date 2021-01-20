package io.github.prepayments.internal.service;

import io.github.jhipster.service.filter.StringFilter;
import io.github.prepayments.domain.PrepaymentData;
import io.github.prepayments.repository.PrepaymentDataRepository;
import io.github.prepayments.service.PrepaymentDataQueryService;
import io.github.prepayments.service.dto.PrepaymentDataCriteria;
import io.github.prepayments.service.dto.PrepsFileUploadDTO;
import io.github.prepayments.service.mapper.PrepaymentDataMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This service deletes prepayments-data that has been updated with the file-upload of a given Id
 */
// TODO To delete
@Service("prepaymentsDataDeletionService")
public class PrepaymentsDataDeletionService implements HandlingService<PrepsFileUploadDTO> {

    private final PrepaymentDataRepository prepaymentDataRepository;
    private final PrepaymentDataMapper prepaymentDataMapper;
    private final PrepaymentDataQueryService prepaymentDataQueryService;

    public PrepaymentsDataDeletionService(final PrepaymentDataRepository prepaymentDataRepository, final PrepaymentDataMapper prepaymentDataMapper,
                                          final PrepaymentDataQueryService prepaymentDataQueryService) {
        this.prepaymentDataRepository = prepaymentDataRepository;
        this.prepaymentDataMapper = prepaymentDataMapper;
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
        List<PrepaymentData> deletable = prepaymentDataMapper.toEntity(prepaymentDataQueryService.findByCriteria(deleteFileCriteria));
        // @formatter:on

        prepaymentDataRepository.deleteAll(deletable);
    }
}
