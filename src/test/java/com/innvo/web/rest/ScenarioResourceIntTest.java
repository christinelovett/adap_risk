package com.innvo.web.rest;

import com.innvo.AdapRiskApp;
import com.innvo.domain.Scenario;
import com.innvo.repository.ScenarioRepository;
import com.innvo.repository.search.ScenarioSearchRepository;

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
 * Test class for the ScenarioResource REST controller.
 *
 * @see ScenarioResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdapRiskApp.class)
public class ScenarioResourceIntTest {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));
    private static final String DEFAULT_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_NAMESHORT = "AAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAMESHORT = "BBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    private static final Boolean DEFAULT_ISABSTRACT = false;
    private static final Boolean UPDATED_ISABSTRACT = true;
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
    private ScenarioRepository scenarioRepository;

    @Inject
    private ScenarioSearchRepository scenarioSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restScenarioMockMvc;

    private Scenario scenario;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ScenarioResource scenarioResource = new ScenarioResource();
        ReflectionTestUtils.setField(scenarioResource, "scenarioSearchRepository", scenarioSearchRepository);
        ReflectionTestUtils.setField(scenarioResource, "scenarioRepository", scenarioRepository);
        this.restScenarioMockMvc = MockMvcBuilders.standaloneSetup(scenarioResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Scenario createEntity(EntityManager em) {
        Scenario scenario = new Scenario();
        scenario = new Scenario();
        scenario.setName(DEFAULT_NAME);
        scenario.setNameshort(DEFAULT_NAMESHORT);
        scenario.setDescription(DEFAULT_DESCRIPTION);
        scenario.setIsabstract(DEFAULT_ISABSTRACT);
        scenario.setStatus(DEFAULT_STATUS);
        scenario.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        scenario.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        scenario.setDomain(DEFAULT_DOMAIN);
        return scenario;
    }

    @Before
    public void initTest() {
        scenarioSearchRepository.deleteAll();
        scenario = createEntity(em);
    }

    @Test
    @Transactional
    public void createScenario() throws Exception {
        int databaseSizeBeforeCreate = scenarioRepository.findAll().size();

        // Create the Scenario

        restScenarioMockMvc.perform(post("/api/scenarios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(scenario)))
                .andExpect(status().isCreated());

        // Validate the Scenario in the database
        List<Scenario> scenarios = scenarioRepository.findAll();
        assertThat(scenarios).hasSize(databaseSizeBeforeCreate + 1);
        Scenario testScenario = scenarios.get(scenarios.size() - 1);
        assertThat(testScenario.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testScenario.getNameshort()).isEqualTo(DEFAULT_NAMESHORT);
        assertThat(testScenario.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testScenario.isIsabstract()).isEqualTo(DEFAULT_ISABSTRACT);
        assertThat(testScenario.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testScenario.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testScenario.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testScenario.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Scenario in ElasticSearch
        Scenario scenarioEs = scenarioSearchRepository.findOne(testScenario.getId());
        assertThat(scenarioEs).isEqualToComparingFieldByField(testScenario);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = scenarioRepository.findAll().size();
        // set the field null
        scenario.setName(null);

        // Create the Scenario, which fails.

        restScenarioMockMvc.perform(post("/api/scenarios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(scenario)))
                .andExpect(status().isBadRequest());

        List<Scenario> scenarios = scenarioRepository.findAll();
        assertThat(scenarios).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameshortIsRequired() throws Exception {
        int databaseSizeBeforeTest = scenarioRepository.findAll().size();
        // set the field null
        scenario.setNameshort(null);

        // Create the Scenario, which fails.

        restScenarioMockMvc.perform(post("/api/scenarios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(scenario)))
                .andExpect(status().isBadRequest());

        List<Scenario> scenarios = scenarioRepository.findAll();
        assertThat(scenarios).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = scenarioRepository.findAll().size();
        // set the field null
        scenario.setStatus(null);

        // Create the Scenario, which fails.

        restScenarioMockMvc.perform(post("/api/scenarios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(scenario)))
                .andExpect(status().isBadRequest());

        List<Scenario> scenarios = scenarioRepository.findAll();
        assertThat(scenarios).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = scenarioRepository.findAll().size();
        // set the field null
        scenario.setLastmodifiedby(null);

        // Create the Scenario, which fails.

        restScenarioMockMvc.perform(post("/api/scenarios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(scenario)))
                .andExpect(status().isBadRequest());

        List<Scenario> scenarios = scenarioRepository.findAll();
        assertThat(scenarios).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = scenarioRepository.findAll().size();
        // set the field null
        scenario.setLastmodifieddatetime(null);

        // Create the Scenario, which fails.

        restScenarioMockMvc.perform(post("/api/scenarios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(scenario)))
                .andExpect(status().isBadRequest());

        List<Scenario> scenarios = scenarioRepository.findAll();
        assertThat(scenarios).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = scenarioRepository.findAll().size();
        // set the field null
        scenario.setDomain(null);

        // Create the Scenario, which fails.

        restScenarioMockMvc.perform(post("/api/scenarios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(scenario)))
                .andExpect(status().isBadRequest());

        List<Scenario> scenarios = scenarioRepository.findAll();
        assertThat(scenarios).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllScenarios() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get all the scenarios
        restScenarioMockMvc.perform(get("/api/scenarios?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(scenario.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].nameshort").value(hasItem(DEFAULT_NAMESHORT.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].isabstract").value(hasItem(DEFAULT_ISABSTRACT.booleanValue())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getScenario() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);

        // Get the scenario
        restScenarioMockMvc.perform(get("/api/scenarios/{id}", scenario.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(scenario.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.nameshort").value(DEFAULT_NAMESHORT.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.isabstract").value(DEFAULT_ISABSTRACT.booleanValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingScenario() throws Exception {
        // Get the scenario
        restScenarioMockMvc.perform(get("/api/scenarios/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateScenario() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);
        scenarioSearchRepository.save(scenario);
        int databaseSizeBeforeUpdate = scenarioRepository.findAll().size();

        // Update the scenario
        Scenario updatedScenario = scenarioRepository.findOne(scenario.getId());
        updatedScenario.setName(UPDATED_NAME);
        updatedScenario.setNameshort(UPDATED_NAMESHORT);
        updatedScenario.setDescription(UPDATED_DESCRIPTION);
        updatedScenario.setIsabstract(UPDATED_ISABSTRACT);
        updatedScenario.setStatus(UPDATED_STATUS);
        updatedScenario.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedScenario.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedScenario.setDomain(UPDATED_DOMAIN);

        restScenarioMockMvc.perform(put("/api/scenarios")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedScenario)))
                .andExpect(status().isOk());

        // Validate the Scenario in the database
        List<Scenario> scenarios = scenarioRepository.findAll();
        assertThat(scenarios).hasSize(databaseSizeBeforeUpdate);
        Scenario testScenario = scenarios.get(scenarios.size() - 1);
        assertThat(testScenario.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testScenario.getNameshort()).isEqualTo(UPDATED_NAMESHORT);
        assertThat(testScenario.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testScenario.isIsabstract()).isEqualTo(UPDATED_ISABSTRACT);
        assertThat(testScenario.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testScenario.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testScenario.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testScenario.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Scenario in ElasticSearch
        Scenario scenarioEs = scenarioSearchRepository.findOne(testScenario.getId());
        assertThat(scenarioEs).isEqualToComparingFieldByField(testScenario);
    }

    @Test
    @Transactional
    public void deleteScenario() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);
        scenarioSearchRepository.save(scenario);
        int databaseSizeBeforeDelete = scenarioRepository.findAll().size();

        // Get the scenario
        restScenarioMockMvc.perform(delete("/api/scenarios/{id}", scenario.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean scenarioExistsInEs = scenarioSearchRepository.exists(scenario.getId());
        assertThat(scenarioExistsInEs).isFalse();

        // Validate the database is empty
        List<Scenario> scenarios = scenarioRepository.findAll();
        assertThat(scenarios).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchScenario() throws Exception {
        // Initialize the database
        scenarioRepository.saveAndFlush(scenario);
        scenarioSearchRepository.save(scenario);

        // Search the scenario
        restScenarioMockMvc.perform(get("/api/_search/scenarios?query=id:" + scenario.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scenario.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].nameshort").value(hasItem(DEFAULT_NAMESHORT.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].isabstract").value(hasItem(DEFAULT_ISABSTRACT.booleanValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}
