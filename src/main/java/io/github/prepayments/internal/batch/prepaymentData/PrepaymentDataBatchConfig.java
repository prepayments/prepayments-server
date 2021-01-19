package io.github.prepayments.internal.batch.prepaymentData;

import com.google.common.collect.ImmutableList;
import io.github.prepayments.config.FileUploadsProperties;
import io.github.prepayments.internal.Mapping;
import io.github.prepayments.internal.excel.ExcelFileDeserializer;
import io.github.prepayments.internal.model.PrepaymentDataEVM;
import io.github.prepayments.internal.service.BatchService;
import io.github.prepayments.service.PrepsFileUploadService;
import io.github.prepayments.service.dto.PrepaymentDataDTO;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PrepaymentDataBatchConfig {

    @Value("#{jobParameters['fileId']}")
    private static long fileId;

    @Value("#{jobParameters['startUpTime']}")
    private static long startUpTime;

    @Value("#{jobParameters['messageToken']}")
    private static String messageToken;

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final ExcelFileDeserializer<PrepaymentDataEVM> deserializer;

    private final PrepsFileUploadService fileUploadService;

    private final FileUploadsProperties fileUploadsProperties;

    private final JobExecutionListener persistenceJobListener;

    private final BatchService<PrepaymentDataDTO> batchService;

    private final Mapping<PrepaymentDataEVM, PrepaymentDataDTO> mapping;

    public PrepaymentDataBatchConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, ExcelFileDeserializer<PrepaymentDataEVM> deserializer,
                                     PrepsFileUploadService fileUploadService, FileUploadsProperties fileUploadsProperties, JobExecutionListener persistenceJobListener,
                                     BatchService<PrepaymentDataDTO> batchService, Mapping<PrepaymentDataEVM, PrepaymentDataDTO> mapping) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.deserializer = deserializer;
        this.fileUploadService = fileUploadService;
        this.fileUploadsProperties = fileUploadsProperties;
        this.persistenceJobListener = persistenceJobListener;
        this.batchService = batchService;
        this.mapping = mapping;
    }

    @Bean("prepaymentDataListPersistenceJob")
    public Job prepaymentDataListPersistenceJob() {
        // @formatter:off
        return jobBuilderFactory.get("prepaymentDataListPersistenceJob")
            .preventRestart()
            .listener(persistenceJobListener)
            .incrementer(new RunIdIncrementer())
            .flow(readPrepaymentDataListFromFile())
            .end()
            .build();
        // @formatter:on
    }

    @Bean
    public ItemWriter<List<PrepaymentDataDTO>> prepaymentDataListItemWriter() {
        return items -> items.stream().peek(batchService::save).forEach(batchService::index);
    }

    @Bean
    public ItemProcessor<List<PrepaymentDataEVM>, List<PrepaymentDataDTO>> prepaymentDataListItemProcessor() {
        return evms -> evms.stream().map(mapping::toValue2).collect(ImmutableList.toImmutableList());
    }

    /**
     * Configuration of the step for reading prepayment-data-list from file
     */
    @Bean("readPrepaymentDataListFromFile")
    public Step readPrepaymentDataListFromFile() {
        // @formatter:off
        return stepBuilderFactory.get("readPrepaymentDataListFromFile")
            .<List<PrepaymentDataEVM>, List<PrepaymentDataDTO>>chunk(2)
            .reader(prepaymentDataListItemReader(fileId, messageToken))
            .processor(prepaymentDataListItemProcessor())
            .writer(prepaymentDataListItemWriter())
            .build();
        // @formatter:off
    }

    /**
     * Configuration for prepayments-data-list item reader implemented with a list-partition object
     * to break down whole data set into chunks
     *
     */
    @Bean
    @JobScope
    public PrepaymentDataListItemReader prepaymentDataListItemReader(@Value("#{jobParameters['fileId']}") long fileId, @Value("#{jobParameters['messageToken']}") String messageToken) {

        return new PrepaymentDataListItemReader(deserializer, fileUploadService, fileId, messageToken, fileUploadsProperties);
    }
}
