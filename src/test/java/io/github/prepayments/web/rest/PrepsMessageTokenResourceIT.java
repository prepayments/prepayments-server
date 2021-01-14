package io.github.prepayments.web.rest;

import io.github.prepayments.PrepaymentsApp;
import io.github.prepayments.domain.PrepsMessageToken;
import io.github.prepayments.repository.PrepsMessageTokenRepository;
import io.github.prepayments.repository.search.PrepsMessageTokenSearchRepository;
import io.github.prepayments.service.PrepsMessageTokenService;
import io.github.prepayments.service.dto.PrepsMessageTokenDTO;
import io.github.prepayments.service.mapper.PrepsMessageTokenMapper;
import io.github.prepayments.service.dto.PrepsMessageTokenCriteria;
import io.github.prepayments.service.PrepsMessageTokenQueryService;

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

/**
 * Integration tests for the {@link PrepsMessageTokenResource} REST controller.
 */
@SpringBootTest(classes = PrepaymentsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class PrepsMessageTokenResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Long DEFAULT_TIME_SENT = 1L;
    private static final Long UPDATED_TIME_SENT = 2L;
    private static final Long SMALLER_TIME_SENT = 1L - 1L;

    private static final String DEFAULT_TOKEN_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_TOKEN_VALUE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_RECEIVED = false;
    private static final Boolean UPDATED_RECEIVED = true;

    private static final Boolean DEFAULT_ACTIONED = false;
    private static final Boolean UPDATED_ACTIONED = true;

    private static final Boolean DEFAULT_CONTENT_FULLY_ENQUEUED = false;
    private static final Boolean UPDATED_CONTENT_FULLY_ENQUEUED = true;

    @Autowired
    private PrepsMessageTokenRepository prepsMessageTokenRepository;

    @Autowired
    private PrepsMessageTokenMapper prepsMessageTokenMapper;

    @Autowired
    private PrepsMessageTokenService prepsMessageTokenService;

    /**
     * This repository is mocked in the io.github.prepayments.repository.search test package.
     *
     * @see io.github.prepayments.repository.search.PrepsMessageTokenSearchRepositoryMockConfiguration
     */
    @Autowired
    private PrepsMessageTokenSearchRepository mockPrepsMessageTokenSearchRepository;

    @Autowired
    private PrepsMessageTokenQueryService prepsMessageTokenQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPrepsMessageTokenMockMvc;

    private PrepsMessageToken prepsMessageToken;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PrepsMessageToken createEntity(EntityManager em) {
        PrepsMessageToken prepsMessageToken = new PrepsMessageToken()
            .description(DEFAULT_DESCRIPTION)
            .timeSent(DEFAULT_TIME_SENT)
            .tokenValue(DEFAULT_TOKEN_VALUE)
            .received(DEFAULT_RECEIVED)
            .actioned(DEFAULT_ACTIONED)
            .contentFullyEnqueued(DEFAULT_CONTENT_FULLY_ENQUEUED);
        return prepsMessageToken;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PrepsMessageToken createUpdatedEntity(EntityManager em) {
        PrepsMessageToken prepsMessageToken = new PrepsMessageToken()
            .description(UPDATED_DESCRIPTION)
            .timeSent(UPDATED_TIME_SENT)
            .tokenValue(UPDATED_TOKEN_VALUE)
            .received(UPDATED_RECEIVED)
            .actioned(UPDATED_ACTIONED)
            .contentFullyEnqueued(UPDATED_CONTENT_FULLY_ENQUEUED);
        return prepsMessageToken;
    }

    @BeforeEach
    public void initTest() {
        prepsMessageToken = createEntity(em);
    }

    @Test
    @Transactional
    public void createPrepsMessageToken() throws Exception {
        int databaseSizeBeforeCreate = prepsMessageTokenRepository.findAll().size();
        // Create the PrepsMessageToken
        PrepsMessageTokenDTO prepsMessageTokenDTO = prepsMessageTokenMapper.toDto(prepsMessageToken);
        restPrepsMessageTokenMockMvc.perform(post("/api/preps-message-tokens")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepsMessageTokenDTO)))
            .andExpect(status().isCreated());

        // Validate the PrepsMessageToken in the database
        List<PrepsMessageToken> prepsMessageTokenList = prepsMessageTokenRepository.findAll();
        assertThat(prepsMessageTokenList).hasSize(databaseSizeBeforeCreate + 1);
        PrepsMessageToken testPrepsMessageToken = prepsMessageTokenList.get(prepsMessageTokenList.size() - 1);
        assertThat(testPrepsMessageToken.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPrepsMessageToken.getTimeSent()).isEqualTo(DEFAULT_TIME_SENT);
        assertThat(testPrepsMessageToken.getTokenValue()).isEqualTo(DEFAULT_TOKEN_VALUE);
        assertThat(testPrepsMessageToken.isReceived()).isEqualTo(DEFAULT_RECEIVED);
        assertThat(testPrepsMessageToken.isActioned()).isEqualTo(DEFAULT_ACTIONED);
        assertThat(testPrepsMessageToken.isContentFullyEnqueued()).isEqualTo(DEFAULT_CONTENT_FULLY_ENQUEUED);

        // Validate the PrepsMessageToken in Elasticsearch
        verify(mockPrepsMessageTokenSearchRepository, times(1)).save(testPrepsMessageToken);
    }

    @Test
    @Transactional
    public void createPrepsMessageTokenWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = prepsMessageTokenRepository.findAll().size();

        // Create the PrepsMessageToken with an existing ID
        prepsMessageToken.setId(1L);
        PrepsMessageTokenDTO prepsMessageTokenDTO = prepsMessageTokenMapper.toDto(prepsMessageToken);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPrepsMessageTokenMockMvc.perform(post("/api/preps-message-tokens")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepsMessageTokenDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PrepsMessageToken in the database
        List<PrepsMessageToken> prepsMessageTokenList = prepsMessageTokenRepository.findAll();
        assertThat(prepsMessageTokenList).hasSize(databaseSizeBeforeCreate);

        // Validate the PrepsMessageToken in Elasticsearch
        verify(mockPrepsMessageTokenSearchRepository, times(0)).save(prepsMessageToken);
    }


    @Test
    @Transactional
    public void checkTimeSentIsRequired() throws Exception {
        int databaseSizeBeforeTest = prepsMessageTokenRepository.findAll().size();
        // set the field null
        prepsMessageToken.setTimeSent(null);

        // Create the PrepsMessageToken, which fails.
        PrepsMessageTokenDTO prepsMessageTokenDTO = prepsMessageTokenMapper.toDto(prepsMessageToken);


        restPrepsMessageTokenMockMvc.perform(post("/api/preps-message-tokens")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepsMessageTokenDTO)))
            .andExpect(status().isBadRequest());

        List<PrepsMessageToken> prepsMessageTokenList = prepsMessageTokenRepository.findAll();
        assertThat(prepsMessageTokenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTokenValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = prepsMessageTokenRepository.findAll().size();
        // set the field null
        prepsMessageToken.setTokenValue(null);

        // Create the PrepsMessageToken, which fails.
        PrepsMessageTokenDTO prepsMessageTokenDTO = prepsMessageTokenMapper.toDto(prepsMessageToken);


        restPrepsMessageTokenMockMvc.perform(post("/api/preps-message-tokens")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepsMessageTokenDTO)))
            .andExpect(status().isBadRequest());

        List<PrepsMessageToken> prepsMessageTokenList = prepsMessageTokenRepository.findAll();
        assertThat(prepsMessageTokenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokens() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList
        restPrepsMessageTokenMockMvc.perform(get("/api/preps-message-tokens?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prepsMessageToken.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].timeSent").value(hasItem(DEFAULT_TIME_SENT.intValue())))
            .andExpect(jsonPath("$.[*].tokenValue").value(hasItem(DEFAULT_TOKEN_VALUE)))
            .andExpect(jsonPath("$.[*].received").value(hasItem(DEFAULT_RECEIVED.booleanValue())))
            .andExpect(jsonPath("$.[*].actioned").value(hasItem(DEFAULT_ACTIONED.booleanValue())))
            .andExpect(jsonPath("$.[*].contentFullyEnqueued").value(hasItem(DEFAULT_CONTENT_FULLY_ENQUEUED.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getPrepsMessageToken() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get the prepsMessageToken
        restPrepsMessageTokenMockMvc.perform(get("/api/preps-message-tokens/{id}", prepsMessageToken.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(prepsMessageToken.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.timeSent").value(DEFAULT_TIME_SENT.intValue()))
            .andExpect(jsonPath("$.tokenValue").value(DEFAULT_TOKEN_VALUE))
            .andExpect(jsonPath("$.received").value(DEFAULT_RECEIVED.booleanValue()))
            .andExpect(jsonPath("$.actioned").value(DEFAULT_ACTIONED.booleanValue()))
            .andExpect(jsonPath("$.contentFullyEnqueued").value(DEFAULT_CONTENT_FULLY_ENQUEUED.booleanValue()));
    }


    @Test
    @Transactional
    public void getPrepsMessageTokensByIdFiltering() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        Long id = prepsMessageToken.getId();

        defaultPrepsMessageTokenShouldBeFound("id.equals=" + id);
        defaultPrepsMessageTokenShouldNotBeFound("id.notEquals=" + id);

        defaultPrepsMessageTokenShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPrepsMessageTokenShouldNotBeFound("id.greaterThan=" + id);

        defaultPrepsMessageTokenShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPrepsMessageTokenShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllPrepsMessageTokensByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where description equals to DEFAULT_DESCRIPTION
        defaultPrepsMessageTokenShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the prepsMessageTokenList where description equals to UPDATED_DESCRIPTION
        defaultPrepsMessageTokenShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where description not equals to DEFAULT_DESCRIPTION
        defaultPrepsMessageTokenShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the prepsMessageTokenList where description not equals to UPDATED_DESCRIPTION
        defaultPrepsMessageTokenShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultPrepsMessageTokenShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the prepsMessageTokenList where description equals to UPDATED_DESCRIPTION
        defaultPrepsMessageTokenShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where description is not null
        defaultPrepsMessageTokenShouldBeFound("description.specified=true");

        // Get all the prepsMessageTokenList where description is null
        defaultPrepsMessageTokenShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllPrepsMessageTokensByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where description contains DEFAULT_DESCRIPTION
        defaultPrepsMessageTokenShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the prepsMessageTokenList where description contains UPDATED_DESCRIPTION
        defaultPrepsMessageTokenShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where description does not contain DEFAULT_DESCRIPTION
        defaultPrepsMessageTokenShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the prepsMessageTokenList where description does not contain UPDATED_DESCRIPTION
        defaultPrepsMessageTokenShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllPrepsMessageTokensByTimeSentIsEqualToSomething() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where timeSent equals to DEFAULT_TIME_SENT
        defaultPrepsMessageTokenShouldBeFound("timeSent.equals=" + DEFAULT_TIME_SENT);

        // Get all the prepsMessageTokenList where timeSent equals to UPDATED_TIME_SENT
        defaultPrepsMessageTokenShouldNotBeFound("timeSent.equals=" + UPDATED_TIME_SENT);
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByTimeSentIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where timeSent not equals to DEFAULT_TIME_SENT
        defaultPrepsMessageTokenShouldNotBeFound("timeSent.notEquals=" + DEFAULT_TIME_SENT);

        // Get all the prepsMessageTokenList where timeSent not equals to UPDATED_TIME_SENT
        defaultPrepsMessageTokenShouldBeFound("timeSent.notEquals=" + UPDATED_TIME_SENT);
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByTimeSentIsInShouldWork() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where timeSent in DEFAULT_TIME_SENT or UPDATED_TIME_SENT
        defaultPrepsMessageTokenShouldBeFound("timeSent.in=" + DEFAULT_TIME_SENT + "," + UPDATED_TIME_SENT);

        // Get all the prepsMessageTokenList where timeSent equals to UPDATED_TIME_SENT
        defaultPrepsMessageTokenShouldNotBeFound("timeSent.in=" + UPDATED_TIME_SENT);
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByTimeSentIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where timeSent is not null
        defaultPrepsMessageTokenShouldBeFound("timeSent.specified=true");

        // Get all the prepsMessageTokenList where timeSent is null
        defaultPrepsMessageTokenShouldNotBeFound("timeSent.specified=false");
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByTimeSentIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where timeSent is greater than or equal to DEFAULT_TIME_SENT
        defaultPrepsMessageTokenShouldBeFound("timeSent.greaterThanOrEqual=" + DEFAULT_TIME_SENT);

        // Get all the prepsMessageTokenList where timeSent is greater than or equal to UPDATED_TIME_SENT
        defaultPrepsMessageTokenShouldNotBeFound("timeSent.greaterThanOrEqual=" + UPDATED_TIME_SENT);
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByTimeSentIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where timeSent is less than or equal to DEFAULT_TIME_SENT
        defaultPrepsMessageTokenShouldBeFound("timeSent.lessThanOrEqual=" + DEFAULT_TIME_SENT);

        // Get all the prepsMessageTokenList where timeSent is less than or equal to SMALLER_TIME_SENT
        defaultPrepsMessageTokenShouldNotBeFound("timeSent.lessThanOrEqual=" + SMALLER_TIME_SENT);
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByTimeSentIsLessThanSomething() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where timeSent is less than DEFAULT_TIME_SENT
        defaultPrepsMessageTokenShouldNotBeFound("timeSent.lessThan=" + DEFAULT_TIME_SENT);

        // Get all the prepsMessageTokenList where timeSent is less than UPDATED_TIME_SENT
        defaultPrepsMessageTokenShouldBeFound("timeSent.lessThan=" + UPDATED_TIME_SENT);
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByTimeSentIsGreaterThanSomething() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where timeSent is greater than DEFAULT_TIME_SENT
        defaultPrepsMessageTokenShouldNotBeFound("timeSent.greaterThan=" + DEFAULT_TIME_SENT);

        // Get all the prepsMessageTokenList where timeSent is greater than SMALLER_TIME_SENT
        defaultPrepsMessageTokenShouldBeFound("timeSent.greaterThan=" + SMALLER_TIME_SENT);
    }


    @Test
    @Transactional
    public void getAllPrepsMessageTokensByTokenValueIsEqualToSomething() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where tokenValue equals to DEFAULT_TOKEN_VALUE
        defaultPrepsMessageTokenShouldBeFound("tokenValue.equals=" + DEFAULT_TOKEN_VALUE);

        // Get all the prepsMessageTokenList where tokenValue equals to UPDATED_TOKEN_VALUE
        defaultPrepsMessageTokenShouldNotBeFound("tokenValue.equals=" + UPDATED_TOKEN_VALUE);
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByTokenValueIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where tokenValue not equals to DEFAULT_TOKEN_VALUE
        defaultPrepsMessageTokenShouldNotBeFound("tokenValue.notEquals=" + DEFAULT_TOKEN_VALUE);

        // Get all the prepsMessageTokenList where tokenValue not equals to UPDATED_TOKEN_VALUE
        defaultPrepsMessageTokenShouldBeFound("tokenValue.notEquals=" + UPDATED_TOKEN_VALUE);
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByTokenValueIsInShouldWork() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where tokenValue in DEFAULT_TOKEN_VALUE or UPDATED_TOKEN_VALUE
        defaultPrepsMessageTokenShouldBeFound("tokenValue.in=" + DEFAULT_TOKEN_VALUE + "," + UPDATED_TOKEN_VALUE);

        // Get all the prepsMessageTokenList where tokenValue equals to UPDATED_TOKEN_VALUE
        defaultPrepsMessageTokenShouldNotBeFound("tokenValue.in=" + UPDATED_TOKEN_VALUE);
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByTokenValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where tokenValue is not null
        defaultPrepsMessageTokenShouldBeFound("tokenValue.specified=true");

        // Get all the prepsMessageTokenList where tokenValue is null
        defaultPrepsMessageTokenShouldNotBeFound("tokenValue.specified=false");
    }
                @Test
    @Transactional
    public void getAllPrepsMessageTokensByTokenValueContainsSomething() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where tokenValue contains DEFAULT_TOKEN_VALUE
        defaultPrepsMessageTokenShouldBeFound("tokenValue.contains=" + DEFAULT_TOKEN_VALUE);

        // Get all the prepsMessageTokenList where tokenValue contains UPDATED_TOKEN_VALUE
        defaultPrepsMessageTokenShouldNotBeFound("tokenValue.contains=" + UPDATED_TOKEN_VALUE);
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByTokenValueNotContainsSomething() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where tokenValue does not contain DEFAULT_TOKEN_VALUE
        defaultPrepsMessageTokenShouldNotBeFound("tokenValue.doesNotContain=" + DEFAULT_TOKEN_VALUE);

        // Get all the prepsMessageTokenList where tokenValue does not contain UPDATED_TOKEN_VALUE
        defaultPrepsMessageTokenShouldBeFound("tokenValue.doesNotContain=" + UPDATED_TOKEN_VALUE);
    }


    @Test
    @Transactional
    public void getAllPrepsMessageTokensByReceivedIsEqualToSomething() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where received equals to DEFAULT_RECEIVED
        defaultPrepsMessageTokenShouldBeFound("received.equals=" + DEFAULT_RECEIVED);

        // Get all the prepsMessageTokenList where received equals to UPDATED_RECEIVED
        defaultPrepsMessageTokenShouldNotBeFound("received.equals=" + UPDATED_RECEIVED);
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByReceivedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where received not equals to DEFAULT_RECEIVED
        defaultPrepsMessageTokenShouldNotBeFound("received.notEquals=" + DEFAULT_RECEIVED);

        // Get all the prepsMessageTokenList where received not equals to UPDATED_RECEIVED
        defaultPrepsMessageTokenShouldBeFound("received.notEquals=" + UPDATED_RECEIVED);
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByReceivedIsInShouldWork() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where received in DEFAULT_RECEIVED or UPDATED_RECEIVED
        defaultPrepsMessageTokenShouldBeFound("received.in=" + DEFAULT_RECEIVED + "," + UPDATED_RECEIVED);

        // Get all the prepsMessageTokenList where received equals to UPDATED_RECEIVED
        defaultPrepsMessageTokenShouldNotBeFound("received.in=" + UPDATED_RECEIVED);
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByReceivedIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where received is not null
        defaultPrepsMessageTokenShouldBeFound("received.specified=true");

        // Get all the prepsMessageTokenList where received is null
        defaultPrepsMessageTokenShouldNotBeFound("received.specified=false");
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByActionedIsEqualToSomething() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where actioned equals to DEFAULT_ACTIONED
        defaultPrepsMessageTokenShouldBeFound("actioned.equals=" + DEFAULT_ACTIONED);

        // Get all the prepsMessageTokenList where actioned equals to UPDATED_ACTIONED
        defaultPrepsMessageTokenShouldNotBeFound("actioned.equals=" + UPDATED_ACTIONED);
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByActionedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where actioned not equals to DEFAULT_ACTIONED
        defaultPrepsMessageTokenShouldNotBeFound("actioned.notEquals=" + DEFAULT_ACTIONED);

        // Get all the prepsMessageTokenList where actioned not equals to UPDATED_ACTIONED
        defaultPrepsMessageTokenShouldBeFound("actioned.notEquals=" + UPDATED_ACTIONED);
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByActionedIsInShouldWork() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where actioned in DEFAULT_ACTIONED or UPDATED_ACTIONED
        defaultPrepsMessageTokenShouldBeFound("actioned.in=" + DEFAULT_ACTIONED + "," + UPDATED_ACTIONED);

        // Get all the prepsMessageTokenList where actioned equals to UPDATED_ACTIONED
        defaultPrepsMessageTokenShouldNotBeFound("actioned.in=" + UPDATED_ACTIONED);
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByActionedIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where actioned is not null
        defaultPrepsMessageTokenShouldBeFound("actioned.specified=true");

        // Get all the prepsMessageTokenList where actioned is null
        defaultPrepsMessageTokenShouldNotBeFound("actioned.specified=false");
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByContentFullyEnqueuedIsEqualToSomething() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where contentFullyEnqueued equals to DEFAULT_CONTENT_FULLY_ENQUEUED
        defaultPrepsMessageTokenShouldBeFound("contentFullyEnqueued.equals=" + DEFAULT_CONTENT_FULLY_ENQUEUED);

        // Get all the prepsMessageTokenList where contentFullyEnqueued equals to UPDATED_CONTENT_FULLY_ENQUEUED
        defaultPrepsMessageTokenShouldNotBeFound("contentFullyEnqueued.equals=" + UPDATED_CONTENT_FULLY_ENQUEUED);
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByContentFullyEnqueuedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where contentFullyEnqueued not equals to DEFAULT_CONTENT_FULLY_ENQUEUED
        defaultPrepsMessageTokenShouldNotBeFound("contentFullyEnqueued.notEquals=" + DEFAULT_CONTENT_FULLY_ENQUEUED);

        // Get all the prepsMessageTokenList where contentFullyEnqueued not equals to UPDATED_CONTENT_FULLY_ENQUEUED
        defaultPrepsMessageTokenShouldBeFound("contentFullyEnqueued.notEquals=" + UPDATED_CONTENT_FULLY_ENQUEUED);
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByContentFullyEnqueuedIsInShouldWork() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where contentFullyEnqueued in DEFAULT_CONTENT_FULLY_ENQUEUED or UPDATED_CONTENT_FULLY_ENQUEUED
        defaultPrepsMessageTokenShouldBeFound("contentFullyEnqueued.in=" + DEFAULT_CONTENT_FULLY_ENQUEUED + "," + UPDATED_CONTENT_FULLY_ENQUEUED);

        // Get all the prepsMessageTokenList where contentFullyEnqueued equals to UPDATED_CONTENT_FULLY_ENQUEUED
        defaultPrepsMessageTokenShouldNotBeFound("contentFullyEnqueued.in=" + UPDATED_CONTENT_FULLY_ENQUEUED);
    }

    @Test
    @Transactional
    public void getAllPrepsMessageTokensByContentFullyEnqueuedIsNullOrNotNull() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        // Get all the prepsMessageTokenList where contentFullyEnqueued is not null
        defaultPrepsMessageTokenShouldBeFound("contentFullyEnqueued.specified=true");

        // Get all the prepsMessageTokenList where contentFullyEnqueued is null
        defaultPrepsMessageTokenShouldNotBeFound("contentFullyEnqueued.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPrepsMessageTokenShouldBeFound(String filter) throws Exception {
        restPrepsMessageTokenMockMvc.perform(get("/api/preps-message-tokens?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prepsMessageToken.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].timeSent").value(hasItem(DEFAULT_TIME_SENT.intValue())))
            .andExpect(jsonPath("$.[*].tokenValue").value(hasItem(DEFAULT_TOKEN_VALUE)))
            .andExpect(jsonPath("$.[*].received").value(hasItem(DEFAULT_RECEIVED.booleanValue())))
            .andExpect(jsonPath("$.[*].actioned").value(hasItem(DEFAULT_ACTIONED.booleanValue())))
            .andExpect(jsonPath("$.[*].contentFullyEnqueued").value(hasItem(DEFAULT_CONTENT_FULLY_ENQUEUED.booleanValue())));

        // Check, that the count call also returns 1
        restPrepsMessageTokenMockMvc.perform(get("/api/preps-message-tokens/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPrepsMessageTokenShouldNotBeFound(String filter) throws Exception {
        restPrepsMessageTokenMockMvc.perform(get("/api/preps-message-tokens?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPrepsMessageTokenMockMvc.perform(get("/api/preps-message-tokens/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingPrepsMessageToken() throws Exception {
        // Get the prepsMessageToken
        restPrepsMessageTokenMockMvc.perform(get("/api/preps-message-tokens/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePrepsMessageToken() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        int databaseSizeBeforeUpdate = prepsMessageTokenRepository.findAll().size();

        // Update the prepsMessageToken
        PrepsMessageToken updatedPrepsMessageToken = prepsMessageTokenRepository.findById(prepsMessageToken.getId()).get();
        // Disconnect from session so that the updates on updatedPrepsMessageToken are not directly saved in db
        em.detach(updatedPrepsMessageToken);
        updatedPrepsMessageToken
            .description(UPDATED_DESCRIPTION)
            .timeSent(UPDATED_TIME_SENT)
            .tokenValue(UPDATED_TOKEN_VALUE)
            .received(UPDATED_RECEIVED)
            .actioned(UPDATED_ACTIONED)
            .contentFullyEnqueued(UPDATED_CONTENT_FULLY_ENQUEUED);
        PrepsMessageTokenDTO prepsMessageTokenDTO = prepsMessageTokenMapper.toDto(updatedPrepsMessageToken);

        restPrepsMessageTokenMockMvc.perform(put("/api/preps-message-tokens")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepsMessageTokenDTO)))
            .andExpect(status().isOk());

        // Validate the PrepsMessageToken in the database
        List<PrepsMessageToken> prepsMessageTokenList = prepsMessageTokenRepository.findAll();
        assertThat(prepsMessageTokenList).hasSize(databaseSizeBeforeUpdate);
        PrepsMessageToken testPrepsMessageToken = prepsMessageTokenList.get(prepsMessageTokenList.size() - 1);
        assertThat(testPrepsMessageToken.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPrepsMessageToken.getTimeSent()).isEqualTo(UPDATED_TIME_SENT);
        assertThat(testPrepsMessageToken.getTokenValue()).isEqualTo(UPDATED_TOKEN_VALUE);
        assertThat(testPrepsMessageToken.isReceived()).isEqualTo(UPDATED_RECEIVED);
        assertThat(testPrepsMessageToken.isActioned()).isEqualTo(UPDATED_ACTIONED);
        assertThat(testPrepsMessageToken.isContentFullyEnqueued()).isEqualTo(UPDATED_CONTENT_FULLY_ENQUEUED);

        // Validate the PrepsMessageToken in Elasticsearch
        verify(mockPrepsMessageTokenSearchRepository, times(1)).save(testPrepsMessageToken);
    }

    @Test
    @Transactional
    public void updateNonExistingPrepsMessageToken() throws Exception {
        int databaseSizeBeforeUpdate = prepsMessageTokenRepository.findAll().size();

        // Create the PrepsMessageToken
        PrepsMessageTokenDTO prepsMessageTokenDTO = prepsMessageTokenMapper.toDto(prepsMessageToken);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPrepsMessageTokenMockMvc.perform(put("/api/preps-message-tokens")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(prepsMessageTokenDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PrepsMessageToken in the database
        List<PrepsMessageToken> prepsMessageTokenList = prepsMessageTokenRepository.findAll();
        assertThat(prepsMessageTokenList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PrepsMessageToken in Elasticsearch
        verify(mockPrepsMessageTokenSearchRepository, times(0)).save(prepsMessageToken);
    }

    @Test
    @Transactional
    public void deletePrepsMessageToken() throws Exception {
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);

        int databaseSizeBeforeDelete = prepsMessageTokenRepository.findAll().size();

        // Delete the prepsMessageToken
        restPrepsMessageTokenMockMvc.perform(delete("/api/preps-message-tokens/{id}", prepsMessageToken.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PrepsMessageToken> prepsMessageTokenList = prepsMessageTokenRepository.findAll();
        assertThat(prepsMessageTokenList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the PrepsMessageToken in Elasticsearch
        verify(mockPrepsMessageTokenSearchRepository, times(1)).deleteById(prepsMessageToken.getId());
    }

    @Test
    @Transactional
    public void searchPrepsMessageToken() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        prepsMessageTokenRepository.saveAndFlush(prepsMessageToken);
        when(mockPrepsMessageTokenSearchRepository.search(queryStringQuery("id:" + prepsMessageToken.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(prepsMessageToken), PageRequest.of(0, 1), 1));

        // Search the prepsMessageToken
        restPrepsMessageTokenMockMvc.perform(get("/api/_search/preps-message-tokens?query=id:" + prepsMessageToken.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prepsMessageToken.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].timeSent").value(hasItem(DEFAULT_TIME_SENT.intValue())))
            .andExpect(jsonPath("$.[*].tokenValue").value(hasItem(DEFAULT_TOKEN_VALUE)))
            .andExpect(jsonPath("$.[*].received").value(hasItem(DEFAULT_RECEIVED.booleanValue())))
            .andExpect(jsonPath("$.[*].actioned").value(hasItem(DEFAULT_ACTIONED.booleanValue())))
            .andExpect(jsonPath("$.[*].contentFullyEnqueued").value(hasItem(DEFAULT_CONTENT_FULLY_ENQUEUED.booleanValue())));
    }
}
