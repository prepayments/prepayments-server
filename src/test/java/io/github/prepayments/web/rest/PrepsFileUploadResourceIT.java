package io.github.prepayments.web.rest;

import io.github.prepayments.PrepaymentsApp;
import io.github.prepayments.domain.PrepsFileUpload;
import io.github.prepayments.repository.PrepsFileUploadRepository;
import io.github.prepayments.repository.search.PrepsFileUploadSearchRepository;
import io.github.prepayments.service.PrepsFileUploadService;
import io.github.prepayments.service.dto.PrepsFileUploadDTO;
import io.github.prepayments.service.mapper.PrepsFileUploadMapper;
import io.github.prepayments.service.dto.PrepsFileUploadCriteria;
import io.github.prepayments.service.PrepsFileUploadQueryService;

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
import org.springframework.util.Base64Utils;
import javax.persistence.EntityManager;
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
 * Integration tests for the {@link PrepsFileUploadResource} REST controller.
 */
@SpringBootTest(classes = PrepaymentsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class PrepsFileUploadResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FILE_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_PERIOD_FROM = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PERIOD_FROM = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_PERIOD_FROM = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_PERIOD_TO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PERIOD_TO = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_PERIOD_TO = LocalDate.ofEpochDay(-1L);

    private static final Long DEFAULT_PREPS_FILE_TYPE_ID = 1L;
    private static final Long UPDATED_PREPS_FILE_TYPE_ID = 2L;
    private static final Long SMALLER_PREPS_FILE_TYPE_ID = 1L - 1L;

    private static final byte[] DEFAULT_DATA_FILE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_DATA_FILE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_DATA_FILE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_DATA_FILE_CONTENT_TYPE = "image/png";

    private static final Boolean DEFAULT_UPLOAD_SUCCESSFUL = false;
    private static final Boolean UPDATED_UPLOAD_SUCCESSFUL = true;

    private static final Boolean DEFAULT_UPLOAD_PROCESSED = false;
    private static final Boolean UPDATED_UPLOAD_PROCESSED = true;

    private static final String DEFAULT_UPLOAD_TOKEN = "AAAAAAAAAA";
    private static final String UPDATED_UPLOAD_TOKEN = "BBBBBBBBBB";

    @Autowired
    private PrepsFileUploadRepository prepsFileUploadRepository;

    @Autowired
    private PrepsFileUploadMapper prepsFileUploadMapper;

    @Autowired
    private PrepsFileUploadService prepsFileUploadService;

    /**
     * This repository is mocked in the io.github.prepayments.repository.search test package.
     *
     * @see io.github.prepayments.repository.search.PrepsFileUploadSearchRepositoryMockConfiguration
     */
    @Autowired
    private PrepsFileUploadSearchRepository mockPrepsFileUploadSearchRepository;

    @Autowired
    private PrepsFileUploadQueryService prepsFileUploadQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPrepsFileUploadMockMvc;

    private PrepsFileUpload prepsFileUpload;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PrepsFileUpload createEntity(EntityManager em) {
        PrepsFileUpload prepsFileUpload = new PrepsFileUpload()
            .description(DEFAULT_DESCRIPTION)
            .fileName(DEFAULT_FILE_NAME)
            .periodFrom(DEFAULT_PERIOD_FROM)
            .periodTo(DEFAULT_PERIOD_TO)
            .prepsFileTypeId(DEFAULT_PREPS_FILE_TYPE_ID)
            .dataFile(DEFAULT_DATA_FILE)
            .dataFileContentType(DEFAULT_DATA_FILE_CONTENT_TYPE)
            .uploadSuccessful(DEFAULT_UPLOAD_SUCCESSFUL)
            .uploadProcessed(DEFAULT_UPLOAD_PROCESSED)
            .uploadToken(DEFAULT_UPLOAD_TOKEN);
        return prepsFileUpload;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PrepsFileUpload createUpdatedEntity(EntityManager em) {
        PrepsFileUpload prepsFileUpload = new PrepsFileUpload()
            .description(UPDATED_DESCRIPTION)
            .fileName(UPDATED_FILE_NAME)
            .periodFrom(UPDATED_PERIOD_FROM)
            .periodTo(UPDATED_PERIOD_TO)
            .prepsFileTypeId(UPDATED_PREPS_FILE_TYPE_ID)
            .dataFile(UPDATED_DATA_FILE)
            .dataFileContentType(UPDATED_DATA_FILE_CONTENT_TYPE)
            .uploadSuccessful(UPDATED_UPLOAD_SUCCESSFUL)
            .uploadProcessed(UPDATED_UPLOAD_PROCESSED)
            .uploadToken(UPDATED_UPLOAD_TOKEN);
        return prepsFileUpload;
    }

    @BeforeEach
    public void initTest() {
        prepsFileUpload = createEntity(em);
    }

    @Test
    @Transactional
    public void createPrepsFileUpload() throws Exception {
        int databaseSizeBeforeCreate = prepsFileUploadRepository.findAll().size();
        // Create the PrepsFileUpload
        PrepsFileUploadDTO prepsFileUploadDTO = prepsFileUploadMapper.toDto(prepsFileUpload);
        restPrepsFileUploadMockMvc.perform(post("/api/preps-file-uploads")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepsFileUploadDTO)))
            .andExpect(status().isCreated());

        // Validate the PrepsFileUpload in the database
        List<PrepsFileUpload> prepsFileUploadList = prepsFileUploadRepository.findAll();
        assertThat(prepsFileUploadList).hasSize(databaseSizeBeforeCreate + 1);
        PrepsFileUpload testPrepsFileUpload = prepsFileUploadList.get(prepsFileUploadList.size() - 1);
        assertThat(testPrepsFileUpload.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPrepsFileUpload.getFileName()).isEqualTo(DEFAULT_FILE_NAME);
        assertThat(testPrepsFileUpload.getPeriodFrom()).isEqualTo(DEFAULT_PERIOD_FROM);
        assertThat(testPrepsFileUpload.getPeriodTo()).isEqualTo(DEFAULT_PERIOD_TO);
        assertThat(testPrepsFileUpload.getPrepsFileTypeId()).isEqualTo(DEFAULT_PREPS_FILE_TYPE_ID);
        assertThat(testPrepsFileUpload.getDataFile()).isEqualTo(DEFAULT_DATA_FILE);
        assertThat(testPrepsFileUpload.getDataFileContentType()).isEqualTo(DEFAULT_DATA_FILE_CONTENT_TYPE);
        assertThat(testPrepsFileUpload.isUploadSuccessful()).isEqualTo(DEFAULT_UPLOAD_SUCCESSFUL);
        assertThat(testPrepsFileUpload.isUploadProcessed()).isEqualTo(DEFAULT_UPLOAD_PROCESSED);
        assertThat(testPrepsFileUpload.getUploadToken()).isEqualTo(DEFAULT_UPLOAD_TOKEN);

        // Validate the PrepsFileUpload in Elasticsearch
        verify(mockPrepsFileUploadSearchRepository, times(1)).save(testPrepsFileUpload);
    }

    @Test
    @Transactional
    public void createPrepsFileUploadWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = prepsFileUploadRepository.findAll().size();

        // Create the PrepsFileUpload with an existing ID
        prepsFileUpload.setId(1L);
        PrepsFileUploadDTO prepsFileUploadDTO = prepsFileUploadMapper.toDto(prepsFileUpload);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPrepsFileUploadMockMvc.perform(post("/api/preps-file-uploads")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepsFileUploadDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PrepsFileUpload in the database
        List<PrepsFileUpload> prepsFileUploadList = prepsFileUploadRepository.findAll();
        assertThat(prepsFileUploadList).hasSize(databaseSizeBeforeCreate);

        // Validate the PrepsFileUpload in Elasticsearch
        verify(mockPrepsFileUploadSearchRepository, times(0)).save(prepsFileUpload);
    }


    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = prepsFileUploadRepository.findAll().size();
        // set the field null
        prepsFileUpload.setDescription(null);

        // Create the PrepsFileUpload, which fails.
        PrepsFileUploadDTO prepsFileUploadDTO = prepsFileUploadMapper.toDto(prepsFileUpload);


        restPrepsFileUploadMockMvc.perform(post("/api/preps-file-uploads")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepsFileUploadDTO)))
            .andExpect(status().isBadRequest());

        List<PrepsFileUpload> prepsFileUploadList = prepsFileUploadRepository.findAll();
        assertThat(prepsFileUploadList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkFileNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = prepsFileUploadRepository.findAll().size();
        // set the field null
        prepsFileUpload.setFileName(null);

        // Create the PrepsFileUpload, which fails.
        PrepsFileUploadDTO prepsFileUploadDTO = prepsFileUploadMapper.toDto(prepsFileUpload);


        restPrepsFileUploadMockMvc.perform(post("/api/preps-file-uploads")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepsFileUploadDTO)))
            .andExpect(status().isBadRequest());

        List<PrepsFileUpload> prepsFileUploadList = prepsFileUploadRepository.findAll();
        assertThat(prepsFileUploadList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPrepsFileTypeIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = prepsFileUploadRepository.findAll().size();
        // set the field null
        prepsFileUpload.setPrepsFileTypeId(null);

        // Create the PrepsFileUpload, which fails.
        PrepsFileUploadDTO prepsFileUploadDTO = prepsFileUploadMapper.toDto(prepsFileUpload);


        restPrepsFileUploadMockMvc.perform(post("/api/preps-file-uploads")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepsFileUploadDTO)))
            .andExpect(status().isBadRequest());

        List<PrepsFileUpload> prepsFileUploadList = prepsFileUploadRepository.findAll();
        assertThat(prepsFileUploadList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploads() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList
        restPrepsFileUploadMockMvc.perform(get("/api/preps-file-uploads?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prepsFileUpload.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].periodFrom").value(hasItem(DEFAULT_PERIOD_FROM.toString())))
            .andExpect(jsonPath("$.[*].periodTo").value(hasItem(DEFAULT_PERIOD_TO.toString())))
            .andExpect(jsonPath("$.[*].prepsFileTypeId").value(hasItem(DEFAULT_PREPS_FILE_TYPE_ID.intValue())))
            .andExpect(jsonPath("$.[*].dataFileContentType").value(hasItem(DEFAULT_DATA_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].dataFile").value(hasItem(Base64Utils.encodeToString(DEFAULT_DATA_FILE))))
            .andExpect(jsonPath("$.[*].uploadSuccessful").value(hasItem(DEFAULT_UPLOAD_SUCCESSFUL.booleanValue())))
            .andExpect(jsonPath("$.[*].uploadProcessed").value(hasItem(DEFAULT_UPLOAD_PROCESSED.booleanValue())))
            .andExpect(jsonPath("$.[*].uploadToken").value(hasItem(DEFAULT_UPLOAD_TOKEN)));
    }
    
    @Test
    @Transactional
    public void getPrepsFileUpload() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get the prepsFileUpload
        restPrepsFileUploadMockMvc.perform(get("/api/preps-file-uploads/{id}", prepsFileUpload.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(prepsFileUpload.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.fileName").value(DEFAULT_FILE_NAME))
            .andExpect(jsonPath("$.periodFrom").value(DEFAULT_PERIOD_FROM.toString()))
            .andExpect(jsonPath("$.periodTo").value(DEFAULT_PERIOD_TO.toString()))
            .andExpect(jsonPath("$.prepsFileTypeId").value(DEFAULT_PREPS_FILE_TYPE_ID.intValue()))
            .andExpect(jsonPath("$.dataFileContentType").value(DEFAULT_DATA_FILE_CONTENT_TYPE))
            .andExpect(jsonPath("$.dataFile").value(Base64Utils.encodeToString(DEFAULT_DATA_FILE)))
            .andExpect(jsonPath("$.uploadSuccessful").value(DEFAULT_UPLOAD_SUCCESSFUL.booleanValue()))
            .andExpect(jsonPath("$.uploadProcessed").value(DEFAULT_UPLOAD_PROCESSED.booleanValue()))
            .andExpect(jsonPath("$.uploadToken").value(DEFAULT_UPLOAD_TOKEN));
    }


    @Test
    @Transactional
    public void getPrepsFileUploadsByIdFiltering() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        Long id = prepsFileUpload.getId();

        defaultPrepsFileUploadShouldBeFound("id.equals=" + id);
        defaultPrepsFileUploadShouldNotBeFound("id.notEquals=" + id);

        defaultPrepsFileUploadShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPrepsFileUploadShouldNotBeFound("id.greaterThan=" + id);

        defaultPrepsFileUploadShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPrepsFileUploadShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllPrepsFileUploadsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where description equals to DEFAULT_DESCRIPTION
        defaultPrepsFileUploadShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the prepsFileUploadList where description equals to UPDATED_DESCRIPTION
        defaultPrepsFileUploadShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where description not equals to DEFAULT_DESCRIPTION
        defaultPrepsFileUploadShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the prepsFileUploadList where description not equals to UPDATED_DESCRIPTION
        defaultPrepsFileUploadShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultPrepsFileUploadShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the prepsFileUploadList where description equals to UPDATED_DESCRIPTION
        defaultPrepsFileUploadShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where description is not null
        defaultPrepsFileUploadShouldBeFound("description.specified=true");

        // Get all the prepsFileUploadList where description is null
        defaultPrepsFileUploadShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllPrepsFileUploadsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where description contains DEFAULT_DESCRIPTION
        defaultPrepsFileUploadShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the prepsFileUploadList where description contains UPDATED_DESCRIPTION
        defaultPrepsFileUploadShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where description does not contain DEFAULT_DESCRIPTION
        defaultPrepsFileUploadShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the prepsFileUploadList where description does not contain UPDATED_DESCRIPTION
        defaultPrepsFileUploadShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllPrepsFileUploadsByFileNameIsEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where fileName equals to DEFAULT_FILE_NAME
        defaultPrepsFileUploadShouldBeFound("fileName.equals=" + DEFAULT_FILE_NAME);

        // Get all the prepsFileUploadList where fileName equals to UPDATED_FILE_NAME
        defaultPrepsFileUploadShouldNotBeFound("fileName.equals=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByFileNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where fileName not equals to DEFAULT_FILE_NAME
        defaultPrepsFileUploadShouldNotBeFound("fileName.notEquals=" + DEFAULT_FILE_NAME);

        // Get all the prepsFileUploadList where fileName not equals to UPDATED_FILE_NAME
        defaultPrepsFileUploadShouldBeFound("fileName.notEquals=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByFileNameIsInShouldWork() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where fileName in DEFAULT_FILE_NAME or UPDATED_FILE_NAME
        defaultPrepsFileUploadShouldBeFound("fileName.in=" + DEFAULT_FILE_NAME + "," + UPDATED_FILE_NAME);

        // Get all the prepsFileUploadList where fileName equals to UPDATED_FILE_NAME
        defaultPrepsFileUploadShouldNotBeFound("fileName.in=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByFileNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where fileName is not null
        defaultPrepsFileUploadShouldBeFound("fileName.specified=true");

        // Get all the prepsFileUploadList where fileName is null
        defaultPrepsFileUploadShouldNotBeFound("fileName.specified=false");
    }
                @Test
    @Transactional
    public void getAllPrepsFileUploadsByFileNameContainsSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where fileName contains DEFAULT_FILE_NAME
        defaultPrepsFileUploadShouldBeFound("fileName.contains=" + DEFAULT_FILE_NAME);

        // Get all the prepsFileUploadList where fileName contains UPDATED_FILE_NAME
        defaultPrepsFileUploadShouldNotBeFound("fileName.contains=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByFileNameNotContainsSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where fileName does not contain DEFAULT_FILE_NAME
        defaultPrepsFileUploadShouldNotBeFound("fileName.doesNotContain=" + DEFAULT_FILE_NAME);

        // Get all the prepsFileUploadList where fileName does not contain UPDATED_FILE_NAME
        defaultPrepsFileUploadShouldBeFound("fileName.doesNotContain=" + UPDATED_FILE_NAME);
    }


    @Test
    @Transactional
    public void getAllPrepsFileUploadsByPeriodFromIsEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where periodFrom equals to DEFAULT_PERIOD_FROM
        defaultPrepsFileUploadShouldBeFound("periodFrom.equals=" + DEFAULT_PERIOD_FROM);

        // Get all the prepsFileUploadList where periodFrom equals to UPDATED_PERIOD_FROM
        defaultPrepsFileUploadShouldNotBeFound("periodFrom.equals=" + UPDATED_PERIOD_FROM);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByPeriodFromIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where periodFrom not equals to DEFAULT_PERIOD_FROM
        defaultPrepsFileUploadShouldNotBeFound("periodFrom.notEquals=" + DEFAULT_PERIOD_FROM);

        // Get all the prepsFileUploadList where periodFrom not equals to UPDATED_PERIOD_FROM
        defaultPrepsFileUploadShouldBeFound("periodFrom.notEquals=" + UPDATED_PERIOD_FROM);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByPeriodFromIsInShouldWork() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where periodFrom in DEFAULT_PERIOD_FROM or UPDATED_PERIOD_FROM
        defaultPrepsFileUploadShouldBeFound("periodFrom.in=" + DEFAULT_PERIOD_FROM + "," + UPDATED_PERIOD_FROM);

        // Get all the prepsFileUploadList where periodFrom equals to UPDATED_PERIOD_FROM
        defaultPrepsFileUploadShouldNotBeFound("periodFrom.in=" + UPDATED_PERIOD_FROM);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByPeriodFromIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where periodFrom is not null
        defaultPrepsFileUploadShouldBeFound("periodFrom.specified=true");

        // Get all the prepsFileUploadList where periodFrom is null
        defaultPrepsFileUploadShouldNotBeFound("periodFrom.specified=false");
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByPeriodFromIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where periodFrom is greater than or equal to DEFAULT_PERIOD_FROM
        defaultPrepsFileUploadShouldBeFound("periodFrom.greaterThanOrEqual=" + DEFAULT_PERIOD_FROM);

        // Get all the prepsFileUploadList where periodFrom is greater than or equal to UPDATED_PERIOD_FROM
        defaultPrepsFileUploadShouldNotBeFound("periodFrom.greaterThanOrEqual=" + UPDATED_PERIOD_FROM);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByPeriodFromIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where periodFrom is less than or equal to DEFAULT_PERIOD_FROM
        defaultPrepsFileUploadShouldBeFound("periodFrom.lessThanOrEqual=" + DEFAULT_PERIOD_FROM);

        // Get all the prepsFileUploadList where periodFrom is less than or equal to SMALLER_PERIOD_FROM
        defaultPrepsFileUploadShouldNotBeFound("periodFrom.lessThanOrEqual=" + SMALLER_PERIOD_FROM);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByPeriodFromIsLessThanSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where periodFrom is less than DEFAULT_PERIOD_FROM
        defaultPrepsFileUploadShouldNotBeFound("periodFrom.lessThan=" + DEFAULT_PERIOD_FROM);

        // Get all the prepsFileUploadList where periodFrom is less than UPDATED_PERIOD_FROM
        defaultPrepsFileUploadShouldBeFound("periodFrom.lessThan=" + UPDATED_PERIOD_FROM);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByPeriodFromIsGreaterThanSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where periodFrom is greater than DEFAULT_PERIOD_FROM
        defaultPrepsFileUploadShouldNotBeFound("periodFrom.greaterThan=" + DEFAULT_PERIOD_FROM);

        // Get all the prepsFileUploadList where periodFrom is greater than SMALLER_PERIOD_FROM
        defaultPrepsFileUploadShouldBeFound("periodFrom.greaterThan=" + SMALLER_PERIOD_FROM);
    }


    @Test
    @Transactional
    public void getAllPrepsFileUploadsByPeriodToIsEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where periodTo equals to DEFAULT_PERIOD_TO
        defaultPrepsFileUploadShouldBeFound("periodTo.equals=" + DEFAULT_PERIOD_TO);

        // Get all the prepsFileUploadList where periodTo equals to UPDATED_PERIOD_TO
        defaultPrepsFileUploadShouldNotBeFound("periodTo.equals=" + UPDATED_PERIOD_TO);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByPeriodToIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where periodTo not equals to DEFAULT_PERIOD_TO
        defaultPrepsFileUploadShouldNotBeFound("periodTo.notEquals=" + DEFAULT_PERIOD_TO);

        // Get all the prepsFileUploadList where periodTo not equals to UPDATED_PERIOD_TO
        defaultPrepsFileUploadShouldBeFound("periodTo.notEquals=" + UPDATED_PERIOD_TO);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByPeriodToIsInShouldWork() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where periodTo in DEFAULT_PERIOD_TO or UPDATED_PERIOD_TO
        defaultPrepsFileUploadShouldBeFound("periodTo.in=" + DEFAULT_PERIOD_TO + "," + UPDATED_PERIOD_TO);

        // Get all the prepsFileUploadList where periodTo equals to UPDATED_PERIOD_TO
        defaultPrepsFileUploadShouldNotBeFound("periodTo.in=" + UPDATED_PERIOD_TO);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByPeriodToIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where periodTo is not null
        defaultPrepsFileUploadShouldBeFound("periodTo.specified=true");

        // Get all the prepsFileUploadList where periodTo is null
        defaultPrepsFileUploadShouldNotBeFound("periodTo.specified=false");
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByPeriodToIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where periodTo is greater than or equal to DEFAULT_PERIOD_TO
        defaultPrepsFileUploadShouldBeFound("periodTo.greaterThanOrEqual=" + DEFAULT_PERIOD_TO);

        // Get all the prepsFileUploadList where periodTo is greater than or equal to UPDATED_PERIOD_TO
        defaultPrepsFileUploadShouldNotBeFound("periodTo.greaterThanOrEqual=" + UPDATED_PERIOD_TO);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByPeriodToIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where periodTo is less than or equal to DEFAULT_PERIOD_TO
        defaultPrepsFileUploadShouldBeFound("periodTo.lessThanOrEqual=" + DEFAULT_PERIOD_TO);

        // Get all the prepsFileUploadList where periodTo is less than or equal to SMALLER_PERIOD_TO
        defaultPrepsFileUploadShouldNotBeFound("periodTo.lessThanOrEqual=" + SMALLER_PERIOD_TO);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByPeriodToIsLessThanSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where periodTo is less than DEFAULT_PERIOD_TO
        defaultPrepsFileUploadShouldNotBeFound("periodTo.lessThan=" + DEFAULT_PERIOD_TO);

        // Get all the prepsFileUploadList where periodTo is less than UPDATED_PERIOD_TO
        defaultPrepsFileUploadShouldBeFound("periodTo.lessThan=" + UPDATED_PERIOD_TO);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByPeriodToIsGreaterThanSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where periodTo is greater than DEFAULT_PERIOD_TO
        defaultPrepsFileUploadShouldNotBeFound("periodTo.greaterThan=" + DEFAULT_PERIOD_TO);

        // Get all the prepsFileUploadList where periodTo is greater than SMALLER_PERIOD_TO
        defaultPrepsFileUploadShouldBeFound("periodTo.greaterThan=" + SMALLER_PERIOD_TO);
    }


    @Test
    @Transactional
    public void getAllPrepsFileUploadsByPrepsFileTypeIdIsEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where prepsFileTypeId equals to DEFAULT_PREPS_FILE_TYPE_ID
        defaultPrepsFileUploadShouldBeFound("prepsFileTypeId.equals=" + DEFAULT_PREPS_FILE_TYPE_ID);

        // Get all the prepsFileUploadList where prepsFileTypeId equals to UPDATED_PREPS_FILE_TYPE_ID
        defaultPrepsFileUploadShouldNotBeFound("prepsFileTypeId.equals=" + UPDATED_PREPS_FILE_TYPE_ID);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByPrepsFileTypeIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where prepsFileTypeId not equals to DEFAULT_PREPS_FILE_TYPE_ID
        defaultPrepsFileUploadShouldNotBeFound("prepsFileTypeId.notEquals=" + DEFAULT_PREPS_FILE_TYPE_ID);

        // Get all the prepsFileUploadList where prepsFileTypeId not equals to UPDATED_PREPS_FILE_TYPE_ID
        defaultPrepsFileUploadShouldBeFound("prepsFileTypeId.notEquals=" + UPDATED_PREPS_FILE_TYPE_ID);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByPrepsFileTypeIdIsInShouldWork() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where prepsFileTypeId in DEFAULT_PREPS_FILE_TYPE_ID or UPDATED_PREPS_FILE_TYPE_ID
        defaultPrepsFileUploadShouldBeFound("prepsFileTypeId.in=" + DEFAULT_PREPS_FILE_TYPE_ID + "," + UPDATED_PREPS_FILE_TYPE_ID);

        // Get all the prepsFileUploadList where prepsFileTypeId equals to UPDATED_PREPS_FILE_TYPE_ID
        defaultPrepsFileUploadShouldNotBeFound("prepsFileTypeId.in=" + UPDATED_PREPS_FILE_TYPE_ID);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByPrepsFileTypeIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where prepsFileTypeId is not null
        defaultPrepsFileUploadShouldBeFound("prepsFileTypeId.specified=true");

        // Get all the prepsFileUploadList where prepsFileTypeId is null
        defaultPrepsFileUploadShouldNotBeFound("prepsFileTypeId.specified=false");
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByPrepsFileTypeIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where prepsFileTypeId is greater than or equal to DEFAULT_PREPS_FILE_TYPE_ID
        defaultPrepsFileUploadShouldBeFound("prepsFileTypeId.greaterThanOrEqual=" + DEFAULT_PREPS_FILE_TYPE_ID);

        // Get all the prepsFileUploadList where prepsFileTypeId is greater than or equal to UPDATED_PREPS_FILE_TYPE_ID
        defaultPrepsFileUploadShouldNotBeFound("prepsFileTypeId.greaterThanOrEqual=" + UPDATED_PREPS_FILE_TYPE_ID);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByPrepsFileTypeIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where prepsFileTypeId is less than or equal to DEFAULT_PREPS_FILE_TYPE_ID
        defaultPrepsFileUploadShouldBeFound("prepsFileTypeId.lessThanOrEqual=" + DEFAULT_PREPS_FILE_TYPE_ID);

        // Get all the prepsFileUploadList where prepsFileTypeId is less than or equal to SMALLER_PREPS_FILE_TYPE_ID
        defaultPrepsFileUploadShouldNotBeFound("prepsFileTypeId.lessThanOrEqual=" + SMALLER_PREPS_FILE_TYPE_ID);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByPrepsFileTypeIdIsLessThanSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where prepsFileTypeId is less than DEFAULT_PREPS_FILE_TYPE_ID
        defaultPrepsFileUploadShouldNotBeFound("prepsFileTypeId.lessThan=" + DEFAULT_PREPS_FILE_TYPE_ID);

        // Get all the prepsFileUploadList where prepsFileTypeId is less than UPDATED_PREPS_FILE_TYPE_ID
        defaultPrepsFileUploadShouldBeFound("prepsFileTypeId.lessThan=" + UPDATED_PREPS_FILE_TYPE_ID);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByPrepsFileTypeIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where prepsFileTypeId is greater than DEFAULT_PREPS_FILE_TYPE_ID
        defaultPrepsFileUploadShouldNotBeFound("prepsFileTypeId.greaterThan=" + DEFAULT_PREPS_FILE_TYPE_ID);

        // Get all the prepsFileUploadList where prepsFileTypeId is greater than SMALLER_PREPS_FILE_TYPE_ID
        defaultPrepsFileUploadShouldBeFound("prepsFileTypeId.greaterThan=" + SMALLER_PREPS_FILE_TYPE_ID);
    }


    @Test
    @Transactional
    public void getAllPrepsFileUploadsByUploadSuccessfulIsEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where uploadSuccessful equals to DEFAULT_UPLOAD_SUCCESSFUL
        defaultPrepsFileUploadShouldBeFound("uploadSuccessful.equals=" + DEFAULT_UPLOAD_SUCCESSFUL);

        // Get all the prepsFileUploadList where uploadSuccessful equals to UPDATED_UPLOAD_SUCCESSFUL
        defaultPrepsFileUploadShouldNotBeFound("uploadSuccessful.equals=" + UPDATED_UPLOAD_SUCCESSFUL);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByUploadSuccessfulIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where uploadSuccessful not equals to DEFAULT_UPLOAD_SUCCESSFUL
        defaultPrepsFileUploadShouldNotBeFound("uploadSuccessful.notEquals=" + DEFAULT_UPLOAD_SUCCESSFUL);

        // Get all the prepsFileUploadList where uploadSuccessful not equals to UPDATED_UPLOAD_SUCCESSFUL
        defaultPrepsFileUploadShouldBeFound("uploadSuccessful.notEquals=" + UPDATED_UPLOAD_SUCCESSFUL);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByUploadSuccessfulIsInShouldWork() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where uploadSuccessful in DEFAULT_UPLOAD_SUCCESSFUL or UPDATED_UPLOAD_SUCCESSFUL
        defaultPrepsFileUploadShouldBeFound("uploadSuccessful.in=" + DEFAULT_UPLOAD_SUCCESSFUL + "," + UPDATED_UPLOAD_SUCCESSFUL);

        // Get all the prepsFileUploadList where uploadSuccessful equals to UPDATED_UPLOAD_SUCCESSFUL
        defaultPrepsFileUploadShouldNotBeFound("uploadSuccessful.in=" + UPDATED_UPLOAD_SUCCESSFUL);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByUploadSuccessfulIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where uploadSuccessful is not null
        defaultPrepsFileUploadShouldBeFound("uploadSuccessful.specified=true");

        // Get all the prepsFileUploadList where uploadSuccessful is null
        defaultPrepsFileUploadShouldNotBeFound("uploadSuccessful.specified=false");
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByUploadProcessedIsEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where uploadProcessed equals to DEFAULT_UPLOAD_PROCESSED
        defaultPrepsFileUploadShouldBeFound("uploadProcessed.equals=" + DEFAULT_UPLOAD_PROCESSED);

        // Get all the prepsFileUploadList where uploadProcessed equals to UPDATED_UPLOAD_PROCESSED
        defaultPrepsFileUploadShouldNotBeFound("uploadProcessed.equals=" + UPDATED_UPLOAD_PROCESSED);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByUploadProcessedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where uploadProcessed not equals to DEFAULT_UPLOAD_PROCESSED
        defaultPrepsFileUploadShouldNotBeFound("uploadProcessed.notEquals=" + DEFAULT_UPLOAD_PROCESSED);

        // Get all the prepsFileUploadList where uploadProcessed not equals to UPDATED_UPLOAD_PROCESSED
        defaultPrepsFileUploadShouldBeFound("uploadProcessed.notEquals=" + UPDATED_UPLOAD_PROCESSED);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByUploadProcessedIsInShouldWork() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where uploadProcessed in DEFAULT_UPLOAD_PROCESSED or UPDATED_UPLOAD_PROCESSED
        defaultPrepsFileUploadShouldBeFound("uploadProcessed.in=" + DEFAULT_UPLOAD_PROCESSED + "," + UPDATED_UPLOAD_PROCESSED);

        // Get all the prepsFileUploadList where uploadProcessed equals to UPDATED_UPLOAD_PROCESSED
        defaultPrepsFileUploadShouldNotBeFound("uploadProcessed.in=" + UPDATED_UPLOAD_PROCESSED);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByUploadProcessedIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where uploadProcessed is not null
        defaultPrepsFileUploadShouldBeFound("uploadProcessed.specified=true");

        // Get all the prepsFileUploadList where uploadProcessed is null
        defaultPrepsFileUploadShouldNotBeFound("uploadProcessed.specified=false");
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByUploadTokenIsEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where uploadToken equals to DEFAULT_UPLOAD_TOKEN
        defaultPrepsFileUploadShouldBeFound("uploadToken.equals=" + DEFAULT_UPLOAD_TOKEN);

        // Get all the prepsFileUploadList where uploadToken equals to UPDATED_UPLOAD_TOKEN
        defaultPrepsFileUploadShouldNotBeFound("uploadToken.equals=" + UPDATED_UPLOAD_TOKEN);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByUploadTokenIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where uploadToken not equals to DEFAULT_UPLOAD_TOKEN
        defaultPrepsFileUploadShouldNotBeFound("uploadToken.notEquals=" + DEFAULT_UPLOAD_TOKEN);

        // Get all the prepsFileUploadList where uploadToken not equals to UPDATED_UPLOAD_TOKEN
        defaultPrepsFileUploadShouldBeFound("uploadToken.notEquals=" + UPDATED_UPLOAD_TOKEN);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByUploadTokenIsInShouldWork() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where uploadToken in DEFAULT_UPLOAD_TOKEN or UPDATED_UPLOAD_TOKEN
        defaultPrepsFileUploadShouldBeFound("uploadToken.in=" + DEFAULT_UPLOAD_TOKEN + "," + UPDATED_UPLOAD_TOKEN);

        // Get all the prepsFileUploadList where uploadToken equals to UPDATED_UPLOAD_TOKEN
        defaultPrepsFileUploadShouldNotBeFound("uploadToken.in=" + UPDATED_UPLOAD_TOKEN);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByUploadTokenIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where uploadToken is not null
        defaultPrepsFileUploadShouldBeFound("uploadToken.specified=true");

        // Get all the prepsFileUploadList where uploadToken is null
        defaultPrepsFileUploadShouldNotBeFound("uploadToken.specified=false");
    }
                @Test
    @Transactional
    public void getAllPrepsFileUploadsByUploadTokenContainsSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where uploadToken contains DEFAULT_UPLOAD_TOKEN
        defaultPrepsFileUploadShouldBeFound("uploadToken.contains=" + DEFAULT_UPLOAD_TOKEN);

        // Get all the prepsFileUploadList where uploadToken contains UPDATED_UPLOAD_TOKEN
        defaultPrepsFileUploadShouldNotBeFound("uploadToken.contains=" + UPDATED_UPLOAD_TOKEN);
    }

    @Test
    @Transactional
    public void getAllPrepsFileUploadsByUploadTokenNotContainsSomething() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        // Get all the prepsFileUploadList where uploadToken does not contain DEFAULT_UPLOAD_TOKEN
        defaultPrepsFileUploadShouldNotBeFound("uploadToken.doesNotContain=" + DEFAULT_UPLOAD_TOKEN);

        // Get all the prepsFileUploadList where uploadToken does not contain UPDATED_UPLOAD_TOKEN
        defaultPrepsFileUploadShouldBeFound("uploadToken.doesNotContain=" + UPDATED_UPLOAD_TOKEN);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPrepsFileUploadShouldBeFound(String filter) throws Exception {
        restPrepsFileUploadMockMvc.perform(get("/api/preps-file-uploads?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prepsFileUpload.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].periodFrom").value(hasItem(DEFAULT_PERIOD_FROM.toString())))
            .andExpect(jsonPath("$.[*].periodTo").value(hasItem(DEFAULT_PERIOD_TO.toString())))
            .andExpect(jsonPath("$.[*].prepsFileTypeId").value(hasItem(DEFAULT_PREPS_FILE_TYPE_ID.intValue())))
            .andExpect(jsonPath("$.[*].dataFileContentType").value(hasItem(DEFAULT_DATA_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].dataFile").value(hasItem(Base64Utils.encodeToString(DEFAULT_DATA_FILE))))
            .andExpect(jsonPath("$.[*].uploadSuccessful").value(hasItem(DEFAULT_UPLOAD_SUCCESSFUL.booleanValue())))
            .andExpect(jsonPath("$.[*].uploadProcessed").value(hasItem(DEFAULT_UPLOAD_PROCESSED.booleanValue())))
            .andExpect(jsonPath("$.[*].uploadToken").value(hasItem(DEFAULT_UPLOAD_TOKEN)));

        // Check, that the count call also returns 1
        restPrepsFileUploadMockMvc.perform(get("/api/preps-file-uploads/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPrepsFileUploadShouldNotBeFound(String filter) throws Exception {
        restPrepsFileUploadMockMvc.perform(get("/api/preps-file-uploads?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPrepsFileUploadMockMvc.perform(get("/api/preps-file-uploads/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingPrepsFileUpload() throws Exception {
        // Get the prepsFileUpload
        restPrepsFileUploadMockMvc.perform(get("/api/preps-file-uploads/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePrepsFileUpload() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        int databaseSizeBeforeUpdate = prepsFileUploadRepository.findAll().size();

        // Update the prepsFileUpload
        PrepsFileUpload updatedPrepsFileUpload = prepsFileUploadRepository.findById(prepsFileUpload.getId()).get();
        // Disconnect from session so that the updates on updatedPrepsFileUpload are not directly saved in db
        em.detach(updatedPrepsFileUpload);
        updatedPrepsFileUpload
            .description(UPDATED_DESCRIPTION)
            .fileName(UPDATED_FILE_NAME)
            .periodFrom(UPDATED_PERIOD_FROM)
            .periodTo(UPDATED_PERIOD_TO)
            .prepsFileTypeId(UPDATED_PREPS_FILE_TYPE_ID)
            .dataFile(UPDATED_DATA_FILE)
            .dataFileContentType(UPDATED_DATA_FILE_CONTENT_TYPE)
            .uploadSuccessful(UPDATED_UPLOAD_SUCCESSFUL)
            .uploadProcessed(UPDATED_UPLOAD_PROCESSED)
            .uploadToken(UPDATED_UPLOAD_TOKEN);
        PrepsFileUploadDTO prepsFileUploadDTO = prepsFileUploadMapper.toDto(updatedPrepsFileUpload);

        restPrepsFileUploadMockMvc.perform(put("/api/preps-file-uploads")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepsFileUploadDTO)))
            .andExpect(status().isOk());

        // Validate the PrepsFileUpload in the database
        List<PrepsFileUpload> prepsFileUploadList = prepsFileUploadRepository.findAll();
        assertThat(prepsFileUploadList).hasSize(databaseSizeBeforeUpdate);
        PrepsFileUpload testPrepsFileUpload = prepsFileUploadList.get(prepsFileUploadList.size() - 1);
        assertThat(testPrepsFileUpload.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPrepsFileUpload.getFileName()).isEqualTo(UPDATED_FILE_NAME);
        assertThat(testPrepsFileUpload.getPeriodFrom()).isEqualTo(UPDATED_PERIOD_FROM);
        assertThat(testPrepsFileUpload.getPeriodTo()).isEqualTo(UPDATED_PERIOD_TO);
        assertThat(testPrepsFileUpload.getPrepsFileTypeId()).isEqualTo(UPDATED_PREPS_FILE_TYPE_ID);
        assertThat(testPrepsFileUpload.getDataFile()).isEqualTo(UPDATED_DATA_FILE);
        assertThat(testPrepsFileUpload.getDataFileContentType()).isEqualTo(UPDATED_DATA_FILE_CONTENT_TYPE);
        assertThat(testPrepsFileUpload.isUploadSuccessful()).isEqualTo(UPDATED_UPLOAD_SUCCESSFUL);
        assertThat(testPrepsFileUpload.isUploadProcessed()).isEqualTo(UPDATED_UPLOAD_PROCESSED);
        assertThat(testPrepsFileUpload.getUploadToken()).isEqualTo(UPDATED_UPLOAD_TOKEN);

        // Validate the PrepsFileUpload in Elasticsearch
        verify(mockPrepsFileUploadSearchRepository, times(1)).save(testPrepsFileUpload);
    }

    @Test
    @Transactional
    public void updateNonExistingPrepsFileUpload() throws Exception {
        int databaseSizeBeforeUpdate = prepsFileUploadRepository.findAll().size();

        // Create the PrepsFileUpload
        PrepsFileUploadDTO prepsFileUploadDTO = prepsFileUploadMapper.toDto(prepsFileUpload);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPrepsFileUploadMockMvc.perform(put("/api/preps-file-uploads")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepsFileUploadDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PrepsFileUpload in the database
        List<PrepsFileUpload> prepsFileUploadList = prepsFileUploadRepository.findAll();
        assertThat(prepsFileUploadList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PrepsFileUpload in Elasticsearch
        verify(mockPrepsFileUploadSearchRepository, times(0)).save(prepsFileUpload);
    }

    @Test
    @Transactional
    public void deletePrepsFileUpload() throws Exception {
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);

        int databaseSizeBeforeDelete = prepsFileUploadRepository.findAll().size();

        // Delete the prepsFileUpload
        restPrepsFileUploadMockMvc.perform(delete("/api/preps-file-uploads/{id}", prepsFileUpload.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PrepsFileUpload> prepsFileUploadList = prepsFileUploadRepository.findAll();
        assertThat(prepsFileUploadList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the PrepsFileUpload in Elasticsearch
        verify(mockPrepsFileUploadSearchRepository, times(1)).deleteById(prepsFileUpload.getId());
    }

    @Test
    @Transactional
    public void searchPrepsFileUpload() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        prepsFileUploadRepository.saveAndFlush(prepsFileUpload);
        when(mockPrepsFileUploadSearchRepository.search(queryStringQuery("id:" + prepsFileUpload.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(prepsFileUpload), PageRequest.of(0, 1), 1));

        // Search the prepsFileUpload
        restPrepsFileUploadMockMvc.perform(get("/api/_search/preps-file-uploads?query=id:" + prepsFileUpload.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prepsFileUpload.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].periodFrom").value(hasItem(DEFAULT_PERIOD_FROM.toString())))
            .andExpect(jsonPath("$.[*].periodTo").value(hasItem(DEFAULT_PERIOD_TO.toString())))
            .andExpect(jsonPath("$.[*].prepsFileTypeId").value(hasItem(DEFAULT_PREPS_FILE_TYPE_ID.intValue())))
            .andExpect(jsonPath("$.[*].dataFileContentType").value(hasItem(DEFAULT_DATA_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].dataFile").value(hasItem(Base64Utils.encodeToString(DEFAULT_DATA_FILE))))
            .andExpect(jsonPath("$.[*].uploadSuccessful").value(hasItem(DEFAULT_UPLOAD_SUCCESSFUL.booleanValue())))
            .andExpect(jsonPath("$.[*].uploadProcessed").value(hasItem(DEFAULT_UPLOAD_PROCESSED.booleanValue())))
            .andExpect(jsonPath("$.[*].uploadToken").value(hasItem(DEFAULT_UPLOAD_TOKEN)));
    }
}
