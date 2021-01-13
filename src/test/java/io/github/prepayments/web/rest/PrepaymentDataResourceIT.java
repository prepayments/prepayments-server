package io.github.prepayments.web.rest;

import io.github.prepayments.PrepaymentsApp;
import io.github.prepayments.domain.PrepaymentData;
import io.github.prepayments.repository.PrepaymentDataRepository;
import io.github.prepayments.repository.search.PrepaymentDataSearchRepository;
import io.github.prepayments.service.PrepaymentDataService;
import io.github.prepayments.service.dto.PrepaymentDataDTO;
import io.github.prepayments.service.mapper.PrepaymentDataMapper;
import io.github.prepayments.service.dto.PrepaymentDataCriteria;
import io.github.prepayments.service.PrepaymentDataQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link PrepaymentDataResource} REST controller.
 */
@SpringBootTest(classes = PrepaymentsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class PrepaymentDataResourceIT {

    private static final String DEFAULT_ACCOUNT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ACCOUNT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_ACCOUNT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_ACCOUNT_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_EXPENSE_ACCOUNT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_EXPENSE_ACCOUNT_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_PREPAYMENT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PREPAYMENT_NUMBER = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_PREPAYMENT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PREPAYMENT_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_PREPAYMENT_DATE = LocalDate.ofEpochDay(-1L);

    private static final BigDecimal DEFAULT_PREPAYMENT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_PREPAYMENT_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_PREPAYMENT_AMOUNT = new BigDecimal(1 - 1);

    private static final Integer DEFAULT_PREPAYMENT_PERIODS = 1;
    private static final Integer UPDATED_PREPAYMENT_PERIODS = 2;
    private static final Integer SMALLER_PREPAYMENT_PERIODS = 1 - 1;

    @Autowired
    private PrepaymentDataRepository prepaymentDataRepository;

    @Autowired
    private PrepaymentDataMapper prepaymentDataMapper;

    @Autowired
    private PrepaymentDataService prepaymentDataService;

    /**
     * This repository is mocked in the io.github.prepayments.repository.search test package.
     *
     * @see io.github.prepayments.repository.search.PrepaymentDataSearchRepositoryMockConfiguration
     */
    @Autowired
    private PrepaymentDataSearchRepository mockPrepaymentDataSearchRepository;

    @Autowired
    private PrepaymentDataQueryService prepaymentDataQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPrepaymentDataMockMvc;

    private PrepaymentData prepaymentData;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PrepaymentData createEntity(EntityManager em) {
        PrepaymentData prepaymentData = new PrepaymentData()
            .accountName(DEFAULT_ACCOUNT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .accountNumber(DEFAULT_ACCOUNT_NUMBER)
            .expenseAccountNumber(DEFAULT_EXPENSE_ACCOUNT_NUMBER)
            .prepaymentNumber(DEFAULT_PREPAYMENT_NUMBER)
            .prepaymentDate(DEFAULT_PREPAYMENT_DATE)
            .prepaymentAmount(DEFAULT_PREPAYMENT_AMOUNT)
            .prepaymentPeriods(DEFAULT_PREPAYMENT_PERIODS);
        return prepaymentData;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PrepaymentData createUpdatedEntity(EntityManager em) {
        PrepaymentData prepaymentData = new PrepaymentData()
            .accountName(UPDATED_ACCOUNT_NAME)
            .description(UPDATED_DESCRIPTION)
            .accountNumber(UPDATED_ACCOUNT_NUMBER)
            .expenseAccountNumber(UPDATED_EXPENSE_ACCOUNT_NUMBER)
            .prepaymentNumber(UPDATED_PREPAYMENT_NUMBER)
            .prepaymentDate(UPDATED_PREPAYMENT_DATE)
            .prepaymentAmount(UPDATED_PREPAYMENT_AMOUNT)
            .prepaymentPeriods(UPDATED_PREPAYMENT_PERIODS);
        return prepaymentData;
    }

    @BeforeEach
    public void initTest() {
        prepaymentData = createEntity(em);
    }

    @Test
    @Transactional
    public void createPrepaymentData() throws Exception {
        int databaseSizeBeforeCreate = prepaymentDataRepository.findAll().size();
        // Create the PrepaymentData
        PrepaymentDataDTO prepaymentDataDTO = prepaymentDataMapper.toDto(prepaymentData);
        restPrepaymentDataMockMvc.perform(post("/api/prepayment-data")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepaymentDataDTO)))
            .andExpect(status().isCreated());

        // Validate the PrepaymentData in the database
        List<PrepaymentData> prepaymentDataList = prepaymentDataRepository.findAll();
        assertThat(prepaymentDataList).hasSize(databaseSizeBeforeCreate + 1);
        PrepaymentData testPrepaymentData = prepaymentDataList.get(prepaymentDataList.size() - 1);
        assertThat(testPrepaymentData.getAccountName()).isEqualTo(DEFAULT_ACCOUNT_NAME);
        assertThat(testPrepaymentData.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPrepaymentData.getAccountNumber()).isEqualTo(DEFAULT_ACCOUNT_NUMBER);
        assertThat(testPrepaymentData.getExpenseAccountNumber()).isEqualTo(DEFAULT_EXPENSE_ACCOUNT_NUMBER);
        assertThat(testPrepaymentData.getPrepaymentNumber()).isEqualTo(DEFAULT_PREPAYMENT_NUMBER);
        assertThat(testPrepaymentData.getPrepaymentDate()).isEqualTo(DEFAULT_PREPAYMENT_DATE);
        assertThat(testPrepaymentData.getPrepaymentAmount()).isEqualTo(DEFAULT_PREPAYMENT_AMOUNT);
        assertThat(testPrepaymentData.getPrepaymentPeriods()).isEqualTo(DEFAULT_PREPAYMENT_PERIODS);

        // Validate the PrepaymentData in Elasticsearch
        verify(mockPrepaymentDataSearchRepository, times(1)).save(testPrepaymentData);
    }

    @Test
    @Transactional
    public void createPrepaymentDataWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = prepaymentDataRepository.findAll().size();

        // Create the PrepaymentData with an existing ID
        prepaymentData.setId(1L);
        PrepaymentDataDTO prepaymentDataDTO = prepaymentDataMapper.toDto(prepaymentData);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPrepaymentDataMockMvc.perform(post("/api/prepayment-data")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepaymentDataDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PrepaymentData in the database
        List<PrepaymentData> prepaymentDataList = prepaymentDataRepository.findAll();
        assertThat(prepaymentDataList).hasSize(databaseSizeBeforeCreate);

        // Validate the PrepaymentData in Elasticsearch
        verify(mockPrepaymentDataSearchRepository, times(0)).save(prepaymentData);
    }


    @Test
    @Transactional
    public void checkPrepaymentPeriodsIsRequired() throws Exception {
        int databaseSizeBeforeTest = prepaymentDataRepository.findAll().size();
        // set the field null
        prepaymentData.setPrepaymentPeriods(null);

        // Create the PrepaymentData, which fails.
        PrepaymentDataDTO prepaymentDataDTO = prepaymentDataMapper.toDto(prepaymentData);


        restPrepaymentDataMockMvc.perform(post("/api/prepayment-data")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepaymentDataDTO)))
            .andExpect(status().isBadRequest());

        List<PrepaymentData> prepaymentDataList = prepaymentDataRepository.findAll();
        assertThat(prepaymentDataList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPrepaymentData() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList
        restPrepaymentDataMockMvc.perform(get("/api/prepayment-data?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prepaymentData.getId().intValue())))
            .andExpect(jsonPath("$.[*].accountName").value(hasItem(DEFAULT_ACCOUNT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].accountNumber").value(hasItem(DEFAULT_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].expenseAccountNumber").value(hasItem(DEFAULT_EXPENSE_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].prepaymentNumber").value(hasItem(DEFAULT_PREPAYMENT_NUMBER)))
            .andExpect(jsonPath("$.[*].prepaymentDate").value(hasItem(DEFAULT_PREPAYMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].prepaymentAmount").value(hasItem(DEFAULT_PREPAYMENT_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].prepaymentPeriods").value(hasItem(DEFAULT_PREPAYMENT_PERIODS)));
    }
    
    @Test
    @Transactional
    public void getPrepaymentData() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get the prepaymentData
        restPrepaymentDataMockMvc.perform(get("/api/prepayment-data/{id}", prepaymentData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(prepaymentData.getId().intValue()))
            .andExpect(jsonPath("$.accountName").value(DEFAULT_ACCOUNT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.accountNumber").value(DEFAULT_ACCOUNT_NUMBER))
            .andExpect(jsonPath("$.expenseAccountNumber").value(DEFAULT_EXPENSE_ACCOUNT_NUMBER))
            .andExpect(jsonPath("$.prepaymentNumber").value(DEFAULT_PREPAYMENT_NUMBER))
            .andExpect(jsonPath("$.prepaymentDate").value(DEFAULT_PREPAYMENT_DATE.toString()))
            .andExpect(jsonPath("$.prepaymentAmount").value(DEFAULT_PREPAYMENT_AMOUNT.intValue()))
            .andExpect(jsonPath("$.prepaymentPeriods").value(DEFAULT_PREPAYMENT_PERIODS));
    }


    @Test
    @Transactional
    public void getPrepaymentDataByIdFiltering() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        Long id = prepaymentData.getId();

        defaultPrepaymentDataShouldBeFound("id.equals=" + id);
        defaultPrepaymentDataShouldNotBeFound("id.notEquals=" + id);

        defaultPrepaymentDataShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPrepaymentDataShouldNotBeFound("id.greaterThan=" + id);

        defaultPrepaymentDataShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPrepaymentDataShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllPrepaymentDataByAccountNameIsEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where accountName equals to DEFAULT_ACCOUNT_NAME
        defaultPrepaymentDataShouldBeFound("accountName.equals=" + DEFAULT_ACCOUNT_NAME);

        // Get all the prepaymentDataList where accountName equals to UPDATED_ACCOUNT_NAME
        defaultPrepaymentDataShouldNotBeFound("accountName.equals=" + UPDATED_ACCOUNT_NAME);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByAccountNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where accountName not equals to DEFAULT_ACCOUNT_NAME
        defaultPrepaymentDataShouldNotBeFound("accountName.notEquals=" + DEFAULT_ACCOUNT_NAME);

        // Get all the prepaymentDataList where accountName not equals to UPDATED_ACCOUNT_NAME
        defaultPrepaymentDataShouldBeFound("accountName.notEquals=" + UPDATED_ACCOUNT_NAME);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByAccountNameIsInShouldWork() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where accountName in DEFAULT_ACCOUNT_NAME or UPDATED_ACCOUNT_NAME
        defaultPrepaymentDataShouldBeFound("accountName.in=" + DEFAULT_ACCOUNT_NAME + "," + UPDATED_ACCOUNT_NAME);

        // Get all the prepaymentDataList where accountName equals to UPDATED_ACCOUNT_NAME
        defaultPrepaymentDataShouldNotBeFound("accountName.in=" + UPDATED_ACCOUNT_NAME);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByAccountNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where accountName is not null
        defaultPrepaymentDataShouldBeFound("accountName.specified=true");

        // Get all the prepaymentDataList where accountName is null
        defaultPrepaymentDataShouldNotBeFound("accountName.specified=false");
    }
                @Test
    @Transactional
    public void getAllPrepaymentDataByAccountNameContainsSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where accountName contains DEFAULT_ACCOUNT_NAME
        defaultPrepaymentDataShouldBeFound("accountName.contains=" + DEFAULT_ACCOUNT_NAME);

        // Get all the prepaymentDataList where accountName contains UPDATED_ACCOUNT_NAME
        defaultPrepaymentDataShouldNotBeFound("accountName.contains=" + UPDATED_ACCOUNT_NAME);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByAccountNameNotContainsSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where accountName does not contain DEFAULT_ACCOUNT_NAME
        defaultPrepaymentDataShouldNotBeFound("accountName.doesNotContain=" + DEFAULT_ACCOUNT_NAME);

        // Get all the prepaymentDataList where accountName does not contain UPDATED_ACCOUNT_NAME
        defaultPrepaymentDataShouldBeFound("accountName.doesNotContain=" + UPDATED_ACCOUNT_NAME);
    }


    @Test
    @Transactional
    public void getAllPrepaymentDataByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where description equals to DEFAULT_DESCRIPTION
        defaultPrepaymentDataShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the prepaymentDataList where description equals to UPDATED_DESCRIPTION
        defaultPrepaymentDataShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where description not equals to DEFAULT_DESCRIPTION
        defaultPrepaymentDataShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the prepaymentDataList where description not equals to UPDATED_DESCRIPTION
        defaultPrepaymentDataShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultPrepaymentDataShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the prepaymentDataList where description equals to UPDATED_DESCRIPTION
        defaultPrepaymentDataShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where description is not null
        defaultPrepaymentDataShouldBeFound("description.specified=true");

        // Get all the prepaymentDataList where description is null
        defaultPrepaymentDataShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllPrepaymentDataByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where description contains DEFAULT_DESCRIPTION
        defaultPrepaymentDataShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the prepaymentDataList where description contains UPDATED_DESCRIPTION
        defaultPrepaymentDataShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where description does not contain DEFAULT_DESCRIPTION
        defaultPrepaymentDataShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the prepaymentDataList where description does not contain UPDATED_DESCRIPTION
        defaultPrepaymentDataShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllPrepaymentDataByAccountNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where accountNumber equals to DEFAULT_ACCOUNT_NUMBER
        defaultPrepaymentDataShouldBeFound("accountNumber.equals=" + DEFAULT_ACCOUNT_NUMBER);

        // Get all the prepaymentDataList where accountNumber equals to UPDATED_ACCOUNT_NUMBER
        defaultPrepaymentDataShouldNotBeFound("accountNumber.equals=" + UPDATED_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByAccountNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where accountNumber not equals to DEFAULT_ACCOUNT_NUMBER
        defaultPrepaymentDataShouldNotBeFound("accountNumber.notEquals=" + DEFAULT_ACCOUNT_NUMBER);

        // Get all the prepaymentDataList where accountNumber not equals to UPDATED_ACCOUNT_NUMBER
        defaultPrepaymentDataShouldBeFound("accountNumber.notEquals=" + UPDATED_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByAccountNumberIsInShouldWork() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where accountNumber in DEFAULT_ACCOUNT_NUMBER or UPDATED_ACCOUNT_NUMBER
        defaultPrepaymentDataShouldBeFound("accountNumber.in=" + DEFAULT_ACCOUNT_NUMBER + "," + UPDATED_ACCOUNT_NUMBER);

        // Get all the prepaymentDataList where accountNumber equals to UPDATED_ACCOUNT_NUMBER
        defaultPrepaymentDataShouldNotBeFound("accountNumber.in=" + UPDATED_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByAccountNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where accountNumber is not null
        defaultPrepaymentDataShouldBeFound("accountNumber.specified=true");

        // Get all the prepaymentDataList where accountNumber is null
        defaultPrepaymentDataShouldNotBeFound("accountNumber.specified=false");
    }
                @Test
    @Transactional
    public void getAllPrepaymentDataByAccountNumberContainsSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where accountNumber contains DEFAULT_ACCOUNT_NUMBER
        defaultPrepaymentDataShouldBeFound("accountNumber.contains=" + DEFAULT_ACCOUNT_NUMBER);

        // Get all the prepaymentDataList where accountNumber contains UPDATED_ACCOUNT_NUMBER
        defaultPrepaymentDataShouldNotBeFound("accountNumber.contains=" + UPDATED_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByAccountNumberNotContainsSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where accountNumber does not contain DEFAULT_ACCOUNT_NUMBER
        defaultPrepaymentDataShouldNotBeFound("accountNumber.doesNotContain=" + DEFAULT_ACCOUNT_NUMBER);

        // Get all the prepaymentDataList where accountNumber does not contain UPDATED_ACCOUNT_NUMBER
        defaultPrepaymentDataShouldBeFound("accountNumber.doesNotContain=" + UPDATED_ACCOUNT_NUMBER);
    }


    @Test
    @Transactional
    public void getAllPrepaymentDataByExpenseAccountNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where expenseAccountNumber equals to DEFAULT_EXPENSE_ACCOUNT_NUMBER
        defaultPrepaymentDataShouldBeFound("expenseAccountNumber.equals=" + DEFAULT_EXPENSE_ACCOUNT_NUMBER);

        // Get all the prepaymentDataList where expenseAccountNumber equals to UPDATED_EXPENSE_ACCOUNT_NUMBER
        defaultPrepaymentDataShouldNotBeFound("expenseAccountNumber.equals=" + UPDATED_EXPENSE_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByExpenseAccountNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where expenseAccountNumber not equals to DEFAULT_EXPENSE_ACCOUNT_NUMBER
        defaultPrepaymentDataShouldNotBeFound("expenseAccountNumber.notEquals=" + DEFAULT_EXPENSE_ACCOUNT_NUMBER);

        // Get all the prepaymentDataList where expenseAccountNumber not equals to UPDATED_EXPENSE_ACCOUNT_NUMBER
        defaultPrepaymentDataShouldBeFound("expenseAccountNumber.notEquals=" + UPDATED_EXPENSE_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByExpenseAccountNumberIsInShouldWork() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where expenseAccountNumber in DEFAULT_EXPENSE_ACCOUNT_NUMBER or UPDATED_EXPENSE_ACCOUNT_NUMBER
        defaultPrepaymentDataShouldBeFound("expenseAccountNumber.in=" + DEFAULT_EXPENSE_ACCOUNT_NUMBER + "," + UPDATED_EXPENSE_ACCOUNT_NUMBER);

        // Get all the prepaymentDataList where expenseAccountNumber equals to UPDATED_EXPENSE_ACCOUNT_NUMBER
        defaultPrepaymentDataShouldNotBeFound("expenseAccountNumber.in=" + UPDATED_EXPENSE_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByExpenseAccountNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where expenseAccountNumber is not null
        defaultPrepaymentDataShouldBeFound("expenseAccountNumber.specified=true");

        // Get all the prepaymentDataList where expenseAccountNumber is null
        defaultPrepaymentDataShouldNotBeFound("expenseAccountNumber.specified=false");
    }
                @Test
    @Transactional
    public void getAllPrepaymentDataByExpenseAccountNumberContainsSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where expenseAccountNumber contains DEFAULT_EXPENSE_ACCOUNT_NUMBER
        defaultPrepaymentDataShouldBeFound("expenseAccountNumber.contains=" + DEFAULT_EXPENSE_ACCOUNT_NUMBER);

        // Get all the prepaymentDataList where expenseAccountNumber contains UPDATED_EXPENSE_ACCOUNT_NUMBER
        defaultPrepaymentDataShouldNotBeFound("expenseAccountNumber.contains=" + UPDATED_EXPENSE_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByExpenseAccountNumberNotContainsSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where expenseAccountNumber does not contain DEFAULT_EXPENSE_ACCOUNT_NUMBER
        defaultPrepaymentDataShouldNotBeFound("expenseAccountNumber.doesNotContain=" + DEFAULT_EXPENSE_ACCOUNT_NUMBER);

        // Get all the prepaymentDataList where expenseAccountNumber does not contain UPDATED_EXPENSE_ACCOUNT_NUMBER
        defaultPrepaymentDataShouldBeFound("expenseAccountNumber.doesNotContain=" + UPDATED_EXPENSE_ACCOUNT_NUMBER);
    }


    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentNumber equals to DEFAULT_PREPAYMENT_NUMBER
        defaultPrepaymentDataShouldBeFound("prepaymentNumber.equals=" + DEFAULT_PREPAYMENT_NUMBER);

        // Get all the prepaymentDataList where prepaymentNumber equals to UPDATED_PREPAYMENT_NUMBER
        defaultPrepaymentDataShouldNotBeFound("prepaymentNumber.equals=" + UPDATED_PREPAYMENT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentNumber not equals to DEFAULT_PREPAYMENT_NUMBER
        defaultPrepaymentDataShouldNotBeFound("prepaymentNumber.notEquals=" + DEFAULT_PREPAYMENT_NUMBER);

        // Get all the prepaymentDataList where prepaymentNumber not equals to UPDATED_PREPAYMENT_NUMBER
        defaultPrepaymentDataShouldBeFound("prepaymentNumber.notEquals=" + UPDATED_PREPAYMENT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentNumberIsInShouldWork() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentNumber in DEFAULT_PREPAYMENT_NUMBER or UPDATED_PREPAYMENT_NUMBER
        defaultPrepaymentDataShouldBeFound("prepaymentNumber.in=" + DEFAULT_PREPAYMENT_NUMBER + "," + UPDATED_PREPAYMENT_NUMBER);

        // Get all the prepaymentDataList where prepaymentNumber equals to UPDATED_PREPAYMENT_NUMBER
        defaultPrepaymentDataShouldNotBeFound("prepaymentNumber.in=" + UPDATED_PREPAYMENT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentNumber is not null
        defaultPrepaymentDataShouldBeFound("prepaymentNumber.specified=true");

        // Get all the prepaymentDataList where prepaymentNumber is null
        defaultPrepaymentDataShouldNotBeFound("prepaymentNumber.specified=false");
    }
                @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentNumberContainsSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentNumber contains DEFAULT_PREPAYMENT_NUMBER
        defaultPrepaymentDataShouldBeFound("prepaymentNumber.contains=" + DEFAULT_PREPAYMENT_NUMBER);

        // Get all the prepaymentDataList where prepaymentNumber contains UPDATED_PREPAYMENT_NUMBER
        defaultPrepaymentDataShouldNotBeFound("prepaymentNumber.contains=" + UPDATED_PREPAYMENT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentNumberNotContainsSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentNumber does not contain DEFAULT_PREPAYMENT_NUMBER
        defaultPrepaymentDataShouldNotBeFound("prepaymentNumber.doesNotContain=" + DEFAULT_PREPAYMENT_NUMBER);

        // Get all the prepaymentDataList where prepaymentNumber does not contain UPDATED_PREPAYMENT_NUMBER
        defaultPrepaymentDataShouldBeFound("prepaymentNumber.doesNotContain=" + UPDATED_PREPAYMENT_NUMBER);
    }


    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentDateIsEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentDate equals to DEFAULT_PREPAYMENT_DATE
        defaultPrepaymentDataShouldBeFound("prepaymentDate.equals=" + DEFAULT_PREPAYMENT_DATE);

        // Get all the prepaymentDataList where prepaymentDate equals to UPDATED_PREPAYMENT_DATE
        defaultPrepaymentDataShouldNotBeFound("prepaymentDate.equals=" + UPDATED_PREPAYMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentDate not equals to DEFAULT_PREPAYMENT_DATE
        defaultPrepaymentDataShouldNotBeFound("prepaymentDate.notEquals=" + DEFAULT_PREPAYMENT_DATE);

        // Get all the prepaymentDataList where prepaymentDate not equals to UPDATED_PREPAYMENT_DATE
        defaultPrepaymentDataShouldBeFound("prepaymentDate.notEquals=" + UPDATED_PREPAYMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentDateIsInShouldWork() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentDate in DEFAULT_PREPAYMENT_DATE or UPDATED_PREPAYMENT_DATE
        defaultPrepaymentDataShouldBeFound("prepaymentDate.in=" + DEFAULT_PREPAYMENT_DATE + "," + UPDATED_PREPAYMENT_DATE);

        // Get all the prepaymentDataList where prepaymentDate equals to UPDATED_PREPAYMENT_DATE
        defaultPrepaymentDataShouldNotBeFound("prepaymentDate.in=" + UPDATED_PREPAYMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentDate is not null
        defaultPrepaymentDataShouldBeFound("prepaymentDate.specified=true");

        // Get all the prepaymentDataList where prepaymentDate is null
        defaultPrepaymentDataShouldNotBeFound("prepaymentDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentDate is greater than or equal to DEFAULT_PREPAYMENT_DATE
        defaultPrepaymentDataShouldBeFound("prepaymentDate.greaterThanOrEqual=" + DEFAULT_PREPAYMENT_DATE);

        // Get all the prepaymentDataList where prepaymentDate is greater than or equal to UPDATED_PREPAYMENT_DATE
        defaultPrepaymentDataShouldNotBeFound("prepaymentDate.greaterThanOrEqual=" + UPDATED_PREPAYMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentDate is less than or equal to DEFAULT_PREPAYMENT_DATE
        defaultPrepaymentDataShouldBeFound("prepaymentDate.lessThanOrEqual=" + DEFAULT_PREPAYMENT_DATE);

        // Get all the prepaymentDataList where prepaymentDate is less than or equal to SMALLER_PREPAYMENT_DATE
        defaultPrepaymentDataShouldNotBeFound("prepaymentDate.lessThanOrEqual=" + SMALLER_PREPAYMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentDateIsLessThanSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentDate is less than DEFAULT_PREPAYMENT_DATE
        defaultPrepaymentDataShouldNotBeFound("prepaymentDate.lessThan=" + DEFAULT_PREPAYMENT_DATE);

        // Get all the prepaymentDataList where prepaymentDate is less than UPDATED_PREPAYMENT_DATE
        defaultPrepaymentDataShouldBeFound("prepaymentDate.lessThan=" + UPDATED_PREPAYMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentDate is greater than DEFAULT_PREPAYMENT_DATE
        defaultPrepaymentDataShouldNotBeFound("prepaymentDate.greaterThan=" + DEFAULT_PREPAYMENT_DATE);

        // Get all the prepaymentDataList where prepaymentDate is greater than SMALLER_PREPAYMENT_DATE
        defaultPrepaymentDataShouldBeFound("prepaymentDate.greaterThan=" + SMALLER_PREPAYMENT_DATE);
    }


    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentAmount equals to DEFAULT_PREPAYMENT_AMOUNT
        defaultPrepaymentDataShouldBeFound("prepaymentAmount.equals=" + DEFAULT_PREPAYMENT_AMOUNT);

        // Get all the prepaymentDataList where prepaymentAmount equals to UPDATED_PREPAYMENT_AMOUNT
        defaultPrepaymentDataShouldNotBeFound("prepaymentAmount.equals=" + UPDATED_PREPAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentAmountIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentAmount not equals to DEFAULT_PREPAYMENT_AMOUNT
        defaultPrepaymentDataShouldNotBeFound("prepaymentAmount.notEquals=" + DEFAULT_PREPAYMENT_AMOUNT);

        // Get all the prepaymentDataList where prepaymentAmount not equals to UPDATED_PREPAYMENT_AMOUNT
        defaultPrepaymentDataShouldBeFound("prepaymentAmount.notEquals=" + UPDATED_PREPAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentAmountIsInShouldWork() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentAmount in DEFAULT_PREPAYMENT_AMOUNT or UPDATED_PREPAYMENT_AMOUNT
        defaultPrepaymentDataShouldBeFound("prepaymentAmount.in=" + DEFAULT_PREPAYMENT_AMOUNT + "," + UPDATED_PREPAYMENT_AMOUNT);

        // Get all the prepaymentDataList where prepaymentAmount equals to UPDATED_PREPAYMENT_AMOUNT
        defaultPrepaymentDataShouldNotBeFound("prepaymentAmount.in=" + UPDATED_PREPAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentAmount is not null
        defaultPrepaymentDataShouldBeFound("prepaymentAmount.specified=true");

        // Get all the prepaymentDataList where prepaymentAmount is null
        defaultPrepaymentDataShouldNotBeFound("prepaymentAmount.specified=false");
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentAmount is greater than or equal to DEFAULT_PREPAYMENT_AMOUNT
        defaultPrepaymentDataShouldBeFound("prepaymentAmount.greaterThanOrEqual=" + DEFAULT_PREPAYMENT_AMOUNT);

        // Get all the prepaymentDataList where prepaymentAmount is greater than or equal to UPDATED_PREPAYMENT_AMOUNT
        defaultPrepaymentDataShouldNotBeFound("prepaymentAmount.greaterThanOrEqual=" + UPDATED_PREPAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentAmount is less than or equal to DEFAULT_PREPAYMENT_AMOUNT
        defaultPrepaymentDataShouldBeFound("prepaymentAmount.lessThanOrEqual=" + DEFAULT_PREPAYMENT_AMOUNT);

        // Get all the prepaymentDataList where prepaymentAmount is less than or equal to SMALLER_PREPAYMENT_AMOUNT
        defaultPrepaymentDataShouldNotBeFound("prepaymentAmount.lessThanOrEqual=" + SMALLER_PREPAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentAmount is less than DEFAULT_PREPAYMENT_AMOUNT
        defaultPrepaymentDataShouldNotBeFound("prepaymentAmount.lessThan=" + DEFAULT_PREPAYMENT_AMOUNT);

        // Get all the prepaymentDataList where prepaymentAmount is less than UPDATED_PREPAYMENT_AMOUNT
        defaultPrepaymentDataShouldBeFound("prepaymentAmount.lessThan=" + UPDATED_PREPAYMENT_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentAmount is greater than DEFAULT_PREPAYMENT_AMOUNT
        defaultPrepaymentDataShouldNotBeFound("prepaymentAmount.greaterThan=" + DEFAULT_PREPAYMENT_AMOUNT);

        // Get all the prepaymentDataList where prepaymentAmount is greater than SMALLER_PREPAYMENT_AMOUNT
        defaultPrepaymentDataShouldBeFound("prepaymentAmount.greaterThan=" + SMALLER_PREPAYMENT_AMOUNT);
    }


    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentPeriodsIsEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentPeriods equals to DEFAULT_PREPAYMENT_PERIODS
        defaultPrepaymentDataShouldBeFound("prepaymentPeriods.equals=" + DEFAULT_PREPAYMENT_PERIODS);

        // Get all the prepaymentDataList where prepaymentPeriods equals to UPDATED_PREPAYMENT_PERIODS
        defaultPrepaymentDataShouldNotBeFound("prepaymentPeriods.equals=" + UPDATED_PREPAYMENT_PERIODS);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentPeriodsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentPeriods not equals to DEFAULT_PREPAYMENT_PERIODS
        defaultPrepaymentDataShouldNotBeFound("prepaymentPeriods.notEquals=" + DEFAULT_PREPAYMENT_PERIODS);

        // Get all the prepaymentDataList where prepaymentPeriods not equals to UPDATED_PREPAYMENT_PERIODS
        defaultPrepaymentDataShouldBeFound("prepaymentPeriods.notEquals=" + UPDATED_PREPAYMENT_PERIODS);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentPeriodsIsInShouldWork() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentPeriods in DEFAULT_PREPAYMENT_PERIODS or UPDATED_PREPAYMENT_PERIODS
        defaultPrepaymentDataShouldBeFound("prepaymentPeriods.in=" + DEFAULT_PREPAYMENT_PERIODS + "," + UPDATED_PREPAYMENT_PERIODS);

        // Get all the prepaymentDataList where prepaymentPeriods equals to UPDATED_PREPAYMENT_PERIODS
        defaultPrepaymentDataShouldNotBeFound("prepaymentPeriods.in=" + UPDATED_PREPAYMENT_PERIODS);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentPeriodsIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentPeriods is not null
        defaultPrepaymentDataShouldBeFound("prepaymentPeriods.specified=true");

        // Get all the prepaymentDataList where prepaymentPeriods is null
        defaultPrepaymentDataShouldNotBeFound("prepaymentPeriods.specified=false");
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentPeriodsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentPeriods is greater than or equal to DEFAULT_PREPAYMENT_PERIODS
        defaultPrepaymentDataShouldBeFound("prepaymentPeriods.greaterThanOrEqual=" + DEFAULT_PREPAYMENT_PERIODS);

        // Get all the prepaymentDataList where prepaymentPeriods is greater than or equal to UPDATED_PREPAYMENT_PERIODS
        defaultPrepaymentDataShouldNotBeFound("prepaymentPeriods.greaterThanOrEqual=" + UPDATED_PREPAYMENT_PERIODS);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentPeriodsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentPeriods is less than or equal to DEFAULT_PREPAYMENT_PERIODS
        defaultPrepaymentDataShouldBeFound("prepaymentPeriods.lessThanOrEqual=" + DEFAULT_PREPAYMENT_PERIODS);

        // Get all the prepaymentDataList where prepaymentPeriods is less than or equal to SMALLER_PREPAYMENT_PERIODS
        defaultPrepaymentDataShouldNotBeFound("prepaymentPeriods.lessThanOrEqual=" + SMALLER_PREPAYMENT_PERIODS);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentPeriodsIsLessThanSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentPeriods is less than DEFAULT_PREPAYMENT_PERIODS
        defaultPrepaymentDataShouldNotBeFound("prepaymentPeriods.lessThan=" + DEFAULT_PREPAYMENT_PERIODS);

        // Get all the prepaymentDataList where prepaymentPeriods is less than UPDATED_PREPAYMENT_PERIODS
        defaultPrepaymentDataShouldBeFound("prepaymentPeriods.lessThan=" + UPDATED_PREPAYMENT_PERIODS);
    }

    @Test
    @Transactional
    public void getAllPrepaymentDataByPrepaymentPeriodsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        // Get all the prepaymentDataList where prepaymentPeriods is greater than DEFAULT_PREPAYMENT_PERIODS
        defaultPrepaymentDataShouldNotBeFound("prepaymentPeriods.greaterThan=" + DEFAULT_PREPAYMENT_PERIODS);

        // Get all the prepaymentDataList where prepaymentPeriods is greater than SMALLER_PREPAYMENT_PERIODS
        defaultPrepaymentDataShouldBeFound("prepaymentPeriods.greaterThan=" + SMALLER_PREPAYMENT_PERIODS);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPrepaymentDataShouldBeFound(String filter) throws Exception {
        restPrepaymentDataMockMvc.perform(get("/api/prepayment-data?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prepaymentData.getId().intValue())))
            .andExpect(jsonPath("$.[*].accountName").value(hasItem(DEFAULT_ACCOUNT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].accountNumber").value(hasItem(DEFAULT_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].expenseAccountNumber").value(hasItem(DEFAULT_EXPENSE_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].prepaymentNumber").value(hasItem(DEFAULT_PREPAYMENT_NUMBER)))
            .andExpect(jsonPath("$.[*].prepaymentDate").value(hasItem(DEFAULT_PREPAYMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].prepaymentAmount").value(hasItem(DEFAULT_PREPAYMENT_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].prepaymentPeriods").value(hasItem(DEFAULT_PREPAYMENT_PERIODS)));

        // Check, that the count call also returns 1
        restPrepaymentDataMockMvc.perform(get("/api/prepayment-data/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPrepaymentDataShouldNotBeFound(String filter) throws Exception {
        restPrepaymentDataMockMvc.perform(get("/api/prepayment-data?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPrepaymentDataMockMvc.perform(get("/api/prepayment-data/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingPrepaymentData() throws Exception {
        // Get the prepaymentData
        restPrepaymentDataMockMvc.perform(get("/api/prepayment-data/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePrepaymentData() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        int databaseSizeBeforeUpdate = prepaymentDataRepository.findAll().size();

        // Update the prepaymentData
        PrepaymentData updatedPrepaymentData = prepaymentDataRepository.findById(prepaymentData.getId()).get();
        // Disconnect from session so that the updates on updatedPrepaymentData are not directly saved in db
        em.detach(updatedPrepaymentData);
        updatedPrepaymentData
            .accountName(UPDATED_ACCOUNT_NAME)
            .description(UPDATED_DESCRIPTION)
            .accountNumber(UPDATED_ACCOUNT_NUMBER)
            .expenseAccountNumber(UPDATED_EXPENSE_ACCOUNT_NUMBER)
            .prepaymentNumber(UPDATED_PREPAYMENT_NUMBER)
            .prepaymentDate(UPDATED_PREPAYMENT_DATE)
            .prepaymentAmount(UPDATED_PREPAYMENT_AMOUNT)
            .prepaymentPeriods(UPDATED_PREPAYMENT_PERIODS);
        PrepaymentDataDTO prepaymentDataDTO = prepaymentDataMapper.toDto(updatedPrepaymentData);

        restPrepaymentDataMockMvc.perform(put("/api/prepayment-data")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepaymentDataDTO)))
            .andExpect(status().isOk());

        // Validate the PrepaymentData in the database
        List<PrepaymentData> prepaymentDataList = prepaymentDataRepository.findAll();
        assertThat(prepaymentDataList).hasSize(databaseSizeBeforeUpdate);
        PrepaymentData testPrepaymentData = prepaymentDataList.get(prepaymentDataList.size() - 1);
        assertThat(testPrepaymentData.getAccountName()).isEqualTo(UPDATED_ACCOUNT_NAME);
        assertThat(testPrepaymentData.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPrepaymentData.getAccountNumber()).isEqualTo(UPDATED_ACCOUNT_NUMBER);
        assertThat(testPrepaymentData.getExpenseAccountNumber()).isEqualTo(UPDATED_EXPENSE_ACCOUNT_NUMBER);
        assertThat(testPrepaymentData.getPrepaymentNumber()).isEqualTo(UPDATED_PREPAYMENT_NUMBER);
        assertThat(testPrepaymentData.getPrepaymentDate()).isEqualTo(UPDATED_PREPAYMENT_DATE);
        assertThat(testPrepaymentData.getPrepaymentAmount()).isEqualTo(UPDATED_PREPAYMENT_AMOUNT);
        assertThat(testPrepaymentData.getPrepaymentPeriods()).isEqualTo(UPDATED_PREPAYMENT_PERIODS);

        // Validate the PrepaymentData in Elasticsearch
        verify(mockPrepaymentDataSearchRepository, times(1)).save(testPrepaymentData);
    }

    @Test
    @Transactional
    public void updateNonExistingPrepaymentData() throws Exception {
        int databaseSizeBeforeUpdate = prepaymentDataRepository.findAll().size();

        // Create the PrepaymentData
        PrepaymentDataDTO prepaymentDataDTO = prepaymentDataMapper.toDto(prepaymentData);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPrepaymentDataMockMvc.perform(put("/api/prepayment-data")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepaymentDataDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PrepaymentData in the database
        List<PrepaymentData> prepaymentDataList = prepaymentDataRepository.findAll();
        assertThat(prepaymentDataList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PrepaymentData in Elasticsearch
        verify(mockPrepaymentDataSearchRepository, times(0)).save(prepaymentData);
    }

    @Test
    @Transactional
    public void deletePrepaymentData() throws Exception {
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);

        int databaseSizeBeforeDelete = prepaymentDataRepository.findAll().size();

        // Delete the prepaymentData
        restPrepaymentDataMockMvc.perform(delete("/api/prepayment-data/{id}", prepaymentData.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PrepaymentData> prepaymentDataList = prepaymentDataRepository.findAll();
        assertThat(prepaymentDataList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the PrepaymentData in Elasticsearch
        verify(mockPrepaymentDataSearchRepository, times(1)).deleteById(prepaymentData.getId());
    }

    @Test
    @Transactional
    public void searchPrepaymentData() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        prepaymentDataRepository.saveAndFlush(prepaymentData);
        when(mockPrepaymentDataSearchRepository.search(queryStringQuery("id:" + prepaymentData.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(prepaymentData), PageRequest.of(0, 1), 1));

        // Search the prepaymentData
        restPrepaymentDataMockMvc.perform(get("/api/_search/prepayment-data?query=id:" + prepaymentData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prepaymentData.getId().intValue())))
            .andExpect(jsonPath("$.[*].accountName").value(hasItem(DEFAULT_ACCOUNT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].accountNumber").value(hasItem(DEFAULT_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].expenseAccountNumber").value(hasItem(DEFAULT_EXPENSE_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].prepaymentNumber").value(hasItem(DEFAULT_PREPAYMENT_NUMBER)))
            .andExpect(jsonPath("$.[*].prepaymentDate").value(hasItem(DEFAULT_PREPAYMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].prepaymentAmount").value(hasItem(DEFAULT_PREPAYMENT_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].prepaymentPeriods").value(hasItem(DEFAULT_PREPAYMENT_PERIODS)));
    }
}
