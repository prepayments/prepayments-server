package io.github.prepayments.internal.batch.amortizationEntryCompilation;

import io.github.prepayments.config.FileUploadsProperties;
import io.github.prepayments.internal.service.AmortizationDataMappingService;
import io.github.prepayments.internal.service.BatchService;
import io.github.prepayments.repository.PrepaymentDataRepository;
import io.github.prepayments.service.PrepaymentDataQueryService;
import io.github.prepayments.service.dto.AmortizationEntryDTO;
import io.github.prepayments.service.dto.PrepaymentDataDTO;
import io.github.prepayments.service.mapper.PrepaymentDataMapper;
import org.springframework.batch.core.Job;
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
public class AmortizationEntryCompilationConfig {


    @Value("#{jobParameters['fileId']}")
    private static long fileId;

    @Value("#{jobParameters['startUpTime']}")
    private static long startUpTime;

    @Value("#{jobParameters['uploadFileToken']}")
    private static String uploadFileToken;

    @Value("#{jobParameters['compilationToken']}")
    private static String compilationToken;

    @Value("#{jobParameters['fileName']}")
    private static String fileName;

    @Value("#{jobParameters['compilationRequestId']}")
    private static long compilationRequestId;

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
    @Qualifier("amortizationEntryBatchService")
    private BatchService<AmortizationEntryDTO> compilationBatchService;

    @Autowired
    private AmortizationDataMappingService<PrepaymentDataDTO, AmortizationEntryDTO> amortizationDataMappingService;


    @Bean
    public ItemWriter<List<AmortizationEntryDTO>> amortizationEntryCompilationWriter() {
        return new AmortizationEntryCompilationWriter(compilationBatchService);
    }

    @Bean("amortizationEntryCompilationProcessor")
    @JobScope
    public ItemProcessor<List<PrepaymentDataDTO>, List<AmortizationEntryDTO>> amortizationEntryCompilationProcessor(@Value("#{jobParameters['compilationToken']}") String compilationToken) {
        return new AmortizationEntryCompilationProcessor(amortizationDataMappingService, compilationToken);
    }

    /**
     * Configuration for prepayments-data-list item reader implemented with a list-partition object to break down whole data set into chunks
     */
    @Bean("prepaymentEntryCompilationReader")
    @JobScope
    public ItemReader<List<PrepaymentDataDTO>> amortizationEntryCompilationReader(@Value("#{jobParameters['uploadFileToken']}") String uploadFileToken) {

        return new AmortizationEntryCompilationReader(uploadFileToken, prepaymentDataQueryService, fileUploadsProperties);
    }

    @Bean
    @JobScope
    public AmortizationEntryCompilationListener amortizationEntryCompilationListener(@Value("#{jobParameters['fileId']}") long fileId, @Value("#{jobParameters['startUpTime']}") long startUpTime,
                                                                                   @Value("#{jobParameters['uploadFileToken']}") String uploadFileToken,
                                                                                   @Value("#{jobParameters['fileName']}") String fileName,
                                                                                   @Value("#{jobParameters['compilationRequestId']}") long compilationRequestId) {

        return new AmortizationEntryCompilationListener(fileId, startUpTime, fileName, uploadFileToken, compilationRequestId);
    }

    @Bean("amortizationEntryCompilationJob")
    public Job amortizationEntryCompilationJob() {
        // @formatter:off
        return jobBuilderFactory.get("amortizationEntryCompilationJob")
            .preventRestart()
            .listener(amortizationEntryCompilationListener(fileId, startUpTime, fileName, uploadFileToken, compilationRequestId))
            .incrementer(new RunIdIncrementer())
            .flow(amortizationEntryCompilationStep())
            .end()
            .build();
        // @formatter:on
    }

    /**
     * Configuration of the step for reading prepayment-data-list from file
     */
    @Bean("amortizationEntryCompilationStep")
    public Step amortizationEntryCompilationStep() {
        // @formatter:off
        return stepBuilderFactory.get("amortizationEntryCompilationStep")
            .<List<PrepaymentDataDTO>, List<AmortizationEntryDTO>>chunk(2)
            .reader(amortizationEntryCompilationReader(uploadFileToken))
            .processor(amortizationEntryCompilationProcessor(compilationToken))
            .writer(amortizationEntryCompilationWriter())
            .build();
        // @formatter:off
    }
}
