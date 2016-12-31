package com.innvo.web.rest;

import com.innvo.AdapRiskApp;
import com.innvo.domain.Countermeasurefactortype;
import com.innvo.repository.CountermeasurefactortypeRepository;
import com.innvo.repository.search.CountermeasurefactortypeSearchRepository;

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
 * Test class for the CountermeasurefactortypeResource REST controller.
 *
 * @see CountermeasurefactortypeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdapRiskApp.class)
public class CountermeasurefactortypeResourceIntTest {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));
    private static final String DEFAULT_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_NAMESHORT = "AAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAMESHORT = "BBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
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
    private CountermeasurefactortypeRepository countermeasurefactortypeRepository;

    @Inject
    private CountermeasurefactortypeSearchRepository countermeasurefactortypeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restCountermeasurefactortypeMockMvc;

    private Countermeasurefactortype countermeasurefactortype;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CountermeasurefactortypeResource countermeasurefactortypeResource = new CountermeasurefactortypeResource();
        ReflectionTestUtils.setField(countermeasurefactortypeResource, "countermeasurefactortypeSearchRepository", countermeasurefactortypeSearchRepository);
        ReflectionTestUtils.setField(countermeasurefactortypeResource, "countermeasurefactortypeRepository", countermeasurefactortypeRepository);
        this.restCountermeasurefactortypeMockMvc = MockMvcBuilders.standaloneSetup(countermeasurefactortypeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Countermeasurefactortype createEntity(EntityManager em) {
        Countermeasurefactortype countermeasurefactortype = new Countermeasurefactortype();
        countermeasurefactortype = new Countermeasurefactortype()
                .name(DEFAULT_NAME)
                .nameshort(DEFAULT_NAMESHORT)
                .description(DEFAULT_DESCRIPTION)
                .status(DEFAULT_STATUS)
                .lastmodifiedby(DEFAULT_LASTMODIFIEDBY)
                .lastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME)
                .domain(DEFAULT_DOMAIN);
        return countermeasurefactortype;
    }

    @Before
    public void initTest() {
        countermeasurefactortypeSearchRepository.deleteAll();
        countermeasurefactortype = createEntity(em);
    }

    @Test
    @Transactional
    public void createCountermeasurefactortype() throws Exception {
        int databaseSizeBeforeCreate = countermeasurefactortypeRepository.findAll().size();

        // Create the Countermeasurefactortype

        restCountermeasurefactortypeMockMvc.perform(post("/api/countermeasurefactortypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(countermeasurefactortype)))
                .andExpect(status().isCreated());

        // Validate the Countermeasurefactortype in the database
        List<Countermeasurefactortype> countermeasurefactortypes = countermeasurefactortypeRepository.findAll();
        assertThat(countermeasurefactortypes).hasSize(databaseSizeBeforeCreate + 1);
        Countermeasurefactortype testCountermeasurefactortype = countermeasurefactortypes.get(countermeasurefactortypes.size() - 1);
        assertThat(testCountermeasurefactortype.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCountermeasurefactortype.getNameshort()).isEqualTo(DEFAULT_NAMESHORT);
        assertThat(testCountermeasurefactortype.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCountermeasurefactortype.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testCountermeasurefactortype.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testCountermeasurefactortype.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testCountermeasurefactortype.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Countermeasurefactortype in ElasticSearch
        Countermeasurefactortype countermeasurefactortypeEs = countermeasurefactortypeSearchRepository.findOne(testCountermeasurefactortype.getId());
        assertThat(countermeasurefactortypeEs).isEqualToComparingFieldByField(testCountermeasurefactortype);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = countermeasurefactortypeRepository.findAll().size();
        // set the field null
        countermeasurefactortype.setName(null);

        // Create the Countermeasurefactortype, which fails.

        restCountermeasurefactortypeMockMvc.perform(post("/api/countermeasurefactortypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(countermeasurefactortype)))
                .andExpect(status().isBadRequest());

        List<Countermeasurefactortype> countermeasurefactortypes = countermeasurefactortypeRepository.findAll();
        assertThat(countermeasurefactortypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameshortIsRequired() throws Exception {
        int databaseSizeBeforeTest = countermeasurefactortypeRepository.findAll().size();
        // set the field null
        countermeasurefactortype.setNameshort(null);

        // Create the Countermeasurefactortype, which fails.

        restCountermeasurefactortypeMockMvc.perform(post("/api/countermeasurefactortypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(countermeasurefactortype)))
                .andExpect(status().isBadRequest());

        List<Countermeasurefactortype> countermeasurefactortypes = countermeasurefactortypeRepository.findAll();
        assertThat(countermeasurefactortypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = countermeasurefactortypeRepository.findAll().size();
        // set the field null
        countermeasurefactortype.setStatus(null);

        // Create the Countermeasurefactortype, which fails.

        restCountermeasurefactortypeMockMvc.perform(post("/api/countermeasurefactortypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(countermeasurefactortype)))
                .andExpect(status().isBadRequest());

        List<Countermeasurefactortype> countermeasurefactortypes = countermeasurefactortypeRepository.findAll();
        assertThat(countermeasurefactortypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = countermeasurefactortypeRepository.findAll().size();
        // set the field null
        countermeasurefactortype.setLastmodifiedby(null);

        // Create the Countermeasurefactortype, which fails.

        restCountermeasurefactortypeMockMvc.perform(post("/api/countermeasurefactortypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(countermeasurefactortype)))
                .andExpect(status().isBadRequest());

        List<Countermeasurefactortype> countermeasurefactortypes = countermeasurefactortypeRepository.findAll();
        assertThat(countermeasurefactortypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = countermeasurefactortypeRepository.findAll().size();
        // set the field null
        countermeasurefactortype.setLastmodifieddatetime(null);

        // Create the Countermeasurefactortype, which fails.

        restCountermeasurefactortypeMockMvc.perform(post("/api/countermeasurefactortypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(countermeasurefactortype)))
                .andExpect(status().isBadRequest());

        List<Countermeasurefactortype> countermeasurefactortypes = countermeasurefactortypeRepository.findAll();
        assertThat(countermeasurefactortypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = countermeasurefactortypeRepository.findAll().size();
        // set the field null
        countermeasurefactortype.setDomain(null);

        // Create the Countermeasurefactortype, which fails.

        restCountermeasurefactortypeMockMvc.perform(post("/api/countermeasurefactortypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(countermeasurefactortype)))
                .andExpect(status().isBadRequest());

        List<Countermeasurefactortype> countermeasurefactortypes = countermeasurefactortypeRepository.findAll();
        assertThat(countermeasurefactortypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCountermeasurefactortypes() throws Exception {
        // Initialize the database
        countermeasurefactortypeRepository.saveAndFlush(countermeasurefactortype);

        // Get all the countermeasurefactortypes
        restCountermeasurefactortypeMockMvc.perform(get("/api/countermeasurefactortypes?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(countermeasurefactortype.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].nameshort").value(hasItem(DEFAULT_NAMESHORT.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getCountermeasurefactortype() throws Exception {
        // Initialize the database
        countermeasurefactortypeRepository.saveAndFlush(countermeasurefactortype);

        // Get the countermeasurefactortype
        restCountermeasurefactortypeMockMvc.perform(get("/api/countermeasurefactortypes/{id}", countermeasurefactortype.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(countermeasurefactortype.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.nameshort").value(DEFAULT_NAMESHORT.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCountermeasurefactortype() throws Exception {
        // Get the countermeasurefactortype
        restCountermeasurefactortypeMockMvc.perform(get("/api/countermeasurefactortypes/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCountermeasurefactortype() throws Exception {
        // Initialize the database
        countermeasurefactortypeRepository.saveAndFlush(countermeasurefactortype);
        countermeasurefactortypeSearchRepository.save(countermeasurefactortype);
        int databaseSizeBeforeUpdate = countermeasurefactortypeRepository.findAll().size();

        // Update the countermeasurefactortype
        Countermeasurefactortype updatedCountermeasurefactortype = countermeasurefactortypeRepository.findOne(countermeasurefactortype.getId());
        updatedCountermeasurefactortype
                .name(UPDATED_NAME)
                .nameshort(UPDATED_NAMESHORT)
                .description(UPDATED_DESCRIPTION)
                .status(UPDATED_STATUS)
                .lastmodifiedby(UPDATED_LASTMODIFIEDBY)
                .lastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME)
                .domain(UPDATED_DOMAIN);

        restCountermeasurefactortypeMockMvc.perform(put("/api/countermeasurefactortypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedCountermeasurefactortype)))
                .andExpect(status().isOk());

        // Validate the Countermeasurefactortype in the database
        List<Countermeasurefactortype> countermeasurefactortypes = countermeasurefactortypeRepository.findAll();
        assertThat(countermeasurefactortypes).hasSize(databaseSizeBeforeUpdate);
        Countermeasurefactortype testCountermeasurefactortype = countermeasurefactortypes.get(countermeasurefactortypes.size() - 1);
        assertThat(testCountermeasurefactortype.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCountermeasurefactortype.getNameshort()).isEqualTo(UPDATED_NAMESHORT);
        assertThat(testCountermeasurefactortype.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCountermeasurefactortype.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testCountermeasurefactortype.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testCountermeasurefactortype.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testCountermeasurefactortype.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Countermeasurefactortype in ElasticSearch
        Countermeasurefactortype countermeasurefactortypeEs = countermeasurefactortypeSearchRepository.findOne(testCountermeasurefactortype.getId());
        assertThat(countermeasurefactortypeEs).isEqualToComparingFieldByField(testCountermeasurefactortype);
    }

    @Test
    @Transactional
    public void deleteCountermeasurefactortype() throws Exception {
        // Initialize the database
        countermeasurefactortypeRepository.saveAndFlush(countermeasurefactortype);
        countermeasurefactortypeSearchRepository.save(countermeasurefactortype);
        int databaseSizeBeforeDelete = countermeasurefactortypeRepository.findAll().size();

        // Get the countermeasurefactortype
        restCountermeasurefactortypeMockMvc.perform(delete("/api/countermeasurefactortypes/{id}", countermeasurefactortype.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean countermeasurefactortypeExistsInEs = countermeasurefactortypeSearchRepository.exists(countermeasurefactortype.getId());
        assertThat(countermeasurefactortypeExistsInEs).isFalse();

        // Validate the database is empty
        List<Countermeasurefactortype> countermeasurefactortypes = countermeasurefactortypeRepository.findAll();
        assertThat(countermeasurefactortypes).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchCountermeasurefactortype() throws Exception {
        // Initialize the database
        countermeasurefactortypeRepository.saveAndFlush(countermeasurefactortype);
        countermeasurefactortypeSearchRepository.save(countermeasurefactortype);

        // Search the countermeasurefactortype
        restCountermeasurefactortypeMockMvc.perform(get("/api/_search/countermeasurefactortypes?query=id:" + countermeasurefactortype.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(countermeasurefactortype.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].nameshort").value(hasItem(DEFAULT_NAMESHORT.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}
