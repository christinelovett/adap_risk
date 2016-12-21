package com.innvo.web.rest;

import com.innvo.AdapRiskApp;
import com.innvo.domain.Countermeasure;
import com.innvo.repository.CountermeasureRepository;
import com.innvo.repository.search.CountermeasureSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CountermeasureResource REST controller.
 *
 * @see CountermeasureResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdapRiskApp.class)
public class CountermeasureResourceIntTest {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));
    private static final String DEFAULT_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_STATUS = "AAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_LASTMODIFIEDBY = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_LASTMODIFIEDBY = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_LASTMODIFIEDDATETIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_LASTMODIFIEDDATETIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_LASTMODIFIEDDATETIME_STR = dateTimeFormatter.format(DEFAULT_LASTMODIFIEDDATETIME);
    private static final String DEFAULT_DOMAIN = "AAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DOMAIN = "BBBBBBBBBBBBBBBBBBBBBBBBB";

    @Inject
    private CountermeasureRepository countermeasureRepository;

    @Inject
    private CountermeasureSearchRepository countermeasureSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restCountermeasureMockMvc;

    private Countermeasure countermeasure;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CountermeasureResource countermeasureResource = new CountermeasureResource();
        ReflectionTestUtils.setField(countermeasureResource, "countermeasureSearchRepository", countermeasureSearchRepository);
        ReflectionTestUtils.setField(countermeasureResource, "countermeasureRepository", countermeasureRepository);
        this.restCountermeasureMockMvc = MockMvcBuilders.standaloneSetup(countermeasureResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Countermeasure createEntity(EntityManager em) {
        Countermeasure countermeasure = new Countermeasure();
        countermeasure = new Countermeasure();
        countermeasure.setName(DEFAULT_NAME);
        countermeasure.setStatus(DEFAULT_STATUS);
        countermeasure.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        countermeasure.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        countermeasure.setDomain(DEFAULT_DOMAIN);
        return countermeasure;
    }

    @Before
    public void initTest() {
        countermeasureSearchRepository.deleteAll();
        countermeasure = createEntity(em);
    }

    @Test
    @Transactional
    public void createCountermeasure() throws Exception {
        int databaseSizeBeforeCreate = countermeasureRepository.findAll().size();

        // Create the Countermeasure

        restCountermeasureMockMvc.perform(post("/api/countermeasures")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(countermeasure)))
                .andExpect(status().isCreated());

        // Validate the Countermeasure in the database
        List<Countermeasure> countermeasures = countermeasureRepository.findAll();
        assertThat(countermeasures).hasSize(databaseSizeBeforeCreate + 1);
        Countermeasure testCountermeasure = countermeasures.get(countermeasures.size() - 1);
        assertThat(testCountermeasure.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCountermeasure.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testCountermeasure.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testCountermeasure.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testCountermeasure.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Countermeasure in ElasticSearch
        Countermeasure countermeasureEs = countermeasureSearchRepository.findOne(testCountermeasure.getId());
        assertThat(countermeasureEs).isEqualToComparingFieldByField(testCountermeasure);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = countermeasureRepository.findAll().size();
        // set the field null
        countermeasure.setName(null);

        // Create the Countermeasure, which fails.

        restCountermeasureMockMvc.perform(post("/api/countermeasures")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(countermeasure)))
                .andExpect(status().isBadRequest());

        List<Countermeasure> countermeasures = countermeasureRepository.findAll();
        assertThat(countermeasures).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = countermeasureRepository.findAll().size();
        // set the field null
        countermeasure.setStatus(null);

        // Create the Countermeasure, which fails.

        restCountermeasureMockMvc.perform(post("/api/countermeasures")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(countermeasure)))
                .andExpect(status().isBadRequest());

        List<Countermeasure> countermeasures = countermeasureRepository.findAll();
        assertThat(countermeasures).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = countermeasureRepository.findAll().size();
        // set the field null
        countermeasure.setLastmodifiedby(null);

        // Create the Countermeasure, which fails.

        restCountermeasureMockMvc.perform(post("/api/countermeasures")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(countermeasure)))
                .andExpect(status().isBadRequest());

        List<Countermeasure> countermeasures = countermeasureRepository.findAll();
        assertThat(countermeasures).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = countermeasureRepository.findAll().size();
        // set the field null
        countermeasure.setLastmodifieddatetime(null);

        // Create the Countermeasure, which fails.

        restCountermeasureMockMvc.perform(post("/api/countermeasures")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(countermeasure)))
                .andExpect(status().isBadRequest());

        List<Countermeasure> countermeasures = countermeasureRepository.findAll();
        assertThat(countermeasures).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = countermeasureRepository.findAll().size();
        // set the field null
        countermeasure.setDomain(null);

        // Create the Countermeasure, which fails.

        restCountermeasureMockMvc.perform(post("/api/countermeasures")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(countermeasure)))
                .andExpect(status().isBadRequest());

        List<Countermeasure> countermeasures = countermeasureRepository.findAll();
        assertThat(countermeasures).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCountermeasures() throws Exception {
        // Initialize the database
        countermeasureRepository.saveAndFlush(countermeasure);

        // Get all the countermeasures
        restCountermeasureMockMvc.perform(get("/api/countermeasures?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(countermeasure.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getCountermeasure() throws Exception {
        // Initialize the database
        countermeasureRepository.saveAndFlush(countermeasure);

        // Get the countermeasure
        restCountermeasureMockMvc.perform(get("/api/countermeasures/{id}", countermeasure.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(countermeasure.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCountermeasure() throws Exception {
        // Get the countermeasure
        restCountermeasureMockMvc.perform(get("/api/countermeasures/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCountermeasure() throws Exception {
        // Initialize the database
        countermeasureRepository.saveAndFlush(countermeasure);
        countermeasureSearchRepository.save(countermeasure);
        int databaseSizeBeforeUpdate = countermeasureRepository.findAll().size();

        // Update the countermeasure
        Countermeasure updatedCountermeasure = countermeasureRepository.findOne(countermeasure.getId());
        updatedCountermeasure.setName(UPDATED_NAME);
        updatedCountermeasure.setStatus(UPDATED_STATUS);
        updatedCountermeasure.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedCountermeasure.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedCountermeasure.setDomain(UPDATED_DOMAIN);

        restCountermeasureMockMvc.perform(put("/api/countermeasures")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedCountermeasure)))
                .andExpect(status().isOk());

        // Validate the Countermeasure in the database
        List<Countermeasure> countermeasures = countermeasureRepository.findAll();
        assertThat(countermeasures).hasSize(databaseSizeBeforeUpdate);
        Countermeasure testCountermeasure = countermeasures.get(countermeasures.size() - 1);
        assertThat(testCountermeasure.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCountermeasure.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testCountermeasure.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testCountermeasure.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testCountermeasure.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Countermeasure in ElasticSearch
        Countermeasure countermeasureEs = countermeasureSearchRepository.findOne(testCountermeasure.getId());
        assertThat(countermeasureEs).isEqualToComparingFieldByField(testCountermeasure);
    }

    @Test
    @Transactional
    public void deleteCountermeasure() throws Exception {
        // Initialize the database
        countermeasureRepository.saveAndFlush(countermeasure);
        countermeasureSearchRepository.save(countermeasure);
        int databaseSizeBeforeDelete = countermeasureRepository.findAll().size();

        // Get the countermeasure
        restCountermeasureMockMvc.perform(delete("/api/countermeasures/{id}", countermeasure.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean countermeasureExistsInEs = countermeasureSearchRepository.exists(countermeasure.getId());
        assertThat(countermeasureExistsInEs).isFalse();

        // Validate the database is empty
        List<Countermeasure> countermeasures = countermeasureRepository.findAll();
        assertThat(countermeasures).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchCountermeasure() throws Exception {
        // Initialize the database
        countermeasureRepository.saveAndFlush(countermeasure);
        countermeasureSearchRepository.save(countermeasure);

        // Search the countermeasure
        restCountermeasureMockMvc.perform(get("/api/_search/countermeasures?query=id:" + countermeasure.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(countermeasure.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}
