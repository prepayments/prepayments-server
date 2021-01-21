package io.github.prepayments.internal.batch.prepaymentEntryCompilation;

import com.google.common.collect.ImmutableList;
import io.github.prepayments.config.FileUploadsProperties;
import io.github.prepayments.domain.PrepaymentData;
import io.github.prepayments.internal.Mapping;
import io.github.prepayments.internal.batch.deletePrepaymentData.DeletePrepaymentDataItemProcessor;
import io.github.prepayments.internal.batch.deletePrepaymentData.DeletePrepaymentDataItemReader;
import io.github.prepayments.internal.batch.deletePrepaymentData.DeletePrepaymentDataItemWriter;
import io.github.prepayments.internal.batch.deletePrepaymentData.PrepaymentsDataDeletionJobListener;
import io.github.prepayments.internal.batch.prepaymentData.PrepaymentDataListItemReader;
import io.github.prepayments.internal.excel.ExcelFileDeserializer;
import io.github.prepayments.internal.model.PrepaymentDataEVM;
import io.github.prepayments.internal.service.BatchService;
import io.github.prepayments.internal.service.PrepaymentDataCompilationDeserializer;
import io.github.prepayments.repository.PrepaymentDataRepository;
import io.github.prepayments.service.PrepaymentDataQueryService;
import io.github.prepayments.service.PrepsFileUploadService;
import io.github.prepayments.service.dto.PrepaymentDataDTO;
import io.github.prepayments.service.dto.PrepaymentEntryDTO;
import io.github.prepayments.service.mapper.PrepaymentDataMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PrepaymentEntryCompilationConfig {


    @Value("#{jobParameters['fileId']}")
    private static long fileId;

    @Value("#{jobParameters['startUpTime']}")
    private static long startUpTime;

    @Value("#{jobParameters['messageToken']}")
    private static String messageToken;

    @Value("#{jobParameters['fileName']}")
    private static String fileName;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    @Qualifier("prepaymentDataQueryService")
    private PrepaymentDataQueryService prepaymentDataQueryService;

    @Autowired
    private FileUploadsProperties fileUploadsProperties;

    @Autowired
    private PrepaymentDataMapper prepaymentDataMapper;

    @Autowired
    private PrepaymentDataRepository prepaymentDataRepository;

    @Autowired
    @Qualifier("prepaymentEntryCompilationDeserializer")
    private PrepaymentDataCompilationDeserializer<PrepaymentEntryDTO> compilationDeserializer;


    @Bean
    public ItemWriter<List<PrepaymentData>> deletePrepaymentDataItemWriter() {
        return new DeletePrepaymentDataItemWriter(prepaymentDataRepository);
    }

    @Bean("prepaymentEntryCompilationProcessor")
    public ItemProcessor<List<PrepaymentDataDTO>, List<PrepaymentEntryDTO>> prepaymentEntryCompilationProcessor() {
        return new PrepaymentEntryCompilationProcessor(compilationDeserializer);
    }

    /**
     * Configuration for prepayments-data-list item reader implemented with a list-partition object to break down whole data set into chunks
     */
    @Bean("prepaymentEntryCompilationReader")
    @JobScope
    public ItemReader<List<PrepaymentDataDTO>> prepaymentEntryCompilationReader(@Value("#{jobParameters['messageToken']}") String messageToken) {

        return new PrepaymentEntryCompilationReader(messageToken, prepaymentDataQueryService, fileUploadsProperties);
    }

    @Bean
    @JobScope
    public PrepaymentsDataDeletionJobListener prepaymentsDataDeletionJobListener(@Value("#{jobParameters['fileId']}") long fileId,
                                                                                 @Value("#{jobParameters['startUpTime']}") long startUpTime,
                                                                                 @Value("#{jobParameters['messageToken']}") String messageToken,
                                                                                 @Value("#{jobParameters['fileName']}") String fileName) {

        return new PrepaymentsDataDeletionJobListener(fileId, startUpTime, fileName, messageToken);
    }

    @Bean("prepaymentDataDeletionJob")
    public Job prepaymentDataDeletionJob() {
        // @formatter:off
        return jobBuilderFactory.get("prepaymentDataDeletionJob")
            .preventRestart()
            .listener(prepaymentsDataDeletionJobListener(fileId, startUpTime, fileName, messageToken))
            .incrementer(new RunIdIncrementer())
            .flow(deletePrepaymentDataByToken())
            .end()
            .build();
        // @formatter:on
    }

    /**
     * Configuration of the step for reading prepayment-data-list from file
     */
    @Bean("deletePrepaymentDataByToken")
    public Step deletePrepaymentDataByToken() {
        // @formatter:off
        return stepBuilderFactory.get("deletePrepaymentDataByToken")
            .<List<PrepaymentDataDTO>, List<PrepaymentEntryDTO>>chunk(2)
            .reader(prepaymentEntryCompilationReader(messageToken))
            .processor(prepaymentEntryCompilationProcessor())
            .writer(deletePrepaymentDataItemWriter())
            .build();
        // @formatter:off
    }
}
