package io.github.prepayments.web.rest;

import io.github.prepayments.PrepaymentsApp;
import io.github.prepayments.domain.CompilationRequest;
import io.github.prepayments.repository.CompilationRequestRepository;
import io.github.prepayments.repository.search.CompilationRequestSearchRepository;
import io.github.prepayments.service.CompilationRequestService;
import io.github.prepayments.service.dto.CompilationRequestDTO;
import io.github.prepayments.service.mapper.CompilationRequestMapper;
import io.github.prepayments.service.dto.CompilationRequestCriteria;
import io.github.prepayments.service.CompilationRequestQueryService;

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
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.prepayments.domain.enumeration.CompilationStatus;
import io.github.prepayments.domain.enumeration.CompilationType;
/**
 * Integration tests for the {@link CompilationRequestResource} REST controller.
 */
@SpringBootTest(classes = PrepaymentsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class CompilationRequestResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Long DEFAULT_FILE_UPLOAD_ID = 1L;
    private static final Long UPDATED_FILE_UPLOAD_ID = 2L;
    private static final Long SMALLER_FILE_UPLOAD_ID = 1L - 1L;

    private static final CompilationStatus DEFAULT_COMPILATION_STATUS = CompilationStatus.IN_PROGRESS;
    private static final CompilationStatus UPDATED_COMPILATION_STATUS = CompilationStatus.COMPLETE;

    private static final CompilationType DEFAULT_COMPILATION_TYPE = CompilationType.AMORTIZATION_ENTRY_COMPILATION;
    private static final CompilationType UPDATED_COMPILATION_TYPE = CompilationType.PREPAYMENT_ENTRY_COMPILATION;

    private static final String DEFAULT_COMPILATION_TOKEN = "AAAAAAAAAA";
    private static final String UPDATED_COMPILATION_TOKEN = "BBBBBBBBBB";

    @Autowired
    private CompilationRequestRepository compilationRequestRepository;

    @Autowired
    private CompilationRequestMapper compilationRequestMapper;

    @Autowired
    private CompilationRequestService compilationRequestService;

    /**
     * This repository is mocked in the io.github.prepayments.repository.search test package.
     *
     * @see io.github.prepayments.repository.search.CompilationRequestSearchRepositoryMockConfiguration
     */
    @Autowired
    private CompilationRequestSearchRepository mockCompilationRequestSearchRepository;

    @Autowired
    private CompilationRequestQueryService compilationRequestQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCompilationRequestMockMvc;

    private CompilationRequest compilationRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CompilationRequest createEntity(EntityManager em) {
        CompilationRequest compilationRequest = new CompilationRequest()
            .description(DEFAULT_DESCRIPTION)
            .fileUploadId(DEFAULT_FILE_UPLOAD_ID)
            .compilationStatus(DEFAULT_COMPILATION_STATUS)
            .compilationType(DEFAULT_COMPILATION_TYPE)
            .compilationToken(DEFAULT_COMPILATION_TOKEN);
        return compilationRequest;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CompilationRequest createUpdatedEntity(EntityManager em) {
        CompilationRequest compilationRequest = new CompilationRequest()
            .description(UPDATED_DESCRIPTION)
            .fileUploadId(UPDATED_FILE_UPLOAD_ID)
            .compilationStatus(UPDATED_COMPILATION_STATUS)
            .compilationType(UPDATED_COMPILATION_TYPE)
            .compilationToken(UPDATED_COMPILATION_TOKEN);
        return compilationRequest;
    }

    @BeforeEach
    public void initTest() {
        compilationRequest = createEntity(em);
    }

    @Test
    @Transactional
    public void createCompilationRequest() throws Exception {
        int databaseSizeBeforeCreate = compilationRequestRepository.findAll().size();
        // Create the CompilationRequest
        CompilationRequestDTO compilationRequestDTO = compilationRequestMapper.toDto(compilationRequest);
        restCompilationRequestMockMvc.perform(post("/api/compilation-requests")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(compilationRequestDTO)))
            .andExpect(status().isCreated());

        // Validate the CompilationRequest in the database
        List<CompilationRequest> compilationRequestList = compilationRequestRepository.findAll();
        assertThat(compilationRequestList).hasSize(databaseSizeBeforeCreate + 1);
        CompilationRequest testCompilationRequest = compilationRequestList.get(compilationRequestList.size() - 1);
        assertThat(testCompilationRequest.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCompilationRequest.getFileUploadId()).isEqualTo(DEFAULT_FILE_UPLOAD_ID);
        assertThat(testCompilationRequest.getCompilationStatus()).isEqualTo(DEFAULT_COMPILATION_STATUS);
        assertThat(testCompilationRequest.getCompilationType()).isEqualTo(DEFAULT_COMPILATION_TYPE);
        assertThat(testCompilationRequest.getCompilationToken()).isEqualTo(DEFAULT_COMPILATION_TOKEN);

        // Validate the CompilationRequest in Elasticsearch
        verify(mockCompilationRequestSearchRepository, times(1)).save(testCompilationRequest);
    }

    @Test
    @Transactional
    public void createCompilationRequestWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = compilationRequestRepository.findAll().size();

        // Create the CompilationRequest with an existing ID
        compilationRequest.setId(1L);
        CompilationRequestDTO compilationRequestDTO = compilationRequestMapper.toDto(compilationRequest);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCompilationRequestMockMvc.perform(post("/api/compilation-requests")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(compilationRequestDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CompilationRequest in the database
        List<CompilationRequest> compilationRequestList = compilationRequestRepository.findAll();
        assertThat(compilationRequestList).hasSize(databaseSizeBeforeCreate);

        // Validate the CompilationRequest in Elasticsearch
        verify(mockCompilationRequestSearchRepository, times(0)).save(compilationRequest);
    }


    @Test
    @Transactional
    public void getAllCompilationRequests() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList
        restCompilationRequestMockMvc.perform(get("/api/compilation-requests?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(compilationRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].fileUploadId").value(hasItem(DEFAULT_FILE_UPLOAD_ID.intValue())))
            .andExpect(jsonPath("$.[*].compilationStatus").value(hasItem(DEFAULT_COMPILATION_STATUS.toString())))
            .andExpect(jsonPath("$.[*].compilationType").value(hasItem(DEFAULT_COMPILATION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].compilationToken").value(hasItem(DEFAULT_COMPILATION_TOKEN)));
    }
    
    @Test
    @Transactional
    public void getCompilationRequest() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get the compilationRequest
        restCompilationRequestMockMvc.perform(get("/api/compilation-requests/{id}", compilationRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(compilationRequest.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.fileUploadId").value(DEFAULT_FILE_UPLOAD_ID.intValue()))
            .andExpect(jsonPath("$.compilationStatus").value(DEFAULT_COMPILATION_STATUS.toString()))
            .andExpect(jsonPath("$.compilationType").value(DEFAULT_COMPILATION_TYPE.toString()))
            .andExpect(jsonPath("$.compilationToken").value(DEFAULT_COMPILATION_TOKEN));
    }


    @Test
    @Transactional
    public void getCompilationRequestsByIdFiltering() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        Long id = compilationRequest.getId();

        defaultCompilationRequestShouldBeFound("id.equals=" + id);
        defaultCompilationRequestShouldNotBeFound("id.notEquals=" + id);

        defaultCompilationRequestShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCompilationRequestShouldNotBeFound("id.greaterThan=" + id);

        defaultCompilationRequestShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCompilationRequestShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllCompilationRequestsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where description equals to DEFAULT_DESCRIPTION
        defaultCompilationRequestShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the compilationRequestList where description equals to UPDATED_DESCRIPTION
        defaultCompilationRequestShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllCompilationRequestsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where description not equals to DEFAULT_DESCRIPTION
        defaultCompilationRequestShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the compilationRequestList where description not equals to UPDATED_DESCRIPTION
        defaultCompilationRequestShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllCompilationRequestsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultCompilationRequestShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the compilationRequestList where description equals to UPDATED_DESCRIPTION
        defaultCompilationRequestShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllCompilationRequestsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where description is not null
        defaultCompilationRequestShouldBeFound("description.specified=true");

        // Get all the compilationRequestList where description is null
        defaultCompilationRequestShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllCompilationRequestsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where description contains DEFAULT_DESCRIPTION
        defaultCompilationRequestShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the compilationRequestList where description contains UPDATED_DESCRIPTION
        defaultCompilationRequestShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllCompilationRequestsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where description does not contain DEFAULT_DESCRIPTION
        defaultCompilationRequestShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the compilationRequestList where description does not contain UPDATED_DESCRIPTION
        defaultCompilationRequestShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllCompilationRequestsByFileUploadIdIsEqualToSomething() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where fileUploadId equals to DEFAULT_FILE_UPLOAD_ID
        defaultCompilationRequestShouldBeFound("fileUploadId.equals=" + DEFAULT_FILE_UPLOAD_ID);

        // Get all the compilationRequestList where fileUploadId equals to UPDATED_FILE_UPLOAD_ID
        defaultCompilationRequestShouldNotBeFound("fileUploadId.equals=" + UPDATED_FILE_UPLOAD_ID);
    }

    @Test
    @Transactional
    public void getAllCompilationRequestsByFileUploadIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where fileUploadId not equals to DEFAULT_FILE_UPLOAD_ID
        defaultCompilationRequestShouldNotBeFound("fileUploadId.notEquals=" + DEFAULT_FILE_UPLOAD_ID);

        // Get all the compilationRequestList where fileUploadId not equals to UPDATED_FILE_UPLOAD_ID
        defaultCompilationRequestShouldBeFound("fileUploadId.notEquals=" + UPDATED_FILE_UPLOAD_ID);
    }

    @Test
    @Transactional
    public void getAllCompilationRequestsByFileUploadIdIsInShouldWork() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where fileUploadId in DEFAULT_FILE_UPLOAD_ID or UPDATED_FILE_UPLOAD_ID
        defaultCompilationRequestShouldBeFound("fileUploadId.in=" + DEFAULT_FILE_UPLOAD_ID + "," + UPDATED_FILE_UPLOAD_ID);

        // Get all the compilationRequestList where fileUploadId equals to UPDATED_FILE_UPLOAD_ID
        defaultCompilationRequestShouldNotBeFound("fileUploadId.in=" + UPDATED_FILE_UPLOAD_ID);
    }

    @Test
    @Transactional
    public void getAllCompilationRequestsByFileUploadIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where fileUploadId is not null
        defaultCompilationRequestShouldBeFound("fileUploadId.specified=true");

        // Get all the compilationRequestList where fileUploadId is null
        defaultCompilationRequestShouldNotBeFound("fileUploadId.specified=false");
    }

    @Test
    @Transactional
    public void getAllCompilationRequestsByFileUploadIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where fileUploadId is greater than or equal to DEFAULT_FILE_UPLOAD_ID
        defaultCompilationRequestShouldBeFound("fileUploadId.greaterThanOrEqual=" + DEFAULT_FILE_UPLOAD_ID);

        // Get all the compilationRequestList where fileUploadId is greater than or equal to UPDATED_FILE_UPLOAD_ID
        defaultCompilationRequestShouldNotBeFound("fileUploadId.greaterThanOrEqual=" + UPDATED_FILE_UPLOAD_ID);
    }

    @Test
    @Transactional
    public void getAllCompilationRequestsByFileUploadIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where fileUploadId is less than or equal to DEFAULT_FILE_UPLOAD_ID
        defaultCompilationRequestShouldBeFound("fileUploadId.lessThanOrEqual=" + DEFAULT_FILE_UPLOAD_ID);

        // Get all the compilationRequestList where fileUploadId is less than or equal to SMALLER_FILE_UPLOAD_ID
        defaultCompilationRequestShouldNotBeFound("fileUploadId.lessThanOrEqual=" + SMALLER_FILE_UPLOAD_ID);
    }

    @Test
    @Transactional
    public void getAllCompilationRequestsByFileUploadIdIsLessThanSomething() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where fileUploadId is less than DEFAULT_FILE_UPLOAD_ID
        defaultCompilationRequestShouldNotBeFound("fileUploadId.lessThan=" + DEFAULT_FILE_UPLOAD_ID);

        // Get all the compilationRequestList where fileUploadId is less than UPDATED_FILE_UPLOAD_ID
        defaultCompilationRequestShouldBeFound("fileUploadId.lessThan=" + UPDATED_FILE_UPLOAD_ID);
    }

    @Test
    @Transactional
    public void getAllCompilationRequestsByFileUploadIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where fileUploadId is greater than DEFAULT_FILE_UPLOAD_ID
        defaultCompilationRequestShouldNotBeFound("fileUploadId.greaterThan=" + DEFAULT_FILE_UPLOAD_ID);

        // Get all the compilationRequestList where fileUploadId is greater than SMALLER_FILE_UPLOAD_ID
        defaultCompilationRequestShouldBeFound("fileUploadId.greaterThan=" + SMALLER_FILE_UPLOAD_ID);
    }


    @Test
    @Transactional
    public void getAllCompilationRequestsByCompilationStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where compilationStatus equals to DEFAULT_COMPILATION_STATUS
        defaultCompilationRequestShouldBeFound("compilationStatus.equals=" + DEFAULT_COMPILATION_STATUS);

        // Get all the compilationRequestList where compilationStatus equals to UPDATED_COMPILATION_STATUS
        defaultCompilationRequestShouldNotBeFound("compilationStatus.equals=" + UPDATED_COMPILATION_STATUS);
    }

    @Test
    @Transactional
    public void getAllCompilationRequestsByCompilationStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where compilationStatus not equals to DEFAULT_COMPILATION_STATUS
        defaultCompilationRequestShouldNotBeFound("compilationStatus.notEquals=" + DEFAULT_COMPILATION_STATUS);

        // Get all the compilationRequestList where compilationStatus not equals to UPDATED_COMPILATION_STATUS
        defaultCompilationRequestShouldBeFound("compilationStatus.notEquals=" + UPDATED_COMPILATION_STATUS);
    }

    @Test
    @Transactional
    public void getAllCompilationRequestsByCompilationStatusIsInShouldWork() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where compilationStatus in DEFAULT_COMPILATION_STATUS or UPDATED_COMPILATION_STATUS
        defaultCompilationRequestShouldBeFound("compilationStatus.in=" + DEFAULT_COMPILATION_STATUS + "," + UPDATED_COMPILATION_STATUS);

        // Get all the compilationRequestList where compilationStatus equals to UPDATED_COMPILATION_STATUS
        defaultCompilationRequestShouldNotBeFound("compilationStatus.in=" + UPDATED_COMPILATION_STATUS);
    }

    @Test
    @Transactional
    public void getAllCompilationRequestsByCompilationStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where compilationStatus is not null
        defaultCompilationRequestShouldBeFound("compilationStatus.specified=true");

        // Get all the compilationRequestList where compilationStatus is null
        defaultCompilationRequestShouldNotBeFound("compilationStatus.specified=false");
    }

    @Test
    @Transactional
    public void getAllCompilationRequestsByCompilationTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where compilationType equals to DEFAULT_COMPILATION_TYPE
        defaultCompilationRequestShouldBeFound("compilationType.equals=" + DEFAULT_COMPILATION_TYPE);

        // Get all the compilationRequestList where compilationType equals to UPDATED_COMPILATION_TYPE
        defaultCompilationRequestShouldNotBeFound("compilationType.equals=" + UPDATED_COMPILATION_TYPE);
    }

    @Test
    @Transactional
    public void getAllCompilationRequestsByCompilationTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where compilationType not equals to DEFAULT_COMPILATION_TYPE
        defaultCompilationRequestShouldNotBeFound("compilationType.notEquals=" + DEFAULT_COMPILATION_TYPE);

        // Get all the compilationRequestList where compilationType not equals to UPDATED_COMPILATION_TYPE
        defaultCompilationRequestShouldBeFound("compilationType.notEquals=" + UPDATED_COMPILATION_TYPE);
    }

    @Test
    @Transactional
    public void getAllCompilationRequestsByCompilationTypeIsInShouldWork() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where compilationType in DEFAULT_COMPILATION_TYPE or UPDATED_COMPILATION_TYPE
        defaultCompilationRequestShouldBeFound("compilationType.in=" + DEFAULT_COMPILATION_TYPE + "," + UPDATED_COMPILATION_TYPE);

        // Get all the compilationRequestList where compilationType equals to UPDATED_COMPILATION_TYPE
        defaultCompilationRequestShouldNotBeFound("compilationType.in=" + UPDATED_COMPILATION_TYPE);
    }

    @Test
    @Transactional
    public void getAllCompilationRequestsByCompilationTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where compilationType is not null
        defaultCompilationRequestShouldBeFound("compilationType.specified=true");

        // Get all the compilationRequestList where compilationType is null
        defaultCompilationRequestShouldNotBeFound("compilationType.specified=false");
    }

    @Test
    @Transactional
    public void getAllCompilationRequestsByCompilationTokenIsEqualToSomething() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where compilationToken equals to DEFAULT_COMPILATION_TOKEN
        defaultCompilationRequestShouldBeFound("compilationToken.equals=" + DEFAULT_COMPILATION_TOKEN);

        // Get all the compilationRequestList where compilationToken equals to UPDATED_COMPILATION_TOKEN
        defaultCompilationRequestShouldNotBeFound("compilationToken.equals=" + UPDATED_COMPILATION_TOKEN);
    }

    @Test
    @Transactional
    public void getAllCompilationRequestsByCompilationTokenIsNotEqualToSomething() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where compilationToken not equals to DEFAULT_COMPILATION_TOKEN
        defaultCompilationRequestShouldNotBeFound("compilationToken.notEquals=" + DEFAULT_COMPILATION_TOKEN);

        // Get all the compilationRequestList where compilationToken not equals to UPDATED_COMPILATION_TOKEN
        defaultCompilationRequestShouldBeFound("compilationToken.notEquals=" + UPDATED_COMPILATION_TOKEN);
    }

    @Test
    @Transactional
    public void getAllCompilationRequestsByCompilationTokenIsInShouldWork() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where compilationToken in DEFAULT_COMPILATION_TOKEN or UPDATED_COMPILATION_TOKEN
        defaultCompilationRequestShouldBeFound("compilationToken.in=" + DEFAULT_COMPILATION_TOKEN + "," + UPDATED_COMPILATION_TOKEN);

        // Get all the compilationRequestList where compilationToken equals to UPDATED_COMPILATION_TOKEN
        defaultCompilationRequestShouldNotBeFound("compilationToken.in=" + UPDATED_COMPILATION_TOKEN);
    }

    @Test
    @Transactional
    public void getAllCompilationRequestsByCompilationTokenIsNullOrNotNull() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where compilationToken is not null
        defaultCompilationRequestShouldBeFound("compilationToken.specified=true");

        // Get all the compilationRequestList where compilationToken is null
        defaultCompilationRequestShouldNotBeFound("compilationToken.specified=false");
    }
                @Test
    @Transactional
    public void getAllCompilationRequestsByCompilationTokenContainsSomething() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where compilationToken contains DEFAULT_COMPILATION_TOKEN
        defaultCompilationRequestShouldBeFound("compilationToken.contains=" + DEFAULT_COMPILATION_TOKEN);

        // Get all the compilationRequestList where compilationToken contains UPDATED_COMPILATION_TOKEN
        defaultCompilationRequestShouldNotBeFound("compilationToken.contains=" + UPDATED_COMPILATION_TOKEN);
    }

    @Test
    @Transactional
    public void getAllCompilationRequestsByCompilationTokenNotContainsSomething() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        // Get all the compilationRequestList where compilationToken does not contain DEFAULT_COMPILATION_TOKEN
        defaultCompilationRequestShouldNotBeFound("compilationToken.doesNotContain=" + DEFAULT_COMPILATION_TOKEN);

        // Get all the compilationRequestList where compilationToken does not contain UPDATED_COMPILATION_TOKEN
        defaultCompilationRequestShouldBeFound("compilationToken.doesNotContain=" + UPDATED_COMPILATION_TOKEN);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCompilationRequestShouldBeFound(String filter) throws Exception {
        restCompilationRequestMockMvc.perform(get("/api/compilation-requests?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(compilationRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].fileUploadId").value(hasItem(DEFAULT_FILE_UPLOAD_ID.intValue())))
            .andExpect(jsonPath("$.[*].compilationStatus").value(hasItem(DEFAULT_COMPILATION_STATUS.toString())))
            .andExpect(jsonPath("$.[*].compilationType").value(hasItem(DEFAULT_COMPILATION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].compilationToken").value(hasItem(DEFAULT_COMPILATION_TOKEN)));

        // Check, that the count call also returns 1
        restCompilationRequestMockMvc.perform(get("/api/compilation-requests/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCompilationRequestShouldNotBeFound(String filter) throws Exception {
        restCompilationRequestMockMvc.perform(get("/api/compilation-requests?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCompilationRequestMockMvc.perform(get("/api/compilation-requests/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingCompilationRequest() throws Exception {
        // Get the compilationRequest
        restCompilationRequestMockMvc.perform(get("/api/compilation-requests/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCompilationRequest() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        int databaseSizeBeforeUpdate = compilationRequestRepository.findAll().size();

        // Update the compilationRequest
        CompilationRequest updatedCompilationRequest = compilationRequestRepository.findById(compilationRequest.getId()).get();
        // Disconnect from session so that the updates on updatedCompilationRequest are not directly saved in db
        em.detach(updatedCompilationRequest);
        updatedCompilationRequest
            .description(UPDATED_DESCRIPTION)
            .fileUploadId(UPDATED_FILE_UPLOAD_ID)
            .compilationStatus(UPDATED_COMPILATION_STATUS)
            .compilationType(UPDATED_COMPILATION_TYPE)
            .compilationToken(UPDATED_COMPILATION_TOKEN);
        CompilationRequestDTO compilationRequestDTO = compilationRequestMapper.toDto(updatedCompilationRequest);

        restCompilationRequestMockMvc.perform(put("/api/compilation-requests")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(compilationRequestDTO)))
            .andExpect(status().isOk());

        // Validate the CompilationRequest in the database
        List<CompilationRequest> compilationRequestList = compilationRequestRepository.findAll();
        assertThat(compilationRequestList).hasSize(databaseSizeBeforeUpdate);
        CompilationRequest testCompilationRequest = compilationRequestList.get(compilationRequestList.size() - 1);
        assertThat(testCompilationRequest.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCompilationRequest.getFileUploadId()).isEqualTo(UPDATED_FILE_UPLOAD_ID);
        assertThat(testCompilationRequest.getCompilationStatus()).isEqualTo(UPDATED_COMPILATION_STATUS);
        assertThat(testCompilationRequest.getCompilationType()).isEqualTo(UPDATED_COMPILATION_TYPE);
        assertThat(testCompilationRequest.getCompilationToken()).isEqualTo(UPDATED_COMPILATION_TOKEN);

        // Validate the CompilationRequest in Elasticsearch
        verify(mockCompilationRequestSearchRepository, times(1)).save(testCompilationRequest);
    }

    @Test
    @Transactional
    public void updateNonExistingCompilationRequest() throws Exception {
        int databaseSizeBeforeUpdate = compilationRequestRepository.findAll().size();

        // Create the CompilationRequest
        CompilationRequestDTO compilationRequestDTO = compilationRequestMapper.toDto(compilationRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompilationRequestMockMvc.perform(put("/api/compilation-requests")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(compilationRequestDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CompilationRequest in the database
        List<CompilationRequest> compilationRequestList = compilationRequestRepository.findAll();
        assertThat(compilationRequestList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CompilationRequest in Elasticsearch
        verify(mockCompilationRequestSearchRepository, times(0)).save(compilationRequest);
    }

    @Test
    @Transactional
    public void deleteCompilationRequest() throws Exception {
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);

        int databaseSizeBeforeDelete = compilationRequestRepository.findAll().size();

        // Delete the compilationRequest
        restCompilationRequestMockMvc.perform(delete("/api/compilation-requests/{id}", compilationRequest.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CompilationRequest> compilationRequestList = compilationRequestRepository.findAll();
        assertThat(compilationRequestList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the CompilationRequest in Elasticsearch
        verify(mockCompilationRequestSearchRepository, times(1)).deleteById(compilationRequest.getId());
    }

    @Test
    @Transactional
    public void searchCompilationRequest() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        compilationRequestRepository.saveAndFlush(compilationRequest);
        when(mockCompilationRequestSearchRepository.search(queryStringQuery("id:" + compilationRequest.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(compilationRequest), PageRequest.of(0, 1), 1));

        // Search the compilationRequest
        restCompilationRequestMockMvc.perform(get("/api/_search/compilation-requests?query=id:" + compilationRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(compilationRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].fileUploadId").value(hasItem(DEFAULT_FILE_UPLOAD_ID.intValue())))
            .andExpect(jsonPath("$.[*].compilationStatus").value(hasItem(DEFAULT_COMPILATION_STATUS.toString())))
            .andExpect(jsonPath("$.[*].compilationType").value(hasItem(DEFAULT_COMPILATION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].compilationToken").value(hasItem(DEFAULT_COMPILATION_TOKEN)));
    }
}
