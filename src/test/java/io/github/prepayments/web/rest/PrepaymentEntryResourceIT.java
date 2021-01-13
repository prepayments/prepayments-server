package io.github.prepayments.web.rest;

import io.github.prepayments.PrepaymentsApp;
import io.github.prepayments.domain.PrepaymentEntry;
import io.github.prepayments.repository.PrepaymentEntryRepository;
import io.github.prepayments.repository.search.PrepaymentEntrySearchRepository;
import io.github.prepayments.service.PrepaymentEntryService;
import io.github.prepayments.service.dto.PrepaymentEntryDTO;
import io.github.prepayments.service.mapper.PrepaymentEntryMapper;
import io.github.prepayments.service.dto.PrepaymentEntryCriteria;
import io.github.prepayments.service.PrepaymentEntryQueryService;

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
 * Integration tests for the {@link PrepaymentEntryResource} REST controller.
 */
@SpringBootTest(classes = PrepaymentsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class PrepaymentEntryResourceIT {

    private static final String DEFAULT_ACCOUNT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ACCOUNT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_ACCOUNT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_ACCOUNT_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_PREPAYMENT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PREPAYMENT_NUMBER = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_PREPAYMENT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PREPAYMENT_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_PREPAYMENT_DATE = LocalDate.ofEpochDay(-1L);

    private static final BigDecimal DEFAULT_TRANSACTION_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TRANSACTION_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_TRANSACTION_AMOUNT = new BigDecimal(1 - 1);

    @Autowired
    private PrepaymentEntryRepository prepaymentEntryRepository;

    @Autowired
    private PrepaymentEntryMapper prepaymentEntryMapper;

    @Autowired
    private PrepaymentEntryService prepaymentEntryService;

    /**
     * This repository is mocked in the io.github.prepayments.repository.search test package.
     *
     * @see io.github.prepayments.repository.search.PrepaymentEntrySearchRepositoryMockConfiguration
     */
    @Autowired
    private PrepaymentEntrySearchRepository mockPrepaymentEntrySearchRepository;

    @Autowired
    private PrepaymentEntryQueryService prepaymentEntryQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPrepaymentEntryMockMvc;

    private PrepaymentEntry prepaymentEntry;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PrepaymentEntry createEntity(EntityManager em) {
        PrepaymentEntry prepaymentEntry = new PrepaymentEntry()
            .accountName(DEFAULT_ACCOUNT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .accountNumber(DEFAULT_ACCOUNT_NUMBER)
            .prepaymentNumber(DEFAULT_PREPAYMENT_NUMBER)
            .prepaymentDate(DEFAULT_PREPAYMENT_DATE)
            .transactionAmount(DEFAULT_TRANSACTION_AMOUNT);
        return prepaymentEntry;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PrepaymentEntry createUpdatedEntity(EntityManager em) {
        PrepaymentEntry prepaymentEntry = new PrepaymentEntry()
            .accountName(UPDATED_ACCOUNT_NAME)
            .description(UPDATED_DESCRIPTION)
            .accountNumber(UPDATED_ACCOUNT_NUMBER)
            .prepaymentNumber(UPDATED_PREPAYMENT_NUMBER)
            .prepaymentDate(UPDATED_PREPAYMENT_DATE)
            .transactionAmount(UPDATED_TRANSACTION_AMOUNT);
        return prepaymentEntry;
    }

    @BeforeEach
    public void initTest() {
        prepaymentEntry = createEntity(em);
    }

    @Test
    @Transactional
    public void createPrepaymentEntry() throws Exception {
        int databaseSizeBeforeCreate = prepaymentEntryRepository.findAll().size();
        // Create the PrepaymentEntry
        PrepaymentEntryDTO prepaymentEntryDTO = prepaymentEntryMapper.toDto(prepaymentEntry);
        restPrepaymentEntryMockMvc.perform(post("/api/prepayment-entries")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepaymentEntryDTO)))
            .andExpect(status().isCreated());

        // Validate the PrepaymentEntry in the database
        List<PrepaymentEntry> prepaymentEntryList = prepaymentEntryRepository.findAll();
        assertThat(prepaymentEntryList).hasSize(databaseSizeBeforeCreate + 1);
        PrepaymentEntry testPrepaymentEntry = prepaymentEntryList.get(prepaymentEntryList.size() - 1);
        assertThat(testPrepaymentEntry.getAccountName()).isEqualTo(DEFAULT_ACCOUNT_NAME);
        assertThat(testPrepaymentEntry.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPrepaymentEntry.getAccountNumber()).isEqualTo(DEFAULT_ACCOUNT_NUMBER);
        assertThat(testPrepaymentEntry.getPrepaymentNumber()).isEqualTo(DEFAULT_PREPAYMENT_NUMBER);
        assertThat(testPrepaymentEntry.getPrepaymentDate()).isEqualTo(DEFAULT_PREPAYMENT_DATE);
        assertThat(testPrepaymentEntry.getTransactionAmount()).isEqualTo(DEFAULT_TRANSACTION_AMOUNT);

        // Validate the PrepaymentEntry in Elasticsearch
        verify(mockPrepaymentEntrySearchRepository, times(1)).save(testPrepaymentEntry);
    }

    @Test
    @Transactional
    public void createPrepaymentEntryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = prepaymentEntryRepository.findAll().size();

        // Create the PrepaymentEntry with an existing ID
        prepaymentEntry.setId(1L);
        PrepaymentEntryDTO prepaymentEntryDTO = prepaymentEntryMapper.toDto(prepaymentEntry);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPrepaymentEntryMockMvc.perform(post("/api/prepayment-entries")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepaymentEntryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PrepaymentEntry in the database
        List<PrepaymentEntry> prepaymentEntryList = prepaymentEntryRepository.findAll();
        assertThat(prepaymentEntryList).hasSize(databaseSizeBeforeCreate);

        // Validate the PrepaymentEntry in Elasticsearch
        verify(mockPrepaymentEntrySearchRepository, times(0)).save(prepaymentEntry);
    }


    @Test
    @Transactional
    public void getAllPrepaymentEntries() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList
        restPrepaymentEntryMockMvc.perform(get("/api/prepayment-entries?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prepaymentEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].accountName").value(hasItem(DEFAULT_ACCOUNT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].accountNumber").value(hasItem(DEFAULT_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].prepaymentNumber").value(hasItem(DEFAULT_PREPAYMENT_NUMBER)))
            .andExpect(jsonPath("$.[*].prepaymentDate").value(hasItem(DEFAULT_PREPAYMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].transactionAmount").value(hasItem(DEFAULT_TRANSACTION_AMOUNT.intValue())));
    }
    
    @Test
    @Transactional
    public void getPrepaymentEntry() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get the prepaymentEntry
        restPrepaymentEntryMockMvc.perform(get("/api/prepayment-entries/{id}", prepaymentEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(prepaymentEntry.getId().intValue()))
            .andExpect(jsonPath("$.accountName").value(DEFAULT_ACCOUNT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.accountNumber").value(DEFAULT_ACCOUNT_NUMBER))
            .andExpect(jsonPath("$.prepaymentNumber").value(DEFAULT_PREPAYMENT_NUMBER))
            .andExpect(jsonPath("$.prepaymentDate").value(DEFAULT_PREPAYMENT_DATE.toString()))
            .andExpect(jsonPath("$.transactionAmount").value(DEFAULT_TRANSACTION_AMOUNT.intValue()));
    }


    @Test
    @Transactional
    public void getPrepaymentEntriesByIdFiltering() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        Long id = prepaymentEntry.getId();

        defaultPrepaymentEntryShouldBeFound("id.equals=" + id);
        defaultPrepaymentEntryShouldNotBeFound("id.notEquals=" + id);

        defaultPrepaymentEntryShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPrepaymentEntryShouldNotBeFound("id.greaterThan=" + id);

        defaultPrepaymentEntryShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPrepaymentEntryShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllPrepaymentEntriesByAccountNameIsEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where accountName equals to DEFAULT_ACCOUNT_NAME
        defaultPrepaymentEntryShouldBeFound("accountName.equals=" + DEFAULT_ACCOUNT_NAME);

        // Get all the prepaymentEntryList where accountName equals to UPDATED_ACCOUNT_NAME
        defaultPrepaymentEntryShouldNotBeFound("accountName.equals=" + UPDATED_ACCOUNT_NAME);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByAccountNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where accountName not equals to DEFAULT_ACCOUNT_NAME
        defaultPrepaymentEntryShouldNotBeFound("accountName.notEquals=" + DEFAULT_ACCOUNT_NAME);

        // Get all the prepaymentEntryList where accountName not equals to UPDATED_ACCOUNT_NAME
        defaultPrepaymentEntryShouldBeFound("accountName.notEquals=" + UPDATED_ACCOUNT_NAME);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByAccountNameIsInShouldWork() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where accountName in DEFAULT_ACCOUNT_NAME or UPDATED_ACCOUNT_NAME
        defaultPrepaymentEntryShouldBeFound("accountName.in=" + DEFAULT_ACCOUNT_NAME + "," + UPDATED_ACCOUNT_NAME);

        // Get all the prepaymentEntryList where accountName equals to UPDATED_ACCOUNT_NAME
        defaultPrepaymentEntryShouldNotBeFound("accountName.in=" + UPDATED_ACCOUNT_NAME);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByAccountNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where accountName is not null
        defaultPrepaymentEntryShouldBeFound("accountName.specified=true");

        // Get all the prepaymentEntryList where accountName is null
        defaultPrepaymentEntryShouldNotBeFound("accountName.specified=false");
    }
                @Test
    @Transactional
    public void getAllPrepaymentEntriesByAccountNameContainsSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where accountName contains DEFAULT_ACCOUNT_NAME
        defaultPrepaymentEntryShouldBeFound("accountName.contains=" + DEFAULT_ACCOUNT_NAME);

        // Get all the prepaymentEntryList where accountName contains UPDATED_ACCOUNT_NAME
        defaultPrepaymentEntryShouldNotBeFound("accountName.contains=" + UPDATED_ACCOUNT_NAME);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByAccountNameNotContainsSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where accountName does not contain DEFAULT_ACCOUNT_NAME
        defaultPrepaymentEntryShouldNotBeFound("accountName.doesNotContain=" + DEFAULT_ACCOUNT_NAME);

        // Get all the prepaymentEntryList where accountName does not contain UPDATED_ACCOUNT_NAME
        defaultPrepaymentEntryShouldBeFound("accountName.doesNotContain=" + UPDATED_ACCOUNT_NAME);
    }


    @Test
    @Transactional
    public void getAllPrepaymentEntriesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where description equals to DEFAULT_DESCRIPTION
        defaultPrepaymentEntryShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the prepaymentEntryList where description equals to UPDATED_DESCRIPTION
        defaultPrepaymentEntryShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where description not equals to DEFAULT_DESCRIPTION
        defaultPrepaymentEntryShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the prepaymentEntryList where description not equals to UPDATED_DESCRIPTION
        defaultPrepaymentEntryShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultPrepaymentEntryShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the prepaymentEntryList where description equals to UPDATED_DESCRIPTION
        defaultPrepaymentEntryShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where description is not null
        defaultPrepaymentEntryShouldBeFound("description.specified=true");

        // Get all the prepaymentEntryList where description is null
        defaultPrepaymentEntryShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllPrepaymentEntriesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where description contains DEFAULT_DESCRIPTION
        defaultPrepaymentEntryShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the prepaymentEntryList where description contains UPDATED_DESCRIPTION
        defaultPrepaymentEntryShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where description does not contain DEFAULT_DESCRIPTION
        defaultPrepaymentEntryShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the prepaymentEntryList where description does not contain UPDATED_DESCRIPTION
        defaultPrepaymentEntryShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllPrepaymentEntriesByAccountNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where accountNumber equals to DEFAULT_ACCOUNT_NUMBER
        defaultPrepaymentEntryShouldBeFound("accountNumber.equals=" + DEFAULT_ACCOUNT_NUMBER);

        // Get all the prepaymentEntryList where accountNumber equals to UPDATED_ACCOUNT_NUMBER
        defaultPrepaymentEntryShouldNotBeFound("accountNumber.equals=" + UPDATED_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByAccountNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where accountNumber not equals to DEFAULT_ACCOUNT_NUMBER
        defaultPrepaymentEntryShouldNotBeFound("accountNumber.notEquals=" + DEFAULT_ACCOUNT_NUMBER);

        // Get all the prepaymentEntryList where accountNumber not equals to UPDATED_ACCOUNT_NUMBER
        defaultPrepaymentEntryShouldBeFound("accountNumber.notEquals=" + UPDATED_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByAccountNumberIsInShouldWork() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where accountNumber in DEFAULT_ACCOUNT_NUMBER or UPDATED_ACCOUNT_NUMBER
        defaultPrepaymentEntryShouldBeFound("accountNumber.in=" + DEFAULT_ACCOUNT_NUMBER + "," + UPDATED_ACCOUNT_NUMBER);

        // Get all the prepaymentEntryList where accountNumber equals to UPDATED_ACCOUNT_NUMBER
        defaultPrepaymentEntryShouldNotBeFound("accountNumber.in=" + UPDATED_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByAccountNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where accountNumber is not null
        defaultPrepaymentEntryShouldBeFound("accountNumber.specified=true");

        // Get all the prepaymentEntryList where accountNumber is null
        defaultPrepaymentEntryShouldNotBeFound("accountNumber.specified=false");
    }
                @Test
    @Transactional
    public void getAllPrepaymentEntriesByAccountNumberContainsSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where accountNumber contains DEFAULT_ACCOUNT_NUMBER
        defaultPrepaymentEntryShouldBeFound("accountNumber.contains=" + DEFAULT_ACCOUNT_NUMBER);

        // Get all the prepaymentEntryList where accountNumber contains UPDATED_ACCOUNT_NUMBER
        defaultPrepaymentEntryShouldNotBeFound("accountNumber.contains=" + UPDATED_ACCOUNT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByAccountNumberNotContainsSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where accountNumber does not contain DEFAULT_ACCOUNT_NUMBER
        defaultPrepaymentEntryShouldNotBeFound("accountNumber.doesNotContain=" + DEFAULT_ACCOUNT_NUMBER);

        // Get all the prepaymentEntryList where accountNumber does not contain UPDATED_ACCOUNT_NUMBER
        defaultPrepaymentEntryShouldBeFound("accountNumber.doesNotContain=" + UPDATED_ACCOUNT_NUMBER);
    }


    @Test
    @Transactional
    public void getAllPrepaymentEntriesByPrepaymentNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where prepaymentNumber equals to DEFAULT_PREPAYMENT_NUMBER
        defaultPrepaymentEntryShouldBeFound("prepaymentNumber.equals=" + DEFAULT_PREPAYMENT_NUMBER);

        // Get all the prepaymentEntryList where prepaymentNumber equals to UPDATED_PREPAYMENT_NUMBER
        defaultPrepaymentEntryShouldNotBeFound("prepaymentNumber.equals=" + UPDATED_PREPAYMENT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByPrepaymentNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where prepaymentNumber not equals to DEFAULT_PREPAYMENT_NUMBER
        defaultPrepaymentEntryShouldNotBeFound("prepaymentNumber.notEquals=" + DEFAULT_PREPAYMENT_NUMBER);

        // Get all the prepaymentEntryList where prepaymentNumber not equals to UPDATED_PREPAYMENT_NUMBER
        defaultPrepaymentEntryShouldBeFound("prepaymentNumber.notEquals=" + UPDATED_PREPAYMENT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByPrepaymentNumberIsInShouldWork() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where prepaymentNumber in DEFAULT_PREPAYMENT_NUMBER or UPDATED_PREPAYMENT_NUMBER
        defaultPrepaymentEntryShouldBeFound("prepaymentNumber.in=" + DEFAULT_PREPAYMENT_NUMBER + "," + UPDATED_PREPAYMENT_NUMBER);

        // Get all the prepaymentEntryList where prepaymentNumber equals to UPDATED_PREPAYMENT_NUMBER
        defaultPrepaymentEntryShouldNotBeFound("prepaymentNumber.in=" + UPDATED_PREPAYMENT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByPrepaymentNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where prepaymentNumber is not null
        defaultPrepaymentEntryShouldBeFound("prepaymentNumber.specified=true");

        // Get all the prepaymentEntryList where prepaymentNumber is null
        defaultPrepaymentEntryShouldNotBeFound("prepaymentNumber.specified=false");
    }
                @Test
    @Transactional
    public void getAllPrepaymentEntriesByPrepaymentNumberContainsSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where prepaymentNumber contains DEFAULT_PREPAYMENT_NUMBER
        defaultPrepaymentEntryShouldBeFound("prepaymentNumber.contains=" + DEFAULT_PREPAYMENT_NUMBER);

        // Get all the prepaymentEntryList where prepaymentNumber contains UPDATED_PREPAYMENT_NUMBER
        defaultPrepaymentEntryShouldNotBeFound("prepaymentNumber.contains=" + UPDATED_PREPAYMENT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByPrepaymentNumberNotContainsSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where prepaymentNumber does not contain DEFAULT_PREPAYMENT_NUMBER
        defaultPrepaymentEntryShouldNotBeFound("prepaymentNumber.doesNotContain=" + DEFAULT_PREPAYMENT_NUMBER);

        // Get all the prepaymentEntryList where prepaymentNumber does not contain UPDATED_PREPAYMENT_NUMBER
        defaultPrepaymentEntryShouldBeFound("prepaymentNumber.doesNotContain=" + UPDATED_PREPAYMENT_NUMBER);
    }


    @Test
    @Transactional
    public void getAllPrepaymentEntriesByPrepaymentDateIsEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where prepaymentDate equals to DEFAULT_PREPAYMENT_DATE
        defaultPrepaymentEntryShouldBeFound("prepaymentDate.equals=" + DEFAULT_PREPAYMENT_DATE);

        // Get all the prepaymentEntryList where prepaymentDate equals to UPDATED_PREPAYMENT_DATE
        defaultPrepaymentEntryShouldNotBeFound("prepaymentDate.equals=" + UPDATED_PREPAYMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByPrepaymentDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where prepaymentDate not equals to DEFAULT_PREPAYMENT_DATE
        defaultPrepaymentEntryShouldNotBeFound("prepaymentDate.notEquals=" + DEFAULT_PREPAYMENT_DATE);

        // Get all the prepaymentEntryList where prepaymentDate not equals to UPDATED_PREPAYMENT_DATE
        defaultPrepaymentEntryShouldBeFound("prepaymentDate.notEquals=" + UPDATED_PREPAYMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByPrepaymentDateIsInShouldWork() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where prepaymentDate in DEFAULT_PREPAYMENT_DATE or UPDATED_PREPAYMENT_DATE
        defaultPrepaymentEntryShouldBeFound("prepaymentDate.in=" + DEFAULT_PREPAYMENT_DATE + "," + UPDATED_PREPAYMENT_DATE);

        // Get all the prepaymentEntryList where prepaymentDate equals to UPDATED_PREPAYMENT_DATE
        defaultPrepaymentEntryShouldNotBeFound("prepaymentDate.in=" + UPDATED_PREPAYMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByPrepaymentDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where prepaymentDate is not null
        defaultPrepaymentEntryShouldBeFound("prepaymentDate.specified=true");

        // Get all the prepaymentEntryList where prepaymentDate is null
        defaultPrepaymentEntryShouldNotBeFound("prepaymentDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByPrepaymentDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where prepaymentDate is greater than or equal to DEFAULT_PREPAYMENT_DATE
        defaultPrepaymentEntryShouldBeFound("prepaymentDate.greaterThanOrEqual=" + DEFAULT_PREPAYMENT_DATE);

        // Get all the prepaymentEntryList where prepaymentDate is greater than or equal to UPDATED_PREPAYMENT_DATE
        defaultPrepaymentEntryShouldNotBeFound("prepaymentDate.greaterThanOrEqual=" + UPDATED_PREPAYMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByPrepaymentDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where prepaymentDate is less than or equal to DEFAULT_PREPAYMENT_DATE
        defaultPrepaymentEntryShouldBeFound("prepaymentDate.lessThanOrEqual=" + DEFAULT_PREPAYMENT_DATE);

        // Get all the prepaymentEntryList where prepaymentDate is less than or equal to SMALLER_PREPAYMENT_DATE
        defaultPrepaymentEntryShouldNotBeFound("prepaymentDate.lessThanOrEqual=" + SMALLER_PREPAYMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByPrepaymentDateIsLessThanSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where prepaymentDate is less than DEFAULT_PREPAYMENT_DATE
        defaultPrepaymentEntryShouldNotBeFound("prepaymentDate.lessThan=" + DEFAULT_PREPAYMENT_DATE);

        // Get all the prepaymentEntryList where prepaymentDate is less than UPDATED_PREPAYMENT_DATE
        defaultPrepaymentEntryShouldBeFound("prepaymentDate.lessThan=" + UPDATED_PREPAYMENT_DATE);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByPrepaymentDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where prepaymentDate is greater than DEFAULT_PREPAYMENT_DATE
        defaultPrepaymentEntryShouldNotBeFound("prepaymentDate.greaterThan=" + DEFAULT_PREPAYMENT_DATE);

        // Get all the prepaymentEntryList where prepaymentDate is greater than SMALLER_PREPAYMENT_DATE
        defaultPrepaymentEntryShouldBeFound("prepaymentDate.greaterThan=" + SMALLER_PREPAYMENT_DATE);
    }


    @Test
    @Transactional
    public void getAllPrepaymentEntriesByTransactionAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where transactionAmount equals to DEFAULT_TRANSACTION_AMOUNT
        defaultPrepaymentEntryShouldBeFound("transactionAmount.equals=" + DEFAULT_TRANSACTION_AMOUNT);

        // Get all the prepaymentEntryList where transactionAmount equals to UPDATED_TRANSACTION_AMOUNT
        defaultPrepaymentEntryShouldNotBeFound("transactionAmount.equals=" + UPDATED_TRANSACTION_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByTransactionAmountIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where transactionAmount not equals to DEFAULT_TRANSACTION_AMOUNT
        defaultPrepaymentEntryShouldNotBeFound("transactionAmount.notEquals=" + DEFAULT_TRANSACTION_AMOUNT);

        // Get all the prepaymentEntryList where transactionAmount not equals to UPDATED_TRANSACTION_AMOUNT
        defaultPrepaymentEntryShouldBeFound("transactionAmount.notEquals=" + UPDATED_TRANSACTION_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByTransactionAmountIsInShouldWork() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where transactionAmount in DEFAULT_TRANSACTION_AMOUNT or UPDATED_TRANSACTION_AMOUNT
        defaultPrepaymentEntryShouldBeFound("transactionAmount.in=" + DEFAULT_TRANSACTION_AMOUNT + "," + UPDATED_TRANSACTION_AMOUNT);

        // Get all the prepaymentEntryList where transactionAmount equals to UPDATED_TRANSACTION_AMOUNT
        defaultPrepaymentEntryShouldNotBeFound("transactionAmount.in=" + UPDATED_TRANSACTION_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByTransactionAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where transactionAmount is not null
        defaultPrepaymentEntryShouldBeFound("transactionAmount.specified=true");

        // Get all the prepaymentEntryList where transactionAmount is null
        defaultPrepaymentEntryShouldNotBeFound("transactionAmount.specified=false");
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByTransactionAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where transactionAmount is greater than or equal to DEFAULT_TRANSACTION_AMOUNT
        defaultPrepaymentEntryShouldBeFound("transactionAmount.greaterThanOrEqual=" + DEFAULT_TRANSACTION_AMOUNT);

        // Get all the prepaymentEntryList where transactionAmount is greater than or equal to UPDATED_TRANSACTION_AMOUNT
        defaultPrepaymentEntryShouldNotBeFound("transactionAmount.greaterThanOrEqual=" + UPDATED_TRANSACTION_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByTransactionAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where transactionAmount is less than or equal to DEFAULT_TRANSACTION_AMOUNT
        defaultPrepaymentEntryShouldBeFound("transactionAmount.lessThanOrEqual=" + DEFAULT_TRANSACTION_AMOUNT);

        // Get all the prepaymentEntryList where transactionAmount is less than or equal to SMALLER_TRANSACTION_AMOUNT
        defaultPrepaymentEntryShouldNotBeFound("transactionAmount.lessThanOrEqual=" + SMALLER_TRANSACTION_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByTransactionAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where transactionAmount is less than DEFAULT_TRANSACTION_AMOUNT
        defaultPrepaymentEntryShouldNotBeFound("transactionAmount.lessThan=" + DEFAULT_TRANSACTION_AMOUNT);

        // Get all the prepaymentEntryList where transactionAmount is less than UPDATED_TRANSACTION_AMOUNT
        defaultPrepaymentEntryShouldBeFound("transactionAmount.lessThan=" + UPDATED_TRANSACTION_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllPrepaymentEntriesByTransactionAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        // Get all the prepaymentEntryList where transactionAmount is greater than DEFAULT_TRANSACTION_AMOUNT
        defaultPrepaymentEntryShouldNotBeFound("transactionAmount.greaterThan=" + DEFAULT_TRANSACTION_AMOUNT);

        // Get all the prepaymentEntryList where transactionAmount is greater than SMALLER_TRANSACTION_AMOUNT
        defaultPrepaymentEntryShouldBeFound("transactionAmount.greaterThan=" + SMALLER_TRANSACTION_AMOUNT);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPrepaymentEntryShouldBeFound(String filter) throws Exception {
        restPrepaymentEntryMockMvc.perform(get("/api/prepayment-entries?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prepaymentEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].accountName").value(hasItem(DEFAULT_ACCOUNT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].accountNumber").value(hasItem(DEFAULT_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].prepaymentNumber").value(hasItem(DEFAULT_PREPAYMENT_NUMBER)))
            .andExpect(jsonPath("$.[*].prepaymentDate").value(hasItem(DEFAULT_PREPAYMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].transactionAmount").value(hasItem(DEFAULT_TRANSACTION_AMOUNT.intValue())));

        // Check, that the count call also returns 1
        restPrepaymentEntryMockMvc.perform(get("/api/prepayment-entries/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPrepaymentEntryShouldNotBeFound(String filter) throws Exception {
        restPrepaymentEntryMockMvc.perform(get("/api/prepayment-entries?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPrepaymentEntryMockMvc.perform(get("/api/prepayment-entries/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingPrepaymentEntry() throws Exception {
        // Get the prepaymentEntry
        restPrepaymentEntryMockMvc.perform(get("/api/prepayment-entries/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePrepaymentEntry() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        int databaseSizeBeforeUpdate = prepaymentEntryRepository.findAll().size();

        // Update the prepaymentEntry
        PrepaymentEntry updatedPrepaymentEntry = prepaymentEntryRepository.findById(prepaymentEntry.getId()).get();
        // Disconnect from session so that the updates on updatedPrepaymentEntry are not directly saved in db
        em.detach(updatedPrepaymentEntry);
        updatedPrepaymentEntry
            .accountName(UPDATED_ACCOUNT_NAME)
            .description(UPDATED_DESCRIPTION)
            .accountNumber(UPDATED_ACCOUNT_NUMBER)
            .prepaymentNumber(UPDATED_PREPAYMENT_NUMBER)
            .prepaymentDate(UPDATED_PREPAYMENT_DATE)
            .transactionAmount(UPDATED_TRANSACTION_AMOUNT);
        PrepaymentEntryDTO prepaymentEntryDTO = prepaymentEntryMapper.toDto(updatedPrepaymentEntry);

        restPrepaymentEntryMockMvc.perform(put("/api/prepayment-entries")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepaymentEntryDTO)))
            .andExpect(status().isOk());

        // Validate the PrepaymentEntry in the database
        List<PrepaymentEntry> prepaymentEntryList = prepaymentEntryRepository.findAll();
        assertThat(prepaymentEntryList).hasSize(databaseSizeBeforeUpdate);
        PrepaymentEntry testPrepaymentEntry = prepaymentEntryList.get(prepaymentEntryList.size() - 1);
        assertThat(testPrepaymentEntry.getAccountName()).isEqualTo(UPDATED_ACCOUNT_NAME);
        assertThat(testPrepaymentEntry.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPrepaymentEntry.getAccountNumber()).isEqualTo(UPDATED_ACCOUNT_NUMBER);
        assertThat(testPrepaymentEntry.getPrepaymentNumber()).isEqualTo(UPDATED_PREPAYMENT_NUMBER);
        assertThat(testPrepaymentEntry.getPrepaymentDate()).isEqualTo(UPDATED_PREPAYMENT_DATE);
        assertThat(testPrepaymentEntry.getTransactionAmount()).isEqualTo(UPDATED_TRANSACTION_AMOUNT);

        // Validate the PrepaymentEntry in Elasticsearch
        verify(mockPrepaymentEntrySearchRepository, times(1)).save(testPrepaymentEntry);
    }

    @Test
    @Transactional
    public void updateNonExistingPrepaymentEntry() throws Exception {
        int databaseSizeBeforeUpdate = prepaymentEntryRepository.findAll().size();

        // Create the PrepaymentEntry
        PrepaymentEntryDTO prepaymentEntryDTO = prepaymentEntryMapper.toDto(prepaymentEntry);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPrepaymentEntryMockMvc.perform(put("/api/prepayment-entries")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepaymentEntryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PrepaymentEntry in the database
        List<PrepaymentEntry> prepaymentEntryList = prepaymentEntryRepository.findAll();
        assertThat(prepaymentEntryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PrepaymentEntry in Elasticsearch
        verify(mockPrepaymentEntrySearchRepository, times(0)).save(prepaymentEntry);
    }

    @Test
    @Transactional
    public void deletePrepaymentEntry() throws Exception {
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);

        int databaseSizeBeforeDelete = prepaymentEntryRepository.findAll().size();

        // Delete the prepaymentEntry
        restPrepaymentEntryMockMvc.perform(delete("/api/prepayment-entries/{id}", prepaymentEntry.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PrepaymentEntry> prepaymentEntryList = prepaymentEntryRepository.findAll();
        assertThat(prepaymentEntryList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the PrepaymentEntry in Elasticsearch
        verify(mockPrepaymentEntrySearchRepository, times(1)).deleteById(prepaymentEntry.getId());
    }

    @Test
    @Transactional
    public void searchPrepaymentEntry() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        prepaymentEntryRepository.saveAndFlush(prepaymentEntry);
        when(mockPrepaymentEntrySearchRepository.search(queryStringQuery("id:" + prepaymentEntry.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(prepaymentEntry), PageRequest.of(0, 1), 1));

        // Search the prepaymentEntry
        restPrepaymentEntryMockMvc.perform(get("/api/_search/prepayment-entries?query=id:" + prepaymentEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prepaymentEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].accountName").value(hasItem(DEFAULT_ACCOUNT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].accountNumber").value(hasItem(DEFAULT_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].prepaymentNumber").value(hasItem(DEFAULT_PREPAYMENT_NUMBER)))
            .andExpect(jsonPath("$.[*].prepaymentDate").value(hasItem(DEFAULT_PREPAYMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].transactionAmount").value(hasItem(DEFAULT_TRANSACTION_AMOUNT.intValue())));
    }
}
