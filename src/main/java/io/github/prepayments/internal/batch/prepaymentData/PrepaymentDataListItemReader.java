package io.github.prepayments.internal.batch.prepaymentData;

import io.github.prepayments.config.FileUploadsProperties;
import io.github.prepayments.internal.batch.ListPartition;
import io.github.prepayments.internal.excel.ExcelFileDeserializer;
import io.github.prepayments.internal.model.PrepaymentDataEVM;
import io.github.prepayments.service.PrepsFileUploadService;
import org.slf4j.Logger;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * This implementation of the item-reader generates a partitioned list of prepayment-data-evm everytime the
 * read() function is called. The object lifecycle is scoped tp the lifetime of the job and the index is therefore
 * reset at the start of every job.
 */
@Scope("job")
public class PrepaymentDataListItemReader implements ItemReader<List<PrepaymentDataEVM>> {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(PrepaymentDataListItemReader.class);
    private final FileUploadsProperties fileUploadsProperties;

    private final ExcelFileDeserializer<PrepaymentDataEVM> deserializer;
    private final PrepsFileUploadService fileUploadService;
    private long fileId;

    private ListPartition<PrepaymentDataEVM> prepaymentsDataEVMPartition;

    PrepaymentDataListItemReader(final ExcelFileDeserializer<PrepaymentDataEVM> deserializer, final PrepsFileUploadService fileUploadService, @Value("#{jobParameters['fileId']}") long fileId,
                                final FileUploadsProperties fileUploadsProperties) {
        this.deserializer = deserializer;
        this.fileUploadService = fileUploadService;
        this.fileId = fileId;
        this.fileUploadsProperties = fileUploadsProperties;
    }

    @PostConstruct
    private void resetIndex() {

        final List<PrepaymentDataEVM> unProcessedItems =
            deserializer.deserialize(fileUploadService.findOne(fileId).orElseThrow(() -> new IllegalArgumentException(fileId + " was not found in the system")).getDataFile());

        prepaymentsDataEVMPartition = new ListPartition<>(fileUploadsProperties.getListSize(), unProcessedItems);

        log.info("Prepayments-Data items deserialized : {}", unProcessedItems.size());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Every time this method is called, it will return a List of unprocessed items the size of which is dictated by the maximumPageSize;
     * <p>
     * Once the list of unprocessed items hits zero, the method call will return null;
     * </p>
     */
    @Override
    public List<PrepaymentDataEVM> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        List<PrepaymentDataEVM> forProcessing = prepaymentsDataEVMPartition.getPartition();

        log.info("Returning list of {} items", forProcessing.size());

        return forProcessing.size() == 0 ? null : forProcessing;
    }
}
