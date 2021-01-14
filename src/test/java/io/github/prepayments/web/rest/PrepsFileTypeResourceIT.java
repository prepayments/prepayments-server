package io.github.prepayments.web.rest;

import io.github.prepayments.PrepaymentsApp;
import io.github.prepayments.domain.PrepsFileType;
import io.github.prepayments.repository.PrepsFileTypeRepository;
import io.github.prepayments.repository.search.PrepsFileTypeSearchRepository;
import io.github.prepayments.service.PrepsFileTypeService;
import io.github.prepayments.service.dto.PrepsFileTypeCriteria;
import io.github.prepayments.service.PrepsFileTypeQueryService;

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
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.prepayments.domain.enumeration.PrepsFileMediumTypes;
import io.github.prepayments.domain.enumeration.PrepsFileModelType;
/**
 * Integration tests for the {@link PrepsFileTypeResource} REST controller.
 */
@SpringBootTest(classes = PrepaymentsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class PrepsFileTypeResourceIT {

    private static final String DEFAULT_PREPS_FILE_TYPE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PREPS_FILE_TYPE_NAME = "BBBBBBBBBB";

    private static final PrepsFileMediumTypes DEFAULT_PREPS_FILE_MEDIUM_TYPE = PrepsFileMediumTypes.EXCEL;
    private static final PrepsFileMediumTypes UPDATED_PREPS_FILE_MEDIUM_TYPE = PrepsFileMediumTypes.EXCEL_XLS;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final byte[] DEFAULT_FILE_TEMPLATE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FILE_TEMPLATE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_FILE_TEMPLATE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_FILE_TEMPLATE_CONTENT_TYPE = "image/png";

    private static final PrepsFileModelType DEFAULT_PREPSFILE_TYPE = PrepsFileModelType.CURRENCY_LIST;
    private static final PrepsFileModelType UPDATED_PREPSFILE_TYPE = PrepsFileModelType.PREPAYMENT_DATA;

    @Autowired
    private PrepsFileTypeRepository prepsFileTypeRepository;

    @Autowired
    private PrepsFileTypeService prepsFileTypeService;

    /**
     * This repository is mocked in the io.github.prepayments.repository.search test package.
     *
     * @see io.github.prepayments.repository.search.PrepsFileTypeSearchRepositoryMockConfiguration
     */
    @Autowired
    private PrepsFileTypeSearchRepository mockPrepsFileTypeSearchRepository;

    @Autowired
    private PrepsFileTypeQueryService prepsFileTypeQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPrepsFileTypeMockMvc;

    private PrepsFileType prepsFileType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PrepsFileType createEntity(EntityManager em) {
        PrepsFileType prepsFileType = new PrepsFileType()
            .prepsFileTypeName(DEFAULT_PREPS_FILE_TYPE_NAME)
            .prepsFileMediumType(DEFAULT_PREPS_FILE_MEDIUM_TYPE)
            .description(DEFAULT_DESCRIPTION)
            .fileTemplate(DEFAULT_FILE_TEMPLATE)
            .fileTemplateContentType(DEFAULT_FILE_TEMPLATE_CONTENT_TYPE)
            .prepsfileType(DEFAULT_PREPSFILE_TYPE);
        return prepsFileType;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PrepsFileType createUpdatedEntity(EntityManager em) {
        PrepsFileType prepsFileType = new PrepsFileType()
            .prepsFileTypeName(UPDATED_PREPS_FILE_TYPE_NAME)
            .prepsFileMediumType(UPDATED_PREPS_FILE_MEDIUM_TYPE)
            .description(UPDATED_DESCRIPTION)
            .fileTemplate(UPDATED_FILE_TEMPLATE)
            .fileTemplateContentType(UPDATED_FILE_TEMPLATE_CONTENT_TYPE)
            .prepsfileType(UPDATED_PREPSFILE_TYPE);
        return prepsFileType;
    }

    @BeforeEach
    public void initTest() {
        prepsFileType = createEntity(em);
    }

    @Test
    @Transactional
    public void createPrepsFileType() throws Exception {
        int databaseSizeBeforeCreate = prepsFileTypeRepository.findAll().size();
        // Create the PrepsFileType
        restPrepsFileTypeMockMvc.perform(post("/api/preps-file-types")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepsFileType)))
            .andExpect(status().isCreated());

        // Validate the PrepsFileType in the database
        List<PrepsFileType> prepsFileTypeList = prepsFileTypeRepository.findAll();
        assertThat(prepsFileTypeList).hasSize(databaseSizeBeforeCreate + 1);
        PrepsFileType testPrepsFileType = prepsFileTypeList.get(prepsFileTypeList.size() - 1);
        assertThat(testPrepsFileType.getPrepsFileTypeName()).isEqualTo(DEFAULT_PREPS_FILE_TYPE_NAME);
        assertThat(testPrepsFileType.getPrepsFileMediumType()).isEqualTo(DEFAULT_PREPS_FILE_MEDIUM_TYPE);
        assertThat(testPrepsFileType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPrepsFileType.getFileTemplate()).isEqualTo(DEFAULT_FILE_TEMPLATE);
        assertThat(testPrepsFileType.getFileTemplateContentType()).isEqualTo(DEFAULT_FILE_TEMPLATE_CONTENT_TYPE);
        assertThat(testPrepsFileType.getPrepsfileType()).isEqualTo(DEFAULT_PREPSFILE_TYPE);

        // Validate the PrepsFileType in Elasticsearch
        verify(mockPrepsFileTypeSearchRepository, times(1)).save(testPrepsFileType);
    }

    @Test
    @Transactional
    public void createPrepsFileTypeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = prepsFileTypeRepository.findAll().size();

        // Create the PrepsFileType with an existing ID
        prepsFileType.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPrepsFileTypeMockMvc.perform(post("/api/preps-file-types")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepsFileType)))
            .andExpect(status().isBadRequest());

        // Validate the PrepsFileType in the database
        List<PrepsFileType> prepsFileTypeList = prepsFileTypeRepository.findAll();
        assertThat(prepsFileTypeList).hasSize(databaseSizeBeforeCreate);

        // Validate the PrepsFileType in Elasticsearch
        verify(mockPrepsFileTypeSearchRepository, times(0)).save(prepsFileType);
    }


    @Test
    @Transactional
    public void checkPrepsFileTypeNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = prepsFileTypeRepository.findAll().size();
        // set the field null
        prepsFileType.setPrepsFileTypeName(null);

        // Create the PrepsFileType, which fails.


        restPrepsFileTypeMockMvc.perform(post("/api/preps-file-types")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepsFileType)))
            .andExpect(status().isBadRequest());

        List<PrepsFileType> prepsFileTypeList = prepsFileTypeRepository.findAll();
        assertThat(prepsFileTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPrepsFileMediumTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = prepsFileTypeRepository.findAll().size();
        // set the field null
        prepsFileType.setPrepsFileMediumType(null);

        // Create the PrepsFileType, which fails.


        restPrepsFileTypeMockMvc.perform(post("/api/preps-file-types")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepsFileType)))
            .andExpect(status().isBadRequest());

        List<PrepsFileType> prepsFileTypeList = prepsFileTypeRepository.findAll();
        assertThat(prepsFileTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPrepsFileTypes() throws Exception {
        // Initialize the database
        prepsFileTypeRepository.saveAndFlush(prepsFileType);

        // Get all the prepsFileTypeList
        restPrepsFileTypeMockMvc.perform(get("/api/preps-file-types?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prepsFileType.getId().intValue())))
            .andExpect(jsonPath("$.[*].prepsFileTypeName").value(hasItem(DEFAULT_PREPS_FILE_TYPE_NAME)))
            .andExpect(jsonPath("$.[*].prepsFileMediumType").value(hasItem(DEFAULT_PREPS_FILE_MEDIUM_TYPE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].fileTemplateContentType").value(hasItem(DEFAULT_FILE_TEMPLATE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].fileTemplate").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILE_TEMPLATE))))
            .andExpect(jsonPath("$.[*].prepsfileType").value(hasItem(DEFAULT_PREPSFILE_TYPE.toString())));
    }
    
    @Test
    @Transactional
    public void getPrepsFileType() throws Exception {
        // Initialize the database
        prepsFileTypeRepository.saveAndFlush(prepsFileType);

        // Get the prepsFileType
        restPrepsFileTypeMockMvc.perform(get("/api/preps-file-types/{id}", prepsFileType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(prepsFileType.getId().intValue()))
            .andExpect(jsonPath("$.prepsFileTypeName").value(DEFAULT_PREPS_FILE_TYPE_NAME))
            .andExpect(jsonPath("$.prepsFileMediumType").value(DEFAULT_PREPS_FILE_MEDIUM_TYPE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.fileTemplateContentType").value(DEFAULT_FILE_TEMPLATE_CONTENT_TYPE))
            .andExpect(jsonPath("$.fileTemplate").value(Base64Utils.encodeToString(DEFAULT_FILE_TEMPLATE)))
            .andExpect(jsonPath("$.prepsfileType").value(DEFAULT_PREPSFILE_TYPE.toString()));
    }


    @Test
    @Transactional
    public void getPrepsFileTypesByIdFiltering() throws Exception {
        // Initialize the database
        prepsFileTypeRepository.saveAndFlush(prepsFileType);

        Long id = prepsFileType.getId();

        defaultPrepsFileTypeShouldBeFound("id.equals=" + id);
        defaultPrepsFileTypeShouldNotBeFound("id.notEquals=" + id);

        defaultPrepsFileTypeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPrepsFileTypeShouldNotBeFound("id.greaterThan=" + id);

        defaultPrepsFileTypeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPrepsFileTypeShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllPrepsFileTypesByPrepsFileTypeNameIsEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileTypeRepository.saveAndFlush(prepsFileType);

        // Get all the prepsFileTypeList where prepsFileTypeName equals to DEFAULT_PREPS_FILE_TYPE_NAME
        defaultPrepsFileTypeShouldBeFound("prepsFileTypeName.equals=" + DEFAULT_PREPS_FILE_TYPE_NAME);

        // Get all the prepsFileTypeList where prepsFileTypeName equals to UPDATED_PREPS_FILE_TYPE_NAME
        defaultPrepsFileTypeShouldNotBeFound("prepsFileTypeName.equals=" + UPDATED_PREPS_FILE_TYPE_NAME);
    }

    @Test
    @Transactional
    public void getAllPrepsFileTypesByPrepsFileTypeNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileTypeRepository.saveAndFlush(prepsFileType);

        // Get all the prepsFileTypeList where prepsFileTypeName not equals to DEFAULT_PREPS_FILE_TYPE_NAME
        defaultPrepsFileTypeShouldNotBeFound("prepsFileTypeName.notEquals=" + DEFAULT_PREPS_FILE_TYPE_NAME);

        // Get all the prepsFileTypeList where prepsFileTypeName not equals to UPDATED_PREPS_FILE_TYPE_NAME
        defaultPrepsFileTypeShouldBeFound("prepsFileTypeName.notEquals=" + UPDATED_PREPS_FILE_TYPE_NAME);
    }

    @Test
    @Transactional
    public void getAllPrepsFileTypesByPrepsFileTypeNameIsInShouldWork() throws Exception {
        // Initialize the database
        prepsFileTypeRepository.saveAndFlush(prepsFileType);

        // Get all the prepsFileTypeList where prepsFileTypeName in DEFAULT_PREPS_FILE_TYPE_NAME or UPDATED_PREPS_FILE_TYPE_NAME
        defaultPrepsFileTypeShouldBeFound("prepsFileTypeName.in=" + DEFAULT_PREPS_FILE_TYPE_NAME + "," + UPDATED_PREPS_FILE_TYPE_NAME);

        // Get all the prepsFileTypeList where prepsFileTypeName equals to UPDATED_PREPS_FILE_TYPE_NAME
        defaultPrepsFileTypeShouldNotBeFound("prepsFileTypeName.in=" + UPDATED_PREPS_FILE_TYPE_NAME);
    }

    @Test
    @Transactional
    public void getAllPrepsFileTypesByPrepsFileTypeNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepsFileTypeRepository.saveAndFlush(prepsFileType);

        // Get all the prepsFileTypeList where prepsFileTypeName is not null
        defaultPrepsFileTypeShouldBeFound("prepsFileTypeName.specified=true");

        // Get all the prepsFileTypeList where prepsFileTypeName is null
        defaultPrepsFileTypeShouldNotBeFound("prepsFileTypeName.specified=false");
    }
                @Test
    @Transactional
    public void getAllPrepsFileTypesByPrepsFileTypeNameContainsSomething() throws Exception {
        // Initialize the database
        prepsFileTypeRepository.saveAndFlush(prepsFileType);

        // Get all the prepsFileTypeList where prepsFileTypeName contains DEFAULT_PREPS_FILE_TYPE_NAME
        defaultPrepsFileTypeShouldBeFound("prepsFileTypeName.contains=" + DEFAULT_PREPS_FILE_TYPE_NAME);

        // Get all the prepsFileTypeList where prepsFileTypeName contains UPDATED_PREPS_FILE_TYPE_NAME
        defaultPrepsFileTypeShouldNotBeFound("prepsFileTypeName.contains=" + UPDATED_PREPS_FILE_TYPE_NAME);
    }

    @Test
    @Transactional
    public void getAllPrepsFileTypesByPrepsFileTypeNameNotContainsSomething() throws Exception {
        // Initialize the database
        prepsFileTypeRepository.saveAndFlush(prepsFileType);

        // Get all the prepsFileTypeList where prepsFileTypeName does not contain DEFAULT_PREPS_FILE_TYPE_NAME
        defaultPrepsFileTypeShouldNotBeFound("prepsFileTypeName.doesNotContain=" + DEFAULT_PREPS_FILE_TYPE_NAME);

        // Get all the prepsFileTypeList where prepsFileTypeName does not contain UPDATED_PREPS_FILE_TYPE_NAME
        defaultPrepsFileTypeShouldBeFound("prepsFileTypeName.doesNotContain=" + UPDATED_PREPS_FILE_TYPE_NAME);
    }


    @Test
    @Transactional
    public void getAllPrepsFileTypesByPrepsFileMediumTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileTypeRepository.saveAndFlush(prepsFileType);

        // Get all the prepsFileTypeList where prepsFileMediumType equals to DEFAULT_PREPS_FILE_MEDIUM_TYPE
        defaultPrepsFileTypeShouldBeFound("prepsFileMediumType.equals=" + DEFAULT_PREPS_FILE_MEDIUM_TYPE);

        // Get all the prepsFileTypeList where prepsFileMediumType equals to UPDATED_PREPS_FILE_MEDIUM_TYPE
        defaultPrepsFileTypeShouldNotBeFound("prepsFileMediumType.equals=" + UPDATED_PREPS_FILE_MEDIUM_TYPE);
    }

    @Test
    @Transactional
    public void getAllPrepsFileTypesByPrepsFileMediumTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileTypeRepository.saveAndFlush(prepsFileType);

        // Get all the prepsFileTypeList where prepsFileMediumType not equals to DEFAULT_PREPS_FILE_MEDIUM_TYPE
        defaultPrepsFileTypeShouldNotBeFound("prepsFileMediumType.notEquals=" + DEFAULT_PREPS_FILE_MEDIUM_TYPE);

        // Get all the prepsFileTypeList where prepsFileMediumType not equals to UPDATED_PREPS_FILE_MEDIUM_TYPE
        defaultPrepsFileTypeShouldBeFound("prepsFileMediumType.notEquals=" + UPDATED_PREPS_FILE_MEDIUM_TYPE);
    }

    @Test
    @Transactional
    public void getAllPrepsFileTypesByPrepsFileMediumTypeIsInShouldWork() throws Exception {
        // Initialize the database
        prepsFileTypeRepository.saveAndFlush(prepsFileType);

        // Get all the prepsFileTypeList where prepsFileMediumType in DEFAULT_PREPS_FILE_MEDIUM_TYPE or UPDATED_PREPS_FILE_MEDIUM_TYPE
        defaultPrepsFileTypeShouldBeFound("prepsFileMediumType.in=" + DEFAULT_PREPS_FILE_MEDIUM_TYPE + "," + UPDATED_PREPS_FILE_MEDIUM_TYPE);

        // Get all the prepsFileTypeList where prepsFileMediumType equals to UPDATED_PREPS_FILE_MEDIUM_TYPE
        defaultPrepsFileTypeShouldNotBeFound("prepsFileMediumType.in=" + UPDATED_PREPS_FILE_MEDIUM_TYPE);
    }

    @Test
    @Transactional
    public void getAllPrepsFileTypesByPrepsFileMediumTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepsFileTypeRepository.saveAndFlush(prepsFileType);

        // Get all the prepsFileTypeList where prepsFileMediumType is not null
        defaultPrepsFileTypeShouldBeFound("prepsFileMediumType.specified=true");

        // Get all the prepsFileTypeList where prepsFileMediumType is null
        defaultPrepsFileTypeShouldNotBeFound("prepsFileMediumType.specified=false");
    }

    @Test
    @Transactional
    public void getAllPrepsFileTypesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileTypeRepository.saveAndFlush(prepsFileType);

        // Get all the prepsFileTypeList where description equals to DEFAULT_DESCRIPTION
        defaultPrepsFileTypeShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the prepsFileTypeList where description equals to UPDATED_DESCRIPTION
        defaultPrepsFileTypeShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPrepsFileTypesByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileTypeRepository.saveAndFlush(prepsFileType);

        // Get all the prepsFileTypeList where description not equals to DEFAULT_DESCRIPTION
        defaultPrepsFileTypeShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the prepsFileTypeList where description not equals to UPDATED_DESCRIPTION
        defaultPrepsFileTypeShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPrepsFileTypesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        prepsFileTypeRepository.saveAndFlush(prepsFileType);

        // Get all the prepsFileTypeList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultPrepsFileTypeShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the prepsFileTypeList where description equals to UPDATED_DESCRIPTION
        defaultPrepsFileTypeShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPrepsFileTypesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepsFileTypeRepository.saveAndFlush(prepsFileType);

        // Get all the prepsFileTypeList where description is not null
        defaultPrepsFileTypeShouldBeFound("description.specified=true");

        // Get all the prepsFileTypeList where description is null
        defaultPrepsFileTypeShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllPrepsFileTypesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        prepsFileTypeRepository.saveAndFlush(prepsFileType);

        // Get all the prepsFileTypeList where description contains DEFAULT_DESCRIPTION
        defaultPrepsFileTypeShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the prepsFileTypeList where description contains UPDATED_DESCRIPTION
        defaultPrepsFileTypeShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPrepsFileTypesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        prepsFileTypeRepository.saveAndFlush(prepsFileType);

        // Get all the prepsFileTypeList where description does not contain DEFAULT_DESCRIPTION
        defaultPrepsFileTypeShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the prepsFileTypeList where description does not contain UPDATED_DESCRIPTION
        defaultPrepsFileTypeShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllPrepsFileTypesByPrepsfileTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileTypeRepository.saveAndFlush(prepsFileType);

        // Get all the prepsFileTypeList where prepsfileType equals to DEFAULT_PREPSFILE_TYPE
        defaultPrepsFileTypeShouldBeFound("prepsfileType.equals=" + DEFAULT_PREPSFILE_TYPE);

        // Get all the prepsFileTypeList where prepsfileType equals to UPDATED_PREPSFILE_TYPE
        defaultPrepsFileTypeShouldNotBeFound("prepsfileType.equals=" + UPDATED_PREPSFILE_TYPE);
    }

    @Test
    @Transactional
    public void getAllPrepsFileTypesByPrepsfileTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepsFileTypeRepository.saveAndFlush(prepsFileType);

        // Get all the prepsFileTypeList where prepsfileType not equals to DEFAULT_PREPSFILE_TYPE
        defaultPrepsFileTypeShouldNotBeFound("prepsfileType.notEquals=" + DEFAULT_PREPSFILE_TYPE);

        // Get all the prepsFileTypeList where prepsfileType not equals to UPDATED_PREPSFILE_TYPE
        defaultPrepsFileTypeShouldBeFound("prepsfileType.notEquals=" + UPDATED_PREPSFILE_TYPE);
    }

    @Test
    @Transactional
    public void getAllPrepsFileTypesByPrepsfileTypeIsInShouldWork() throws Exception {
        // Initialize the database
        prepsFileTypeRepository.saveAndFlush(prepsFileType);

        // Get all the prepsFileTypeList where prepsfileType in DEFAULT_PREPSFILE_TYPE or UPDATED_PREPSFILE_TYPE
        defaultPrepsFileTypeShouldBeFound("prepsfileType.in=" + DEFAULT_PREPSFILE_TYPE + "," + UPDATED_PREPSFILE_TYPE);

        // Get all the prepsFileTypeList where prepsfileType equals to UPDATED_PREPSFILE_TYPE
        defaultPrepsFileTypeShouldNotBeFound("prepsfileType.in=" + UPDATED_PREPSFILE_TYPE);
    }

    @Test
    @Transactional
    public void getAllPrepsFileTypesByPrepsfileTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepsFileTypeRepository.saveAndFlush(prepsFileType);

        // Get all the prepsFileTypeList where prepsfileType is not null
        defaultPrepsFileTypeShouldBeFound("prepsfileType.specified=true");

        // Get all the prepsFileTypeList where prepsfileType is null
        defaultPrepsFileTypeShouldNotBeFound("prepsfileType.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPrepsFileTypeShouldBeFound(String filter) throws Exception {
        restPrepsFileTypeMockMvc.perform(get("/api/preps-file-types?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prepsFileType.getId().intValue())))
            .andExpect(jsonPath("$.[*].prepsFileTypeName").value(hasItem(DEFAULT_PREPS_FILE_TYPE_NAME)))
            .andExpect(jsonPath("$.[*].prepsFileMediumType").value(hasItem(DEFAULT_PREPS_FILE_MEDIUM_TYPE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].fileTemplateContentType").value(hasItem(DEFAULT_FILE_TEMPLATE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].fileTemplate").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILE_TEMPLATE))))
            .andExpect(jsonPath("$.[*].prepsfileType").value(hasItem(DEFAULT_PREPSFILE_TYPE.toString())));

        // Check, that the count call also returns 1
        restPrepsFileTypeMockMvc.perform(get("/api/preps-file-types/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPrepsFileTypeShouldNotBeFound(String filter) throws Exception {
        restPrepsFileTypeMockMvc.perform(get("/api/preps-file-types?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPrepsFileTypeMockMvc.perform(get("/api/preps-file-types/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingPrepsFileType() throws Exception {
        // Get the prepsFileType
        restPrepsFileTypeMockMvc.perform(get("/api/preps-file-types/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePrepsFileType() throws Exception {
        // Initialize the database
        prepsFileTypeService.save(prepsFileType);

        int databaseSizeBeforeUpdate = prepsFileTypeRepository.findAll().size();

        // Update the prepsFileType
        PrepsFileType updatedPrepsFileType = prepsFileTypeRepository.findById(prepsFileType.getId()).get();
        // Disconnect from session so that the updates on updatedPrepsFileType are not directly saved in db
        em.detach(updatedPrepsFileType);
        updatedPrepsFileType
            .prepsFileTypeName(UPDATED_PREPS_FILE_TYPE_NAME)
            .prepsFileMediumType(UPDATED_PREPS_FILE_MEDIUM_TYPE)
            .description(UPDATED_DESCRIPTION)
            .fileTemplate(UPDATED_FILE_TEMPLATE)
            .fileTemplateContentType(UPDATED_FILE_TEMPLATE_CONTENT_TYPE)
            .prepsfileType(UPDATED_PREPSFILE_TYPE);

        restPrepsFileTypeMockMvc.perform(put("/api/preps-file-types")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedPrepsFileType)))
            .andExpect(status().isOk());

        // Validate the PrepsFileType in the database
        List<PrepsFileType> prepsFileTypeList = prepsFileTypeRepository.findAll();
        assertThat(prepsFileTypeList).hasSize(databaseSizeBeforeUpdate);
        PrepsFileType testPrepsFileType = prepsFileTypeList.get(prepsFileTypeList.size() - 1);
        assertThat(testPrepsFileType.getPrepsFileTypeName()).isEqualTo(UPDATED_PREPS_FILE_TYPE_NAME);
        assertThat(testPrepsFileType.getPrepsFileMediumType()).isEqualTo(UPDATED_PREPS_FILE_MEDIUM_TYPE);
        assertThat(testPrepsFileType.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPrepsFileType.getFileTemplate()).isEqualTo(UPDATED_FILE_TEMPLATE);
        assertThat(testPrepsFileType.getFileTemplateContentType()).isEqualTo(UPDATED_FILE_TEMPLATE_CONTENT_TYPE);
        assertThat(testPrepsFileType.getPrepsfileType()).isEqualTo(UPDATED_PREPSFILE_TYPE);

        // Validate the PrepsFileType in Elasticsearch
        verify(mockPrepsFileTypeSearchRepository, times(2)).save(testPrepsFileType);
    }

    @Test
    @Transactional
    public void updateNonExistingPrepsFileType() throws Exception {
        int databaseSizeBeforeUpdate = prepsFileTypeRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPrepsFileTypeMockMvc.perform(put("/api/preps-file-types")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepsFileType)))
            .andExpect(status().isBadRequest());

        // Validate the PrepsFileType in the database
        List<PrepsFileType> prepsFileTypeList = prepsFileTypeRepository.findAll();
        assertThat(prepsFileTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PrepsFileType in Elasticsearch
        verify(mockPrepsFileTypeSearchRepository, times(0)).save(prepsFileType);
    }

    @Test
    @Transactional
    public void deletePrepsFileType() throws Exception {
        // Initialize the database
        prepsFileTypeService.save(prepsFileType);

        int databaseSizeBeforeDelete = prepsFileTypeRepository.findAll().size();

        // Delete the prepsFileType
        restPrepsFileTypeMockMvc.perform(delete("/api/preps-file-types/{id}", prepsFileType.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PrepsFileType> prepsFileTypeList = prepsFileTypeRepository.findAll();
        assertThat(prepsFileTypeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the PrepsFileType in Elasticsearch
        verify(mockPrepsFileTypeSearchRepository, times(1)).deleteById(prepsFileType.getId());
    }

    @Test
    @Transactional
    public void searchPrepsFileType() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        prepsFileTypeService.save(prepsFileType);
        when(mockPrepsFileTypeSearchRepository.search(queryStringQuery("id:" + prepsFileType.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(prepsFileType), PageRequest.of(0, 1), 1));

        // Search the prepsFileType
        restPrepsFileTypeMockMvc.perform(get("/api/_search/preps-file-types?query=id:" + prepsFileType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prepsFileType.getId().intValue())))
            .andExpect(jsonPath("$.[*].prepsFileTypeName").value(hasItem(DEFAULT_PREPS_FILE_TYPE_NAME)))
            .andExpect(jsonPath("$.[*].prepsFileMediumType").value(hasItem(DEFAULT_PREPS_FILE_MEDIUM_TYPE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].fileTemplateContentType").value(hasItem(DEFAULT_FILE_TEMPLATE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].fileTemplate").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILE_TEMPLATE))))
            .andExpect(jsonPath("$.[*].prepsfileType").value(hasItem(DEFAULT_PREPSFILE_TYPE.toString())));
    }
}
