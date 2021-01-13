package io.github.prepayments.web.rest;

import io.github.prepayments.PrepaymentsApp;
import io.github.prepayments.domain.AmortizationEntry;
import io.github.prepayments.repository.AmortizationEntryRepository;
import io.github.prepayments.repository.search.AmortizationEntrySearchRepository;
import io.github.prepayments.service.AmortizationEntryService;
import io.github.prepayments.service.dto.AmortizationEntryDTO;
import io.github.prepayments.service.mapper.AmortizationEntryMapper;
import io.github.prepayments.service.dto.AmortizationEntryCriteria;
import io.github.prepayments.service.AmortizationEntryQueryService;

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
 * Integration tests for the {@link AmortizationEntryResource} REST controller.
 */
@SpringBootTest(classes = PrepaymentsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class AmortizationEntryResourceIT {

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

    private static final BigDecimal DEFAULT_TRANSACTION_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TRANSACTION_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_TRANSACTION_AMOUNT = new BigDecimal(1 - 1);

    private static final LocalDate DEFAULT_AMORTIZATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_AMORTIZATION_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_AMORTIZATION_DATE = LocalDate.ofEpochDay(-1L);

    @Autowired
    private AmortizationEntryRepository amortizationEntryRepository;

    @Autowired
    private AmortizationEntryMapper amortizationEntryMapper;

    @Autowired
    private AmortizationEntryService amortizationEntryService;

    /**
     * This repository is mocked in the io.github.prepayments.repository.search test package.
     *
     * @see io.github.prepayments.repository.search.AmortizationEntrySearchRepositoryMockConfiguration
     */
    @Autowired
    private AmortizationEntrySearchRepository mockAmortizationEntrySearchRepository;

    @Autowired
    private AmortizationEntryQueryService amortizationEntryQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAmortizationEntryMockMvc;

    private AmortizationEntry amortizationEntry;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AmortizationEntry createEntity(EntityManager em) {
        AmortizationEntry amortizationEntry = new AmortizationEntry()
            .accountName(DEFAULT_ACCOUNT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .accountNumber(DEFAULT_ACCOUNT_NUMBER)
            .expenseAccountNumber(DEFAULT_EXPENSE_ACCOUNT_NUMBER)
            .prepaymentNumber(DEFAULT_PREPAYMENT_NUMBER)
            .prepaymentDate(DEFAULT_PREPAYMENT_DATE)
            .transactionAmount(DEFAULT_TRANSACTION_AMOUNT)
            .amortizationDate(DEFAULT_AMORTIZATION_DATE);
        return amortizationEntry;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AmortizationEntry createUpdatedEntity(EntityManager em) {
        AmortizationEntry amortizationEntry = new AmortizationEntry()
            .accountName(UPDATED_ACCOUNT_NAME)
            .description(UPDATED_DESCRIPTION)
            .accountNumber(UPDATED_ACCOUNT_NUMBER)
            .expenseAccountNumber(UPDATED_EXPENSE_ACCOUNT_NUMBER)
            .prepaymentNumber(UPDATED_PREPAYMENT_NUMBER)
            .prepaymentDate(UPDATED_PREPAYMENT_DATE)
            .transactionAmount(UPDATED_TRANSACTION_AMOUNT)
            .amortizationDate(UPDATED_AMORTIZATION_DATE);
        return amortizationEntry;
    }

    @BeforeEach
    public void initTest() {
        amortizationEntry = createEntity(em);
    }

    @Test
    @Transactional
    public void createAmortizationEntry() throws Exception {
        int databaseSizeBeforeCreate = amortizationEntryRepository.findAll().size();
        // Create the AmortizationEntry
        AmortizationEntryDTO amortizationEntryDTO = amortizationEntryMapper.toDto(amortizationEntry);
        restAmortizationEntryMockMvc.perform(post("/api/amortization-entries")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(amortizationEntryDTO)))
            .andExpect(status().isCreated());

        // Validate the AmortizationEntry in the database
        List<AmortizationEntry> amortizationEntryList = amortizationEntryRepository.findAll();
        assertThat(amortizationEntryList).hasSize(databaseSizeBeforeCreate + 1);
        AmortizationEntry testAmortizationEntry = amortizationEntryList.get(amortizationEntryList.size() - 1);
        assertThat(testAmortizationEntry.getAccountName()).isEqualTo(DEFAULT_ACCOUNT_NAME);
        assertThat(testAmortizationEntry.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testAmortizationEntry.getAccountNumber()).isEqualTo(DEFAULT_ACCOUNT_NUMBER);
        assertThat(testAmortizationEntry.getExpenseAccountNumber()).isEqualTo(DEFAULT_EXPENSE_ACCOUNT_NUMBER);
        assertThat(testAmortizationEntry.getPrepaymentNumber()).isEqualTo(DEFAULT_PREPAYMENT_NUMBER);
        assertThat(testAmortizationEntry.getPrepaymentDate()).isEqualTo(DEFAULT_PREPAYMENT_DATE);
        assertThat(testAmortizationEntry.getTransactionAmount()).isEqualTo(DEFAULT_TRANSACTION_AMOUNT);
        assertThat(testAmortizationEntry.getAmortizationDate()).isEqualTo(DEFAULT_AMORTIZATION_DATE);

        // Validate the AmortizationEntry in Elasticsearch
        verify(mockAmortizationEntrySearchRepository, times(1)).save(testAmortizationEntry);
    }

    @Test
    @Transactional
    public void createAmortizationEntryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = amortizationEntryRepository.findAll().size();

        // Create the AmortizationEntry with an existing ID
        amortizationEntry.setId(1L);
        AmortizationEntryDTO amortizationEntryDTO = amortizationEntryMapper.toDto(amortizationEntry);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAmortizationEntryMockMvc.perform(post("/api/amortization-entries")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(amortizationEntryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AmortizationEntry in the database
        List<AmortizationEntry> amortizationEntryList = amortizationEntryRepository.findAll();
        assertThat(amortizationEntryList).hasSize(databaseSizeBeforeCreate);

        // Validate the AmortizationEntry in Elasticsearch
        verify(mockAmortizationEntrySearchRepository, times(0)).save(amortizationEntry);
    }


    @Test
    @Transactional
    public void getAllAmortizationEntries() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList
        restAmortizationEntryMockMvc.perform(get("/api/amortization-entries?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(amortizationEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].accountName").value(hasItem(DEFAULT_ACCOUNT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].accountNumber").value(hasItem(DEFAULT_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].expenseAccountNumber").value(hasItem(DEFAULT_EXPENSE_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].prepaymentNumber").value(hasItem(DEFAULT_PREPAYMENT_NUMBER)))
            .andExpect(jsonPath("$.[*].prepaymentDate").value(hasItem(DEFAULT_PREPAYMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].transactionAmount").value(hasItem(DEFAULT_TRANSACTION_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].amortizationDate").value(hasItem(DEFAULT_AMORTIZATION_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getAmortizationEntry() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get the amortizationEntry
        restAmortizationEntryMockMvc.perform(get("/api/amortization-entries/{id}", amortizationEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(amortizationEntry.getId().intValue()))
            .andExpect(jsonPath("$.accountName").value(DEFAULT_ACCOUNT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.accountNumber").value(DEFAULT_ACCOUNT_NUMBER))
            .andExpect(jsonPath("$.expenseAccountNumber").value(DEFAULT_EXPENSE_ACCOUNT_NUMBER))
            .andExpect(jsonPath("$.prepaymentNumber").value(DEFAULT_PREPAYMENT_NUMBER))
            .andExpect(jsonPath("$.prepaymentDate").value(DEFAULT_PREPAYMENT_DATE.toString()))
            .andExpect(jsonPath("$.transactionAmount").value(DEFAULT_TRANSACTION_AMOUNT.intValue()))
            .andExpect(jsonPath("$.amortizationDate").value(DEFAULT_AMORTIZATION_DATE.toString()));
    }


    @Test
    @Transactional
    public void getAmortizationEntriesByIdFiltering() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        Long id = amortizationEntry.getId();

        defaultAmortizationEntryShouldBeFound("id.equals=" + id);
        defaultAmortizationEntryShouldNotBeFound("id.notEquals=" + id);

        defaultAmortizationEntryShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAmortizationEntryShouldNotBeFound("id.greaterThan=" + id);

        defaultAmortizationEntryShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAmortizationEntryShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllAmortizationEntriesByAccountNameIsEqualToSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where accountName equals to DEFAULT_ACCOUNT_NAME
        defaultAmortizationEntryShouldBeFound("accountName.equals=" + DEFAULT_ACCOUNT_NAME);

        // Get all the amortizationEntryList where accountName equals to UPDATED_ACCOUNT_NAME
        defaultAmortizationEntryShouldNotBeFound("accountName.equals=" + UPDATED_ACCOUNT_NAME);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByAccountNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where accountName not equals to DEFAULT_ACCOUNT_NAME
        defaultAmortizationEntryShouldNotBeFound("accountName.notEquals=" + DEFAULT_ACCOUNT_NAME);

        // Get all the amortizationEntryList where accountName not equals to UPDATED_ACCOUNT_NAME
        defaultAmortizationEntryShouldBeFound("accountName.notEquals=" + UPDATED_ACCOUNT_NAME);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByAccountNameIsInShouldWork() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where accountName in DEFAULT_ACCOUNT_NAME or UPDATED_ACCOUNT_NAME
        defaultAmortizationEntryShouldBeFound("accountName.in=" + DEFAULT_ACCOUNT_NAME + "," + UPDATED_ACCOUNT_NAME);

        // Get all the amortizationEntryList where accountName equals to UPDATED_ACCOUNT_NAME
        defaultAmortizationEntryShouldNotBeFound("accountName.in=" + UPDATED_ACCOUNT_NAME);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByAccountNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where accountName is not null
        defaultAmortizationEntryShouldBeFound("accountName.specified=true");

        // Get all the amortizationEntryList where accountName is null
        defaultAmortizationEntryShouldNotBeFound("accountName.specified=false");
    }
                @Test
    @Transactional
    public void getAllAmortizationEntriesByAccountNameContainsSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where accountName contains DEFAULT_ACCOUNT_NAME
        defaultAmortizationEntryShouldBeFound("accountName.contains=" + DEFAULT_ACCOUNT_NAME);

        // Get all the amortizationEntryList where accountName contains UPDATED_ACCOUNT_NAME
        defaultAmortizationEntryShouldNotBeFound("accountName.contains=" + UPDATED_ACCOUNT_NAME);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByAccountNameNotContainsSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where accountName does not contain DEFAULT_ACCOUNT_NAME
        defaultAmortizationEntryShouldNotBeFound("accountName.doesNotContain=" + DEFAULT_ACCOUNT_NAME);

        // Get all the amortizationEntryList where accountName does not contain UPDATED_ACCOUNT_NAME
        defaultAmortizationEntryShouldBeFound("accountName.doesNotContain=" + UPDATED_ACCOUNT_NAME);
    }


    @Test
    @Transactional
    public void getAllAmortizationEntriesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where description equals to DEFAULT_DESCRIPTION
        defaultAmortizationEntryShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the amortizationEntryList where description equals to UPDATED_DESCRIPTION
        defaultAmortizationEntryShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where description not equals to DEFAULT_DESCRIPTION
        defaultAmortizationEntryShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the amortizationEntryList where description not equals to UPDATED_DESCRIPTION
        defaultAmortizationEntryShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultAmortizationEntryShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the amortizationEntryList where description equals to UPDATED_DESCRIPTION
        defaultAmortizationEntryShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where description is not null
        defaultAmortizationEntryShouldBeFound("description.specified=true");

        // Get all the amortizationEntryList where description is null
        defaultAmortizationEntryShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllAmortizationEntriesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where description contains DEFAULT_DESCRIPTION
        defaultAmortizationEntryShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the amortizationEntryList where description contains UPDATED_DESCRIPTION
        defaultAmortizationEntryShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where description does not contain DEFAULT_DESCRIPTION
        defaultAmortizationEntryShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the amortizationEntryList where description does not contain UPDATED_DESCRIPTION
        defaultAmortizationEntryShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllAmortizationEntriesByAccountNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where accountNumber equals to DEFAULT_ACCOUNT_NUMBER
        defaultAmortizationEntryShouldBeFound("accountNumber.equals=" + DEFAULT_ACCOUNT_NUMBER);

        // Get all the amortizationEntryList where accountNumber equals to UPDATED_ACCOUNT_NUMBER
        defaultAmortizationEntryShouldNotBeFound("accountNumber.equals=" + UPDATED_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByAccountNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where accountNumber not equals to DEFAULT_ACCOUNT_NUMBER
        defaultAmortizationEntryShouldNotBeFound("accountNumber.notEquals=" + DEFAULT_ACCOUNT_NUMBER);

        // Get all the amortizationEntryList where accountNumber not equals to UPDATED_ACCOUNT_NUMBER
        defaultAmortizationEntryShouldBeFound("accountNumber.notEquals=" + UPDATED_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByAccountNumberIsInShouldWork() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where accountNumber in DEFAULT_ACCOUNT_NUMBER or UPDATED_ACCOUNT_NUMBER
        defaultAmortizationEntryShouldBeFound("accountNumber.in=" + DEFAULT_ACCOUNT_NUMBER + "," + UPDATED_ACCOUNT_NUMBER);

        // Get all the amortizationEntryList where accountNumber equals to UPDATED_ACCOUNT_NUMBER
        defaultAmortizationEntryShouldNotBeFound("accountNumber.in=" + UPDATED_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByAccountNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where accountNumber is not null
        defaultAmortizationEntryShouldBeFound("accountNumber.specified=true");

        // Get all the amortizationEntryList where accountNumber is null
        defaultAmortizationEntryShouldNotBeFound("accountNumber.specified=false");
    }
                @Test
    @Transactional
    public void getAllAmortizationEntriesByAccountNumberContainsSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where accountNumber contains DEFAULT_ACCOUNT_NUMBER
        defaultAmortizationEntryShouldBeFound("accountNumber.contains=" + DEFAULT_ACCOUNT_NUMBER);

        // Get all the amortizationEntryList where accountNumber contains UPDATED_ACCOUNT_NUMBER
        defaultAmortizationEntryShouldNotBeFound("accountNumber.contains=" + UPDATED_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByAccountNumberNotContainsSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where accountNumber does not contain DEFAULT_ACCOUNT_NUMBER
        defaultAmortizationEntryShouldNotBeFound("accountNumber.doesNotContain=" + DEFAULT_ACCOUNT_NUMBER);

        // Get all the amortizationEntryList where accountNumber does not contain UPDATED_ACCOUNT_NUMBER
        defaultAmortizationEntryShouldBeFound("accountNumber.doesNotContain=" + UPDATED_ACCOUNT_NUMBER);
    }


    @Test
    @Transactional
    public void getAllAmortizationEntriesByExpenseAccountNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where expenseAccountNumber equals to DEFAULT_EXPENSE_ACCOUNT_NUMBER
        defaultAmortizationEntryShouldBeFound("expenseAccountNumber.equals=" + DEFAULT_EXPENSE_ACCOUNT_NUMBER);

        // Get all the amortizationEntryList where expenseAccountNumber equals to UPDATED_EXPENSE_ACCOUNT_NUMBER
        defaultAmortizationEntryShouldNotBeFound("expenseAccountNumber.equals=" + UPDATED_EXPENSE_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByExpenseAccountNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where expenseAccountNumber not equals to DEFAULT_EXPENSE_ACCOUNT_NUMBER
        defaultAmortizationEntryShouldNotBeFound("expenseAccountNumber.notEquals=" + DEFAULT_EXPENSE_ACCOUNT_NUMBER);

        // Get all the amortizationEntryList where expenseAccountNumber not equals to UPDATED_EXPENSE_ACCOUNT_NUMBER
        defaultAmortizationEntryShouldBeFound("expenseAccountNumber.notEquals=" + UPDATED_EXPENSE_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByExpenseAccountNumberIsInShouldWork() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where expenseAccountNumber in DEFAULT_EXPENSE_ACCOUNT_NUMBER or UPDATED_EXPENSE_ACCOUNT_NUMBER
        defaultAmortizationEntryShouldBeFound("expenseAccountNumber.in=" + DEFAULT_EXPENSE_ACCOUNT_NUMBER + "," + UPDATED_EXPENSE_ACCOUNT_NUMBER);

        // Get all the amortizationEntryList where expenseAccountNumber equals to UPDATED_EXPENSE_ACCOUNT_NUMBER
        defaultAmortizationEntryShouldNotBeFound("expenseAccountNumber.in=" + UPDATED_EXPENSE_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByExpenseAccountNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where expenseAccountNumber is not null
        defaultAmortizationEntryShouldBeFound("expenseAccountNumber.specified=true");

        // Get all the amortizationEntryList where expenseAccountNumber is null
        defaultAmortizationEntryShouldNotBeFound("expenseAccountNumber.specified=false");
    }
                @Test
    @Transactional
    public void getAllAmortizationEntriesByExpenseAccountNumberContainsSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where expenseAccountNumber contains DEFAULT_EXPENSE_ACCOUNT_NUMBER
        defaultAmortizationEntryShouldBeFound("expenseAccountNumber.contains=" + DEFAULT_EXPENSE_ACCOUNT_NUMBER);

        // Get all the amortizationEntryList where expenseAccountNumber contains UPDATED_EXPENSE_ACCOUNT_NUMBER
        defaultAmortizationEntryShouldNotBeFound("expenseAccountNumber.contains=" + UPDATED_EXPENSE_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByExpenseAccountNumberNotContainsSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where expenseAccountNumber does not contain DEFAULT_EXPENSE_ACCOUNT_NUMBER
        defaultAmortizationEntryShouldNotBeFound("expenseAccountNumber.doesNotContain=" + DEFAULT_EXPENSE_ACCOUNT_NUMBER);

        // Get all the amortizationEntryList where expenseAccountNumber does not contain UPDATED_EXPENSE_ACCOUNT_NUMBER
        defaultAmortizationEntryShouldBeFound("expenseAccountNumber.doesNotContain=" + UPDATED_EXPENSE_ACCOUNT_NUMBER);
    }


    @Test
    @Transactional
    public void getAllAmortizationEntriesByPrepaymentNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where prepaymentNumber equals to DEFAULT_PREPAYMENT_NUMBER
        defaultAmortizationEntryShouldBeFound("prepaymentNumber.equals=" + DEFAULT_PREPAYMENT_NUMBER);

        // Get all the amortizationEntryList where prepaymentNumber equals to UPDATED_PREPAYMENT_NUMBER
        defaultAmortizationEntryShouldNotBeFound("prepaymentNumber.equals=" + UPDATED_PREPAYMENT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByPrepaymentNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where prepaymentNumber not equals to DEFAULT_PREPAYMENT_NUMBER
        defaultAmortizationEntryShouldNotBeFound("prepaymentNumber.notEquals=" + DEFAULT_PREPAYMENT_NUMBER);

        // Get all the amortizationEntryList where prepaymentNumber not equals to UPDATED_PREPAYMENT_NUMBER
        defaultAmortizationEntryShouldBeFound("prepaymentNumber.notEquals=" + UPDATED_PREPAYMENT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByPrepaymentNumberIsInShouldWork() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where prepaymentNumber in DEFAULT_PREPAYMENT_NUMBER or UPDATED_PREPAYMENT_NUMBER
        defaultAmortizationEntryShouldBeFound("prepaymentNumber.in=" + DEFAULT_PREPAYMENT_NUMBER + "," + UPDATED_PREPAYMENT_NUMBER);

        // Get all the amortizationEntryList where prepaymentNumber equals to UPDATED_PREPAYMENT_NUMBER
        defaultAmortizationEntryShouldNotBeFound("prepaymentNumber.in=" + UPDATED_PREPAYMENT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByPrepaymentNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where prepaymentNumber is not null
        defaultAmortizationEntryShouldBeFound("prepaymentNumber.specified=true");

        // Get all the amortizationEntryList where prepaymentNumber is null
        defaultAmortizationEntryShouldNotBeFound("prepaymentNumber.specified=false");
    }
                @Test
    @Transactional
    public void getAllAmortizationEntriesByPrepaymentNumberContainsSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where prepaymentNumber contains DEFAULT_PREPAYMENT_NUMBER
        defaultAmortizationEntryShouldBeFound("prepaymentNumber.contains=" + DEFAULT_PREPAYMENT_NUMBER);

        // Get all the amortizationEntryList where prepaymentNumber contains UPDATED_PREPAYMENT_NUMBER
        defaultAmortizationEntryShouldNotBeFound("prepaymentNumber.contains=" + UPDATED_PREPAYMENT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByPrepaymentNumberNotContainsSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where prepaymentNumber does not contain DEFAULT_PREPAYMENT_NUMBER
        defaultAmortizationEntryShouldNotBeFound("prepaymentNumber.doesNotContain=" + DEFAULT_PREPAYMENT_NUMBER);

        // Get all the amortizationEntryList where prepaymentNumber does not contain UPDATED_PREPAYMENT_NUMBER
        defaultAmortizationEntryShouldBeFound("prepaymentNumber.doesNotContain=" + UPDATED_PREPAYMENT_NUMBER);
    }


    @Test
    @Transactional
    public void getAllAmortizationEntriesByPrepaymentDateIsEqualToSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where prepaymentDate equals to DEFAULT_PREPAYMENT_DATE
        defaultAmortizationEntryShouldBeFound("prepaymentDate.equals=" + DEFAULT_PREPAYMENT_DATE);

        // Get all the amortizationEntryList where prepaymentDate equals to UPDATED_PREPAYMENT_DATE
        defaultAmortizationEntryShouldNotBeFound("prepaymentDate.equals=" + UPDATED_PREPAYMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByPrepaymentDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where prepaymentDate not equals to DEFAULT_PREPAYMENT_DATE
        defaultAmortizationEntryShouldNotBeFound("prepaymentDate.notEquals=" + DEFAULT_PREPAYMENT_DATE);

        // Get all the amortizationEntryList where prepaymentDate not equals to UPDATED_PREPAYMENT_DATE
        defaultAmortizationEntryShouldBeFound("prepaymentDate.notEquals=" + UPDATED_PREPAYMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByPrepaymentDateIsInShouldWork() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where prepaymentDate in DEFAULT_PREPAYMENT_DATE or UPDATED_PREPAYMENT_DATE
        defaultAmortizationEntryShouldBeFound("prepaymentDate.in=" + DEFAULT_PREPAYMENT_DATE + "," + UPDATED_PREPAYMENT_DATE);

        // Get all the amortizationEntryList where prepaymentDate equals to UPDATED_PREPAYMENT_DATE
        defaultAmortizationEntryShouldNotBeFound("prepaymentDate.in=" + UPDATED_PREPAYMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByPrepaymentDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where prepaymentDate is not null
        defaultAmortizationEntryShouldBeFound("prepaymentDate.specified=true");

        // Get all the amortizationEntryList where prepaymentDate is null
        defaultAmortizationEntryShouldNotBeFound("prepaymentDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByPrepaymentDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where prepaymentDate is greater than or equal to DEFAULT_PREPAYMENT_DATE
        defaultAmortizationEntryShouldBeFound("prepaymentDate.greaterThanOrEqual=" + DEFAULT_PREPAYMENT_DATE);

        // Get all the amortizationEntryList where prepaymentDate is greater than or equal to UPDATED_PREPAYMENT_DATE
        defaultAmortizationEntryShouldNotBeFound("prepaymentDate.greaterThanOrEqual=" + UPDATED_PREPAYMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByPrepaymentDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where prepaymentDate is less than or equal to DEFAULT_PREPAYMENT_DATE
        defaultAmortizationEntryShouldBeFound("prepaymentDate.lessThanOrEqual=" + DEFAULT_PREPAYMENT_DATE);

        // Get all the amortizationEntryList where prepaymentDate is less than or equal to SMALLER_PREPAYMENT_DATE
        defaultAmortizationEntryShouldNotBeFound("prepaymentDate.lessThanOrEqual=" + SMALLER_PREPAYMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByPrepaymentDateIsLessThanSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where prepaymentDate is less than DEFAULT_PREPAYMENT_DATE
        defaultAmortizationEntryShouldNotBeFound("prepaymentDate.lessThan=" + DEFAULT_PREPAYMENT_DATE);

        // Get all the amortizationEntryList where prepaymentDate is less than UPDATED_PREPAYMENT_DATE
        defaultAmortizationEntryShouldBeFound("prepaymentDate.lessThan=" + UPDATED_PREPAYMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByPrepaymentDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where prepaymentDate is greater than DEFAULT_PREPAYMENT_DATE
        defaultAmortizationEntryShouldNotBeFound("prepaymentDate.greaterThan=" + DEFAULT_PREPAYMENT_DATE);

        // Get all the amortizationEntryList where prepaymentDate is greater than SMALLER_PREPAYMENT_DATE
        defaultAmortizationEntryShouldBeFound("prepaymentDate.greaterThan=" + SMALLER_PREPAYMENT_DATE);
    }


    @Test
    @Transactional
    public void getAllAmortizationEntriesByTransactionAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where transactionAmount equals to DEFAULT_TRANSACTION_AMOUNT
        defaultAmortizationEntryShouldBeFound("transactionAmount.equals=" + DEFAULT_TRANSACTION_AMOUNT);

        // Get all the amortizationEntryList where transactionAmount equals to UPDATED_TRANSACTION_AMOUNT
        defaultAmortizationEntryShouldNotBeFound("transactionAmount.equals=" + UPDATED_TRANSACTION_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByTransactionAmountIsNotEqualToSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where transactionAmount not equals to DEFAULT_TRANSACTION_AMOUNT
        defaultAmortizationEntryShouldNotBeFound("transactionAmount.notEquals=" + DEFAULT_TRANSACTION_AMOUNT);

        // Get all the amortizationEntryList where transactionAmount not equals to UPDATED_TRANSACTION_AMOUNT
        defaultAmortizationEntryShouldBeFound("transactionAmount.notEquals=" + UPDATED_TRANSACTION_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByTransactionAmountIsInShouldWork() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where transactionAmount in DEFAULT_TRANSACTION_AMOUNT or UPDATED_TRANSACTION_AMOUNT
        defaultAmortizationEntryShouldBeFound("transactionAmount.in=" + DEFAULT_TRANSACTION_AMOUNT + "," + UPDATED_TRANSACTION_AMOUNT);

        // Get all the amortizationEntryList where transactionAmount equals to UPDATED_TRANSACTION_AMOUNT
        defaultAmortizationEntryShouldNotBeFound("transactionAmount.in=" + UPDATED_TRANSACTION_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByTransactionAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where transactionAmount is not null
        defaultAmortizationEntryShouldBeFound("transactionAmount.specified=true");

        // Get all the amortizationEntryList where transactionAmount is null
        defaultAmortizationEntryShouldNotBeFound("transactionAmount.specified=false");
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByTransactionAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where transactionAmount is greater than or equal to DEFAULT_TRANSACTION_AMOUNT
        defaultAmortizationEntryShouldBeFound("transactionAmount.greaterThanOrEqual=" + DEFAULT_TRANSACTION_AMOUNT);

        // Get all the amortizationEntryList where transactionAmount is greater than or equal to UPDATED_TRANSACTION_AMOUNT
        defaultAmortizationEntryShouldNotBeFound("transactionAmount.greaterThanOrEqual=" + UPDATED_TRANSACTION_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByTransactionAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where transactionAmount is less than or equal to DEFAULT_TRANSACTION_AMOUNT
        defaultAmortizationEntryShouldBeFound("transactionAmount.lessThanOrEqual=" + DEFAULT_TRANSACTION_AMOUNT);

        // Get all the amortizationEntryList where transactionAmount is less than or equal to SMALLER_TRANSACTION_AMOUNT
        defaultAmortizationEntryShouldNotBeFound("transactionAmount.lessThanOrEqual=" + SMALLER_TRANSACTION_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByTransactionAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where transactionAmount is less than DEFAULT_TRANSACTION_AMOUNT
        defaultAmortizationEntryShouldNotBeFound("transactionAmount.lessThan=" + DEFAULT_TRANSACTION_AMOUNT);

        // Get all the amortizationEntryList where transactionAmount is less than UPDATED_TRANSACTION_AMOUNT
        defaultAmortizationEntryShouldBeFound("transactionAmount.lessThan=" + UPDATED_TRANSACTION_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByTransactionAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where transactionAmount is greater than DEFAULT_TRANSACTION_AMOUNT
        defaultAmortizationEntryShouldNotBeFound("transactionAmount.greaterThan=" + DEFAULT_TRANSACTION_AMOUNT);

        // Get all the amortizationEntryList where transactionAmount is greater than SMALLER_TRANSACTION_AMOUNT
        defaultAmortizationEntryShouldBeFound("transactionAmount.greaterThan=" + SMALLER_TRANSACTION_AMOUNT);
    }


    @Test
    @Transactional
    public void getAllAmortizationEntriesByAmortizationDateIsEqualToSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where amortizationDate equals to DEFAULT_AMORTIZATION_DATE
        defaultAmortizationEntryShouldBeFound("amortizationDate.equals=" + DEFAULT_AMORTIZATION_DATE);

        // Get all the amortizationEntryList where amortizationDate equals to UPDATED_AMORTIZATION_DATE
        defaultAmortizationEntryShouldNotBeFound("amortizationDate.equals=" + UPDATED_AMORTIZATION_DATE);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByAmortizationDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where amortizationDate not equals to DEFAULT_AMORTIZATION_DATE
        defaultAmortizationEntryShouldNotBeFound("amortizationDate.notEquals=" + DEFAULT_AMORTIZATION_DATE);

        // Get all the amortizationEntryList where amortizationDate not equals to UPDATED_AMORTIZATION_DATE
        defaultAmortizationEntryShouldBeFound("amortizationDate.notEquals=" + UPDATED_AMORTIZATION_DATE);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByAmortizationDateIsInShouldWork() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where amortizationDate in DEFAULT_AMORTIZATION_DATE or UPDATED_AMORTIZATION_DATE
        defaultAmortizationEntryShouldBeFound("amortizationDate.in=" + DEFAULT_AMORTIZATION_DATE + "," + UPDATED_AMORTIZATION_DATE);

        // Get all the amortizationEntryList where amortizationDate equals to UPDATED_AMORTIZATION_DATE
        defaultAmortizationEntryShouldNotBeFound("amortizationDate.in=" + UPDATED_AMORTIZATION_DATE);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByAmortizationDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where amortizationDate is not null
        defaultAmortizationEntryShouldBeFound("amortizationDate.specified=true");

        // Get all the amortizationEntryList where amortizationDate is null
        defaultAmortizationEntryShouldNotBeFound("amortizationDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByAmortizationDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where amortizationDate is greater than or equal to DEFAULT_AMORTIZATION_DATE
        defaultAmortizationEntryShouldBeFound("amortizationDate.greaterThanOrEqual=" + DEFAULT_AMORTIZATION_DATE);

        // Get all the amortizationEntryList where amortizationDate is greater than or equal to UPDATED_AMORTIZATION_DATE
        defaultAmortizationEntryShouldNotBeFound("amortizationDate.greaterThanOrEqual=" + UPDATED_AMORTIZATION_DATE);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByAmortizationDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where amortizationDate is less than or equal to DEFAULT_AMORTIZATION_DATE
        defaultAmortizationEntryShouldBeFound("amortizationDate.lessThanOrEqual=" + DEFAULT_AMORTIZATION_DATE);

        // Get all the amortizationEntryList where amortizationDate is less than or equal to SMALLER_AMORTIZATION_DATE
        defaultAmortizationEntryShouldNotBeFound("amortizationDate.lessThanOrEqual=" + SMALLER_AMORTIZATION_DATE);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByAmortizationDateIsLessThanSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where amortizationDate is less than DEFAULT_AMORTIZATION_DATE
        defaultAmortizationEntryShouldNotBeFound("amortizationDate.lessThan=" + DEFAULT_AMORTIZATION_DATE);

        // Get all the amortizationEntryList where amortizationDate is less than UPDATED_AMORTIZATION_DATE
        defaultAmortizationEntryShouldBeFound("amortizationDate.lessThan=" + UPDATED_AMORTIZATION_DATE);
    }

    @Test
    @Transactional
    public void getAllAmortizationEntriesByAmortizationDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        // Get all the amortizationEntryList where amortizationDate is greater than DEFAULT_AMORTIZATION_DATE
        defaultAmortizationEntryShouldNotBeFound("amortizationDate.greaterThan=" + DEFAULT_AMORTIZATION_DATE);

        // Get all the amortizationEntryList where amortizationDate is greater than SMALLER_AMORTIZATION_DATE
        defaultAmortizationEntryShouldBeFound("amortizationDate.greaterThan=" + SMALLER_AMORTIZATION_DATE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAmortizationEntryShouldBeFound(String filter) throws Exception {
        restAmortizationEntryMockMvc.perform(get("/api/amortization-entries?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(amortizationEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].accountName").value(hasItem(DEFAULT_ACCOUNT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].accountNumber").value(hasItem(DEFAULT_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].expenseAccountNumber").value(hasItem(DEFAULT_EXPENSE_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].prepaymentNumber").value(hasItem(DEFAULT_PREPAYMENT_NUMBER)))
            .andExpect(jsonPath("$.[*].prepaymentDate").value(hasItem(DEFAULT_PREPAYMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].transactionAmount").value(hasItem(DEFAULT_TRANSACTION_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].amortizationDate").value(hasItem(DEFAULT_AMORTIZATION_DATE.toString())));

        // Check, that the count call also returns 1
        restAmortizationEntryMockMvc.perform(get("/api/amortization-entries/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAmortizationEntryShouldNotBeFound(String filter) throws Exception {
        restAmortizationEntryMockMvc.perform(get("/api/amortization-entries?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAmortizationEntryMockMvc.perform(get("/api/amortization-entries/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingAmortizationEntry() throws Exception {
        // Get the amortizationEntry
        restAmortizationEntryMockMvc.perform(get("/api/amortization-entries/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAmortizationEntry() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        int databaseSizeBeforeUpdate = amortizationEntryRepository.findAll().size();

        // Update the amortizationEntry
        AmortizationEntry updatedAmortizationEntry = amortizationEntryRepository.findById(amortizationEntry.getId()).get();
        // Disconnect from session so that the updates on updatedAmortizationEntry are not directly saved in db
        em.detach(updatedAmortizationEntry);
        updatedAmortizationEntry
            .accountName(UPDATED_ACCOUNT_NAME)
            .description(UPDATED_DESCRIPTION)
            .accountNumber(UPDATED_ACCOUNT_NUMBER)
            .expenseAccountNumber(UPDATED_EXPENSE_ACCOUNT_NUMBER)
            .prepaymentNumber(UPDATED_PREPAYMENT_NUMBER)
            .prepaymentDate(UPDATED_PREPAYMENT_DATE)
            .transactionAmount(UPDATED_TRANSACTION_AMOUNT)
            .amortizationDate(UPDATED_AMORTIZATION_DATE);
        AmortizationEntryDTO amortizationEntryDTO = amortizationEntryMapper.toDto(updatedAmortizationEntry);

        restAmortizationEntryMockMvc.perform(put("/api/amortization-entries")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(amortizationEntryDTO)))
            .andExpect(status().isOk());

        // Validate the AmortizationEntry in the database
        List<AmortizationEntry> amortizationEntryList = amortizationEntryRepository.findAll();
        assertThat(amortizationEntryList).hasSize(databaseSizeBeforeUpdate);
        AmortizationEntry testAmortizationEntry = amortizationEntryList.get(amortizationEntryList.size() - 1);
        assertThat(testAmortizationEntry.getAccountName()).isEqualTo(UPDATED_ACCOUNT_NAME);
        assertThat(testAmortizationEntry.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAmortizationEntry.getAccountNumber()).isEqualTo(UPDATED_ACCOUNT_NUMBER);
        assertThat(testAmortizationEntry.getExpenseAccountNumber()).isEqualTo(UPDATED_EXPENSE_ACCOUNT_NUMBER);
        assertThat(testAmortizationEntry.getPrepaymentNumber()).isEqualTo(UPDATED_PREPAYMENT_NUMBER);
        assertThat(testAmortizationEntry.getPrepaymentDate()).isEqualTo(UPDATED_PREPAYMENT_DATE);
        assertThat(testAmortizationEntry.getTransactionAmount()).isEqualTo(UPDATED_TRANSACTION_AMOUNT);
        assertThat(testAmortizationEntry.getAmortizationDate()).isEqualTo(UPDATED_AMORTIZATION_DATE);

        // Validate the AmortizationEntry in Elasticsearch
        verify(mockAmortizationEntrySearchRepository, times(1)).save(testAmortizationEntry);
    }

    @Test
    @Transactional
    public void updateNonExistingAmortizationEntry() throws Exception {
        int databaseSizeBeforeUpdate = amortizationEntryRepository.findAll().size();

        // Create the AmortizationEntry
        AmortizationEntryDTO amortizationEntryDTO = amortizationEntryMapper.toDto(amortizationEntry);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAmortizationEntryMockMvc.perform(put("/api/amortization-entries")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(amortizationEntryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AmortizationEntry in the database
        List<AmortizationEntry> amortizationEntryList = amortizationEntryRepository.findAll();
        assertThat(amortizationEntryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AmortizationEntry in Elasticsearch
        verify(mockAmortizationEntrySearchRepository, times(0)).save(amortizationEntry);
    }

    @Test
    @Transactional
    public void deleteAmortizationEntry() throws Exception {
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);

        int databaseSizeBeforeDelete = amortizationEntryRepository.findAll().size();

        // Delete the amortizationEntry
        restAmortizationEntryMockMvc.perform(delete("/api/amortization-entries/{id}", amortizationEntry.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AmortizationEntry> amortizationEntryList = amortizationEntryRepository.findAll();
        assertThat(amortizationEntryList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AmortizationEntry in Elasticsearch
        verify(mockAmortizationEntrySearchRepository, times(1)).deleteById(amortizationEntry.getId());
    }

    @Test
    @Transactional
    public void searchAmortizationEntry() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        amortizationEntryRepository.saveAndFlush(amortizationEntry);
        when(mockAmortizationEntrySearchRepository.search(queryStringQuery("id:" + amortizationEntry.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(amortizationEntry), PageRequest.of(0, 1), 1));

        // Search the amortizationEntry
        restAmortizationEntryMockMvc.perform(get("/api/_search/amortization-entries?query=id:" + amortizationEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(amortizationEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].accountName").value(hasItem(DEFAULT_ACCOUNT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].accountNumber").value(hasItem(DEFAULT_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].expenseAccountNumber").value(hasItem(DEFAULT_EXPENSE_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].prepaymentNumber").value(hasItem(DEFAULT_PREPAYMENT_NUMBER)))
            .andExpect(jsonPath("$.[*].prepaymentDate").value(hasItem(DEFAULT_PREPAYMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].transactionAmount").value(hasItem(DEFAULT_TRANSACTION_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].amortizationDate").value(hasItem(DEFAULT_AMORTIZATION_DATE.toString())));
    }
}
