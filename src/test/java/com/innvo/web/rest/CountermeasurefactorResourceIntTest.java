package com.innvo.web.rest;

import com.innvo.AdapRiskApp;
import com.innvo.domain.Countermeasurefactor;
import com.innvo.domain.Countermeasure;
import com.innvo.domain.Pathway;
import com.innvo.domain.Countermeasurefactortype;
import com.innvo.repository.CountermeasurefactorRepository;
import com.innvo.repository.search.CountermeasurefactorSearchRepository;

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
 * Test class for the CountermeasurefactorResource REST controller.
 *
 * @see CountermeasurefactorResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdapRiskApp.class)
public class CountermeasurefactorResourceIntTest {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));
    private static final String DEFAULT_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_NAMESHORT = "AAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAMESHORT = "BBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_VERSION = "AAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_VERSION = "BBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_VALUE = "AAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_COMMENT = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
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
    private CountermeasurefactorRepository countermeasurefactorRepository;

    @Inject
    private CountermeasurefactorSearchRepository countermeasurefactorSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restCountermeasurefactorMockMvc;

    private Countermeasurefactor countermeasurefactor;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CountermeasurefactorResource countermeasurefactorResource = new CountermeasurefactorResource();
        ReflectionTestUtils.setField(countermeasurefactorResource, "countermeasurefactorSearchRepository", countermeasurefactorSearchRepository);
        ReflectionTestUtils.setField(countermeasurefactorResource, "countermeasurefactorRepository", countermeasurefactorRepository);
        this.restCountermeasurefactorMockMvc = MockMvcBuilders.standaloneSetup(countermeasurefactorResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Countermeasurefactor createEntity(EntityManager em) {
        Countermeasurefactor countermeasurefactor = new Countermeasurefactor();
        countermeasurefactor = new Countermeasurefactor();
        countermeasurefactor.setName(DEFAULT_NAME);
        countermeasurefactor.setNameshort(DEFAULT_NAMESHORT);
        countermeasurefactor.setDescription(DEFAULT_DESCRIPTION);
        countermeasurefactor.setVersion(DEFAULT_VERSION);
        countermeasurefactor.setValue(DEFAULT_VALUE);
        countermeasurefactor.setComment(DEFAULT_COMMENT);
        countermeasurefactor.setStatus(DEFAULT_STATUS);
        countermeasurefactor.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        countermeasurefactor.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        countermeasurefactor.setDomain(DEFAULT_DOMAIN);
        // Add required entity
        Countermeasure countermeasure = CountermeasureResourceIntTest.createEntity(em);
        em.persist(countermeasure);
        em.flush();
        countermeasurefactor.setCountermeasure(countermeasure);
        // Add required entity
        Pathway pathway = PathwayResourceIntTest.createEntity(em);
        em.persist(pathway);
        em.flush();
        countermeasurefactor.setPathway(pathway);
        // Add required entity
        Countermeasurefactortype countermeasurefactortype = CountermeasurefactortypeResourceIntTest.createEntity(em);
        em.persist(countermeasurefactortype);
        em.flush();
        countermeasurefactor.setCountermeasurefactortype(countermeasurefactortype);
        return countermeasurefactor;
    }

    @Before
    public void initTest() {
        countermeasurefactorSearchRepository.deleteAll();
        countermeasurefactor = createEntity(em);
    }

    @Test
    @Transactional
    public void createCountermeasurefactor() throws Exception {
        int databaseSizeBeforeCreate = countermeasurefactorRepository.findAll().size();

        // Create the Countermeasurefactor

        restCountermeasurefactorMockMvc.perform(post("/api/countermeasurefactors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(countermeasurefactor)))
                .andExpect(status().isCreated());

        // Validate the Countermeasurefactor in the database
        List<Countermeasurefactor> countermeasurefactors = countermeasurefactorRepository.findAll();
        assertThat(countermeasurefactors).hasSize(databaseSizeBeforeCreate + 1);
        Countermeasurefactor testCountermeasurefactor = countermeasurefactors.get(countermeasurefactors.size() - 1);
        assertThat(testCountermeasurefactor.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCountermeasurefactor.getNameshort()).isEqualTo(DEFAULT_NAMESHORT);
        assertThat(testCountermeasurefactor.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCountermeasurefactor.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testCountermeasurefactor.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testCountermeasurefactor.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testCountermeasurefactor.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testCountermeasurefactor.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testCountermeasurefactor.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testCountermeasurefactor.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Countermeasurefactor in ElasticSearch
        Countermeasurefactor countermeasurefactorEs = countermeasurefactorSearchRepository.findOne(testCountermeasurefactor.getId());
        assertThat(countermeasurefactorEs).isEqualToComparingFieldByField(testCountermeasurefactor);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = countermeasurefactorRepository.findAll().size();
        // set the field null
        countermeasurefactor.setName(null);

        // Create the Countermeasurefactor, which fails.

        restCountermeasurefactorMockMvc.perform(post("/api/countermeasurefactors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(countermeasurefactor)))
                .andExpect(status().isBadRequest());

        List<Countermeasurefactor> countermeasurefactors = countermeasurefactorRepository.findAll();
        assertThat(countermeasurefactors).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameshortIsRequired() throws Exception {
        int databaseSizeBeforeTest = countermeasurefactorRepository.findAll().size();
        // set the field null
        countermeasurefactor.setNameshort(null);

        // Create the Countermeasurefactor, which fails.

        restCountermeasurefactorMockMvc.perform(post("/api/countermeasurefactors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(countermeasurefactor)))
                .andExpect(status().isBadRequest());

        List<Countermeasurefactor> countermeasurefactors = countermeasurefactorRepository.findAll();
        assertThat(countermeasurefactors).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = countermeasurefactorRepository.findAll().size();
        // set the field null
        countermeasurefactor.setValue(null);

        // Create the Countermeasurefactor, which fails.

        restCountermeasurefactorMockMvc.perform(post("/api/countermeasurefactors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(countermeasurefactor)))
                .andExpect(status().isBadRequest());

        List<Countermeasurefactor> countermeasurefactors = countermeasurefactorRepository.findAll();
        assertThat(countermeasurefactors).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = countermeasurefactorRepository.findAll().size();
        // set the field null
        countermeasurefactor.setStatus(null);

        // Create the Countermeasurefactor, which fails.

        restCountermeasurefactorMockMvc.perform(post("/api/countermeasurefactors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(countermeasurefactor)))
                .andExpect(status().isBadRequest());

        List<Countermeasurefactor> countermeasurefactors = countermeasurefactorRepository.findAll();
        assertThat(countermeasurefactors).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = countermeasurefactorRepository.findAll().size();
        // set the field null
        countermeasurefactor.setLastmodifiedby(null);

        // Create the Countermeasurefactor, which fails.

        restCountermeasurefactorMockMvc.perform(post("/api/countermeasurefactors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(countermeasurefactor)))
                .andExpect(status().isBadRequest());

        List<Countermeasurefactor> countermeasurefactors = countermeasurefactorRepository.findAll();
        assertThat(countermeasurefactors).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = countermeasurefactorRepository.findAll().size();
        // set the field null
        countermeasurefactor.setLastmodifieddatetime(null);

        // Create the Countermeasurefactor, which fails.

        restCountermeasurefactorMockMvc.perform(post("/api/countermeasurefactors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(countermeasurefactor)))
                .andExpect(status().isBadRequest());

        List<Countermeasurefactor> countermeasurefactors = countermeasurefactorRepository.findAll();
        assertThat(countermeasurefactors).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = countermeasurefactorRepository.findAll().size();
        // set the field null
        countermeasurefactor.setDomain(null);

        // Create the Countermeasurefactor, which fails.

        restCountermeasurefactorMockMvc.perform(post("/api/countermeasurefactors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(countermeasurefactor)))
                .andExpect(status().isBadRequest());

        List<Countermeasurefactor> countermeasurefactors = countermeasurefactorRepository.findAll();
        assertThat(countermeasurefactors).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCountermeasurefactors() throws Exception {
        // Initialize the database
        countermeasurefactorRepository.saveAndFlush(countermeasurefactor);

        // Get all the countermeasurefactors
        restCountermeasurefactorMockMvc.perform(get("/api/countermeasurefactors?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(countermeasurefactor.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].nameshort").value(hasItem(DEFAULT_NAMESHORT.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.toString())))
                .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getCountermeasurefactor() throws Exception {
        // Initialize the database
        countermeasurefactorRepository.saveAndFlush(countermeasurefactor);

        // Get the countermeasurefactor
        restCountermeasurefactorMockMvc.perform(get("/api/countermeasurefactors/{id}", countermeasurefactor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(countermeasurefactor.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.nameshort").value(DEFAULT_NAMESHORT.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION.toString()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCountermeasurefactor() throws Exception {
        // Get the countermeasurefactor
        restCountermeasurefactorMockMvc.perform(get("/api/countermeasurefactors/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCountermeasurefactor() throws Exception {
        // Initialize the database
        countermeasurefactorRepository.saveAndFlush(countermeasurefactor);
        countermeasurefactorSearchRepository.save(countermeasurefactor);
        int databaseSizeBeforeUpdate = countermeasurefactorRepository.findAll().size();

        // Update the countermeasurefactor
        Countermeasurefactor updatedCountermeasurefactor = countermeasurefactorRepository.findOne(countermeasurefactor.getId());
        updatedCountermeasurefactor.setName(UPDATED_NAME);
        updatedCountermeasurefactor.setNameshort(UPDATED_NAMESHORT);
        updatedCountermeasurefactor.setDescription(UPDATED_DESCRIPTION);
        updatedCountermeasurefactor.setVersion(UPDATED_VERSION);
        updatedCountermeasurefactor.setValue(UPDATED_VALUE);
        updatedCountermeasurefactor.setComment(UPDATED_COMMENT);
        updatedCountermeasurefactor.setStatus(UPDATED_STATUS);
        updatedCountermeasurefactor.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedCountermeasurefactor.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedCountermeasurefactor.setDomain(UPDATED_DOMAIN);

        restCountermeasurefactorMockMvc.perform(put("/api/countermeasurefactors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedCountermeasurefactor)))
                .andExpect(status().isOk());

        // Validate the Countermeasurefactor in the database
        List<Countermeasurefactor> countermeasurefactors = countermeasurefactorRepository.findAll();
        assertThat(countermeasurefactors).hasSize(databaseSizeBeforeUpdate);
        Countermeasurefactor testCountermeasurefactor = countermeasurefactors.get(countermeasurefactors.size() - 1);
        assertThat(testCountermeasurefactor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCountermeasurefactor.getNameshort()).isEqualTo(UPDATED_NAMESHORT);
        assertThat(testCountermeasurefactor.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCountermeasurefactor.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testCountermeasurefactor.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testCountermeasurefactor.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testCountermeasurefactor.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testCountermeasurefactor.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testCountermeasurefactor.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testCountermeasurefactor.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Countermeasurefactor in ElasticSearch
        Countermeasurefactor countermeasurefactorEs = countermeasurefactorSearchRepository.findOne(testCountermeasurefactor.getId());
        assertThat(countermeasurefactorEs).isEqualToComparingFieldByField(testCountermeasurefactor);
    }

    @Test
    @Transactional
    public void deleteCountermeasurefactor() throws Exception {
        // Initialize the database
        countermeasurefactorRepository.saveAndFlush(countermeasurefactor);
        countermeasurefactorSearchRepository.save(countermeasurefactor);
        int databaseSizeBeforeDelete = countermeasurefactorRepository.findAll().size();

        // Get the countermeasurefactor
        restCountermeasurefactorMockMvc.perform(delete("/api/countermeasurefactors/{id}", countermeasurefactor.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean countermeasurefactorExistsInEs = countermeasurefactorSearchRepository.exists(countermeasurefactor.getId());
        assertThat(countermeasurefactorExistsInEs).isFalse();

        // Validate the database is empty
        List<Countermeasurefactor> countermeasurefactors = countermeasurefactorRepository.findAll();
        assertThat(countermeasurefactors).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchCountermeasurefactor() throws Exception {
        // Initialize the database
        countermeasurefactorRepository.saveAndFlush(countermeasurefactor);
        countermeasurefactorSearchRepository.save(countermeasurefactor);

        // Search the countermeasurefactor
        restCountermeasurefactorMockMvc.perform(get("/api/_search/countermeasurefactors?query=id:" + countermeasurefactor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(countermeasurefactor.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].nameshort").value(hasItem(DEFAULT_NAMESHORT.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}
