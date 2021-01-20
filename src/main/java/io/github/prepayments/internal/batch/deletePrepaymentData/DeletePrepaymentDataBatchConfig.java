package io.github.prepayments.internal.batch.deletePrepaymentData;

import io.github.prepayments.config.FileUploadsProperties;
import io.github.prepayments.domain.PrepaymentData;
import io.github.prepayments.repository.PrepaymentDataRepository;
import io.github.prepayments.service.PrepaymentDataQueryService;
import io.github.prepayments.service.dto.PrepaymentDataDTO;
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

// TODO Implement job listener to mark a message-token entity as deleted
@Configuration
public class DeletePrepaymentDataBatchConfig {


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


    @Bean
    public ItemWriter<List<PrepaymentData>> deletePrepaymentDataItemWriter() {
        return new DeletePrepaymentDataItemWriter(prepaymentDataRepository);
    }

    @Bean
    public ItemProcessor<List<PrepaymentDataDTO>, List<PrepaymentData>> deletePrepaymentDataItemProcessor() {
        return new DeletePrepaymentDataItemProcessor(prepaymentDataMapper);
    }

    /**
     * Configuration for prepayments-data-list item reader implemented with a list-partition object to break down whole data set into chunks
     */
    @Bean
    @JobScope
    public ItemReader<List<PrepaymentDataDTO>> deletePrepaymentDataItemReader(@Value("#{jobParameters['messageToken']}") String messageToken) {

        return new DeletePrepaymentDataItemReader(messageToken, prepaymentDataQueryService, fileUploadsProperties);
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
            .<List<PrepaymentDataDTO>, List<PrepaymentData>>chunk(2)
            .reader(deletePrepaymentDataItemReader(messageToken))
            .processor(deletePrepaymentDataItemProcessor())
            .writer(deletePrepaymentDataItemWriter())
            .build();
        // @formatter:off
    }
}
