package io.github.prepayments.internal.batch.amortizationEntryCompilation;

import io.github.jhipster.service.filter.StringFilter;
import io.github.prepayments.config.FileUploadsProperties;
import io.github.prepayments.internal.batch.ListPartition;
import io.github.prepayments.service.PrepaymentDataQueryService;
import io.github.prepayments.service.dto.PrepaymentDataCriteria;
import io.github.prepayments.service.dto.PrepaymentDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Scope("job")
public class AmortizationEntryCompilationReader implements ItemReader<List<PrepaymentDataDTO>> {


    private String messageToken;
    private PrepaymentDataCriteria prepaymentDataCriteria;
    private final PrepaymentDataQueryService prepaymentDataQueryService;


    private final FileUploadsProperties fileUploadsProperties;

    private ListPartition<PrepaymentDataDTO> prepaymentsDataDTOPartition;

    AmortizationEntryCompilationReader(@Value("#{jobParameters['messageToken']}") String messageToken, final PrepaymentDataQueryService prepaymentDataQueryService,
                                     final FileUploadsProperties fileUploadsProperties) {
        this.messageToken = messageToken;
        this.prepaymentDataQueryService = prepaymentDataQueryService;
        this.fileUploadsProperties = fileUploadsProperties;
    }

    /**
     * Initialize resources at the beginning of the job
     */
    @PostConstruct
    private void initializeResources() {

        log.info("Lining up prepayments-data with the token : {}", messageToken);

        prepaymentDataCriteria = new PrepaymentDataCriteria();
        StringFilter uploadTokenFilter = new StringFilter();
        uploadTokenFilter.setEquals(messageToken);
        prepaymentDataCriteria.setUploadToken(uploadTokenFilter);

        final List<PrepaymentDataDTO> unProcessedItems = prepaymentDataQueryService.findByCriteria(prepaymentDataCriteria);

        prepaymentsDataDTOPartition = new ListPartition<>(fileUploadsProperties.getLargeUploads(), unProcessedItems);

        log.info("Prepayments-Data items for compilation : {}", unProcessedItems.size());
    }

    @Override
    public List<PrepaymentDataDTO> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        List<PrepaymentDataDTO> forProcessing = prepaymentsDataDTOPartition.getPartition();

        log.info("Returning list of {} items", forProcessing.size());

        // return null if the size is zero
        return forProcessing.size() == 0 ? null : forProcessing;
    }
}
