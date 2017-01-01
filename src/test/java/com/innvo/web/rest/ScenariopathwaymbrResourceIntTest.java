package com.innvo.web.rest;

import com.innvo.AdapRiskApp;
import com.innvo.domain.Scenariopathwaymbr;
import com.innvo.domain.Scenario;
import com.innvo.domain.Pathway;
import com.innvo.repository.ScenariopathwaymbrRepository;
import com.innvo.repository.search.ScenariopathwaymbrSearchRepository;

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
 * Test class for the ScenariopathwaymbrResource REST controller.
 *
 * @see ScenariopathwaymbrResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdapRiskApp.class)
public class ScenariopathwaymbrResourceIntTest {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));
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
    private ScenariopathwaymbrRepository scenariopathwaymbrRepository;

    @Inject
    private ScenariopathwaymbrSearchRepository scenariopathwaymbrSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restScenariopathwaymbrMockMvc;

    private Scenariopathwaymbr scenariopathwaymbr;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ScenariopathwaymbrResource scenariopathwaymbrResource = new ScenariopathwaymbrResource();
        ReflectionTestUtils.setField(scenariopathwaymbrResource, "scenariopathwaymbrSearchRepository", scenariopathwaymbrSearchRepository);
        ReflectionTestUtils.setField(scenariopathwaymbrResource, "scenariopathwaymbrRepository", scenariopathwaymbrRepository);
        this.restScenariopathwaymbrMockMvc = MockMvcBuilders.standaloneSetup(scenariopathwaymbrResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Scenariopathwaymbr createEntity(EntityManager em) {
        Scenariopathwaymbr scenariopathwaymbr = new Scenariopathwaymbr();
        scenariopathwaymbr = new Scenariopathwaymbr();
        scenariopathwaymbr.setComment(DEFAULT_COMMENT);
        scenariopathwaymbr.setStatus(DEFAULT_STATUS);
        scenariopathwaymbr.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        scenariopathwaymbr.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        scenariopathwaymbr.setDomain(DEFAULT_DOMAIN);
        // Add required entity
        Scenario scenario = ScenarioResourceIntTest.createEntity(em);
        em.persist(scenario);
        em.flush();
        scenariopathwaymbr.setScenario(scenario);
        // Add required entity
        Pathway pathway = PathwayResourceIntTest.createEntity(em);
        em.persist(pathway);
        em.flush();
        scenariopathwaymbr.setPathway(pathway);
        return scenariopathwaymbr;
    }

    @Before
    public void initTest() {
        scenariopathwaymbrSearchRepository.deleteAll();
        scenariopathwaymbr = createEntity(em);
    }

    @Test
    @Transactional
    public void createScenariopathwaymbr() throws Exception {
        int databaseSizeBeforeCreate = scenariopathwaymbrRepository.findAll().size();

        // Create the Scenariopathwaymbr

        restScenariopathwaymbrMockMvc.perform(post("/api/scenariopathwaymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(scenariopathwaymbr)))
                .andExpect(status().isCreated());

        // Validate the Scenariopathwaymbr in the database
        List<Scenariopathwaymbr> scenariopathwaymbrs = scenariopathwaymbrRepository.findAll();
        assertThat(scenariopathwaymbrs).hasSize(databaseSizeBeforeCreate + 1);
        Scenariopathwaymbr testScenariopathwaymbr = scenariopathwaymbrs.get(scenariopathwaymbrs.size() - 1);
        assertThat(testScenariopathwaymbr.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testScenariopathwaymbr.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testScenariopathwaymbr.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testScenariopathwaymbr.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testScenariopathwaymbr.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Scenariopathwaymbr in ElasticSearch
        Scenariopathwaymbr scenariopathwaymbrEs = scenariopathwaymbrSearchRepository.findOne(testScenariopathwaymbr.getId());
        assertThat(scenariopathwaymbrEs).isEqualToComparingFieldByField(testScenariopathwaymbr);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = scenariopathwaymbrRepository.findAll().size();
        // set the field null
        scenariopathwaymbr.setStatus(null);

        // Create the Scenariopathwaymbr, which fails.

        restScenariopathwaymbrMockMvc.perform(post("/api/scenariopathwaymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(scenariopathwaymbr)))
                .andExpect(status().isBadRequest());

        List<Scenariopathwaymbr> scenariopathwaymbrs = scenariopathwaymbrRepository.findAll();
        assertThat(scenariopathwaymbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = scenariopathwaymbrRepository.findAll().size();
        // set the field null
        scenariopathwaymbr.setLastmodifiedby(null);

        // Create the Scenariopathwaymbr, which fails.

        restScenariopathwaymbrMockMvc.perform(post("/api/scenariopathwaymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(scenariopathwaymbr)))
                .andExpect(status().isBadRequest());

        List<Scenariopathwaymbr> scenariopathwaymbrs = scenariopathwaymbrRepository.findAll();
        assertThat(scenariopathwaymbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = scenariopathwaymbrRepository.findAll().size();
        // set the field null
        scenariopathwaymbr.setLastmodifieddatetime(null);

        // Create the Scenariopathwaymbr, which fails.

        restScenariopathwaymbrMockMvc.perform(post("/api/scenariopathwaymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(scenariopathwaymbr)))
                .andExpect(status().isBadRequest());

        List<Scenariopathwaymbr> scenariopathwaymbrs = scenariopathwaymbrRepository.findAll();
        assertThat(scenariopathwaymbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = scenariopathwaymbrRepository.findAll().size();
        // set the field null
        scenariopathwaymbr.setDomain(null);

        // Create the Scenariopathwaymbr, which fails.

        restScenariopathwaymbrMockMvc.perform(post("/api/scenariopathwaymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(scenariopathwaymbr)))
                .andExpect(status().isBadRequest());

        List<Scenariopathwaymbr> scenariopathwaymbrs = scenariopathwaymbrRepository.findAll();
        assertThat(scenariopathwaymbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllScenariopathwaymbrs() throws Exception {
        // Initialize the database
        scenariopathwaymbrRepository.saveAndFlush(scenariopathwaymbr);

        // Get all the scenariopathwaymbrs
        restScenariopathwaymbrMockMvc.perform(get("/api/scenariopathwaymbrs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(scenariopathwaymbr.getId().intValue())))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getScenariopathwaymbr() throws Exception {
        // Initialize the database
        scenariopathwaymbrRepository.saveAndFlush(scenariopathwaymbr);

        // Get the scenariopathwaymbr
        restScenariopathwaymbrMockMvc.perform(get("/api/scenariopathwaymbrs/{id}", scenariopathwaymbr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(scenariopathwaymbr.getId().intValue()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingScenariopathwaymbr() throws Exception {
        // Get the scenariopathwaymbr
        restScenariopathwaymbrMockMvc.perform(get("/api/scenariopathwaymbrs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateScenariopathwaymbr() throws Exception {
        // Initialize the database
        scenariopathwaymbrRepository.saveAndFlush(scenariopathwaymbr);
        scenariopathwaymbrSearchRepository.save(scenariopathwaymbr);
        int databaseSizeBeforeUpdate = scenariopathwaymbrRepository.findAll().size();

        // Update the scenariopathwaymbr
        Scenariopathwaymbr updatedScenariopathwaymbr = scenariopathwaymbrRepository.findOne(scenariopathwaymbr.getId());
        updatedScenariopathwaymbr.setComment(UPDATED_COMMENT);
        updatedScenariopathwaymbr.setStatus(UPDATED_STATUS);
        updatedScenariopathwaymbr.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedScenariopathwaymbr.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedScenariopathwaymbr.setDomain(UPDATED_DOMAIN);

        restScenariopathwaymbrMockMvc.perform(put("/api/scenariopathwaymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedScenariopathwaymbr)))
                .andExpect(status().isOk());

        // Validate the Scenariopathwaymbr in the database
        List<Scenariopathwaymbr> scenariopathwaymbrs = scenariopathwaymbrRepository.findAll();
        assertThat(scenariopathwaymbrs).hasSize(databaseSizeBeforeUpdate);
        Scenariopathwaymbr testScenariopathwaymbr = scenariopathwaymbrs.get(scenariopathwaymbrs.size() - 1);
        assertThat(testScenariopathwaymbr.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testScenariopathwaymbr.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testScenariopathwaymbr.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testScenariopathwaymbr.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testScenariopathwaymbr.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Scenariopathwaymbr in ElasticSearch
        Scenariopathwaymbr scenariopathwaymbrEs = scenariopathwaymbrSearchRepository.findOne(testScenariopathwaymbr.getId());
        assertThat(scenariopathwaymbrEs).isEqualToComparingFieldByField(testScenariopathwaymbr);
    }

    @Test
    @Transactional
    public void deleteScenariopathwaymbr() throws Exception {
        // Initialize the database
        scenariopathwaymbrRepository.saveAndFlush(scenariopathwaymbr);
        scenariopathwaymbrSearchRepository.save(scenariopathwaymbr);
        int databaseSizeBeforeDelete = scenariopathwaymbrRepository.findAll().size();

        // Get the scenariopathwaymbr
        restScenariopathwaymbrMockMvc.perform(delete("/api/scenariopathwaymbrs/{id}", scenariopathwaymbr.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean scenariopathwaymbrExistsInEs = scenariopathwaymbrSearchRepository.exists(scenariopathwaymbr.getId());
        assertThat(scenariopathwaymbrExistsInEs).isFalse();

        // Validate the database is empty
        List<Scenariopathwaymbr> scenariopathwaymbrs = scenariopathwaymbrRepository.findAll();
        assertThat(scenariopathwaymbrs).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchScenariopathwaymbr() throws Exception {
        // Initialize the database
        scenariopathwaymbrRepository.saveAndFlush(scenariopathwaymbr);
        scenariopathwaymbrSearchRepository.save(scenariopathwaymbr);

        // Search the scenariopathwaymbr
        restScenariopathwaymbrMockMvc.perform(get("/api/_search/scenariopathwaymbrs?query=id:" + scenariopathwaymbr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scenariopathwaymbr.getId().intValue())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}
