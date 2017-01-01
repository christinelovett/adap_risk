package com.innvo.web.rest;

import com.innvo.AdapRiskApp;
import com.innvo.domain.Pathwaycountermeasurembr;
import com.innvo.domain.Pathway;
import com.innvo.domain.Countermeasure;
import com.innvo.repository.PathwaycountermeasurembrRepository;
import com.innvo.repository.search.PathwaycountermeasurembrSearchRepository;

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
 * Test class for the PathwaycountermeasurembrResource REST controller.
 *
 * @see PathwaycountermeasurembrResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdapRiskApp.class)
public class PathwaycountermeasurembrResourceIntTest {
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
    private PathwaycountermeasurembrRepository pathwaycountermeasurembrRepository;

    @Inject
    private PathwaycountermeasurembrSearchRepository pathwaycountermeasurembrSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restPathwaycountermeasurembrMockMvc;

    private Pathwaycountermeasurembr pathwaycountermeasurembr;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PathwaycountermeasurembrResource pathwaycountermeasurembrResource = new PathwaycountermeasurembrResource();
        ReflectionTestUtils.setField(pathwaycountermeasurembrResource, "pathwaycountermeasurembrSearchRepository", pathwaycountermeasurembrSearchRepository);
        ReflectionTestUtils.setField(pathwaycountermeasurembrResource, "pathwaycountermeasurembrRepository", pathwaycountermeasurembrRepository);
        this.restPathwaycountermeasurembrMockMvc = MockMvcBuilders.standaloneSetup(pathwaycountermeasurembrResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pathwaycountermeasurembr createEntity(EntityManager em) {
        Pathwaycountermeasurembr pathwaycountermeasurembr = new Pathwaycountermeasurembr();
        pathwaycountermeasurembr = new Pathwaycountermeasurembr();
        pathwaycountermeasurembr.setComment(DEFAULT_COMMENT);
        pathwaycountermeasurembr.setStatus(DEFAULT_STATUS);
        pathwaycountermeasurembr.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        pathwaycountermeasurembr.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        pathwaycountermeasurembr.setDomain(DEFAULT_DOMAIN);
        // Add required entity
        Pathway pathway = PathwayResourceIntTest.createEntity(em);
        em.persist(pathway);
        em.flush();
        pathwaycountermeasurembr.setPathway(pathway);
        // Add required entity
        Countermeasure countermeasure = CountermeasureResourceIntTest.createEntity(em);
        em.persist(countermeasure);
        em.flush();
        pathwaycountermeasurembr.setCountermeasure(countermeasure);
        return pathwaycountermeasurembr;
    }

    @Before
    public void initTest() {
        pathwaycountermeasurembrSearchRepository.deleteAll();
        pathwaycountermeasurembr = createEntity(em);
    }

    @Test
    @Transactional
    public void createPathwaycountermeasurembr() throws Exception {
        int databaseSizeBeforeCreate = pathwaycountermeasurembrRepository.findAll().size();

        // Create the Pathwaycountermeasurembr

        restPathwaycountermeasurembrMockMvc.perform(post("/api/pathwaycountermeasurembrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pathwaycountermeasurembr)))
                .andExpect(status().isCreated());

        // Validate the Pathwaycountermeasurembr in the database
        List<Pathwaycountermeasurembr> pathwaycountermeasurembrs = pathwaycountermeasurembrRepository.findAll();
        assertThat(pathwaycountermeasurembrs).hasSize(databaseSizeBeforeCreate + 1);
        Pathwaycountermeasurembr testPathwaycountermeasurembr = pathwaycountermeasurembrs.get(pathwaycountermeasurembrs.size() - 1);
        assertThat(testPathwaycountermeasurembr.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testPathwaycountermeasurembr.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testPathwaycountermeasurembr.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testPathwaycountermeasurembr.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testPathwaycountermeasurembr.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Pathwaycountermeasurembr in ElasticSearch
        Pathwaycountermeasurembr pathwaycountermeasurembrEs = pathwaycountermeasurembrSearchRepository.findOne(testPathwaycountermeasurembr.getId());
        assertThat(pathwaycountermeasurembrEs).isEqualToComparingFieldByField(testPathwaycountermeasurembr);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaycountermeasurembrRepository.findAll().size();
        // set the field null
        pathwaycountermeasurembr.setStatus(null);

        // Create the Pathwaycountermeasurembr, which fails.

        restPathwaycountermeasurembrMockMvc.perform(post("/api/pathwaycountermeasurembrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pathwaycountermeasurembr)))
                .andExpect(status().isBadRequest());

        List<Pathwaycountermeasurembr> pathwaycountermeasurembrs = pathwaycountermeasurembrRepository.findAll();
        assertThat(pathwaycountermeasurembrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaycountermeasurembrRepository.findAll().size();
        // set the field null
        pathwaycountermeasurembr.setLastmodifiedby(null);

        // Create the Pathwaycountermeasurembr, which fails.

        restPathwaycountermeasurembrMockMvc.perform(post("/api/pathwaycountermeasurembrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pathwaycountermeasurembr)))
                .andExpect(status().isBadRequest());

        List<Pathwaycountermeasurembr> pathwaycountermeasurembrs = pathwaycountermeasurembrRepository.findAll();
        assertThat(pathwaycountermeasurembrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaycountermeasurembrRepository.findAll().size();
        // set the field null
        pathwaycountermeasurembr.setLastmodifieddatetime(null);

        // Create the Pathwaycountermeasurembr, which fails.

        restPathwaycountermeasurembrMockMvc.perform(post("/api/pathwaycountermeasurembrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pathwaycountermeasurembr)))
                .andExpect(status().isBadRequest());

        List<Pathwaycountermeasurembr> pathwaycountermeasurembrs = pathwaycountermeasurembrRepository.findAll();
        assertThat(pathwaycountermeasurembrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaycountermeasurembrRepository.findAll().size();
        // set the field null
        pathwaycountermeasurembr.setDomain(null);

        // Create the Pathwaycountermeasurembr, which fails.

        restPathwaycountermeasurembrMockMvc.perform(post("/api/pathwaycountermeasurembrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pathwaycountermeasurembr)))
                .andExpect(status().isBadRequest());

        List<Pathwaycountermeasurembr> pathwaycountermeasurembrs = pathwaycountermeasurembrRepository.findAll();
        assertThat(pathwaycountermeasurembrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPathwaycountermeasurembrs() throws Exception {
        // Initialize the database
        pathwaycountermeasurembrRepository.saveAndFlush(pathwaycountermeasurembr);

        // Get all the pathwaycountermeasurembrs
        restPathwaycountermeasurembrMockMvc.perform(get("/api/pathwaycountermeasurembrs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(pathwaycountermeasurembr.getId().intValue())))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getPathwaycountermeasurembr() throws Exception {
        // Initialize the database
        pathwaycountermeasurembrRepository.saveAndFlush(pathwaycountermeasurembr);

        // Get the pathwaycountermeasurembr
        restPathwaycountermeasurembrMockMvc.perform(get("/api/pathwaycountermeasurembrs/{id}", pathwaycountermeasurembr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(pathwaycountermeasurembr.getId().intValue()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPathwaycountermeasurembr() throws Exception {
        // Get the pathwaycountermeasurembr
        restPathwaycountermeasurembrMockMvc.perform(get("/api/pathwaycountermeasurembrs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePathwaycountermeasurembr() throws Exception {
        // Initialize the database
        pathwaycountermeasurembrRepository.saveAndFlush(pathwaycountermeasurembr);
        pathwaycountermeasurembrSearchRepository.save(pathwaycountermeasurembr);
        int databaseSizeBeforeUpdate = pathwaycountermeasurembrRepository.findAll().size();

        // Update the pathwaycountermeasurembr
        Pathwaycountermeasurembr updatedPathwaycountermeasurembr = pathwaycountermeasurembrRepository.findOne(pathwaycountermeasurembr.getId());
        updatedPathwaycountermeasurembr.setComment(UPDATED_COMMENT);
        updatedPathwaycountermeasurembr.setStatus(UPDATED_STATUS);
        updatedPathwaycountermeasurembr.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedPathwaycountermeasurembr.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedPathwaycountermeasurembr.setDomain(UPDATED_DOMAIN);

        restPathwaycountermeasurembrMockMvc.perform(put("/api/pathwaycountermeasurembrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPathwaycountermeasurembr)))
                .andExpect(status().isOk());

        // Validate the Pathwaycountermeasurembr in the database
        List<Pathwaycountermeasurembr> pathwaycountermeasurembrs = pathwaycountermeasurembrRepository.findAll();
        assertThat(pathwaycountermeasurembrs).hasSize(databaseSizeBeforeUpdate);
        Pathwaycountermeasurembr testPathwaycountermeasurembr = pathwaycountermeasurembrs.get(pathwaycountermeasurembrs.size() - 1);
        assertThat(testPathwaycountermeasurembr.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testPathwaycountermeasurembr.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPathwaycountermeasurembr.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testPathwaycountermeasurembr.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testPathwaycountermeasurembr.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Pathwaycountermeasurembr in ElasticSearch
        Pathwaycountermeasurembr pathwaycountermeasurembrEs = pathwaycountermeasurembrSearchRepository.findOne(testPathwaycountermeasurembr.getId());
        assertThat(pathwaycountermeasurembrEs).isEqualToComparingFieldByField(testPathwaycountermeasurembr);
    }

    @Test
    @Transactional
    public void deletePathwaycountermeasurembr() throws Exception {
        // Initialize the database
        pathwaycountermeasurembrRepository.saveAndFlush(pathwaycountermeasurembr);
        pathwaycountermeasurembrSearchRepository.save(pathwaycountermeasurembr);
        int databaseSizeBeforeDelete = pathwaycountermeasurembrRepository.findAll().size();

        // Get the pathwaycountermeasurembr
        restPathwaycountermeasurembrMockMvc.perform(delete("/api/pathwaycountermeasurembrs/{id}", pathwaycountermeasurembr.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean pathwaycountermeasurembrExistsInEs = pathwaycountermeasurembrSearchRepository.exists(pathwaycountermeasurembr.getId());
        assertThat(pathwaycountermeasurembrExistsInEs).isFalse();

        // Validate the database is empty
        List<Pathwaycountermeasurembr> pathwaycountermeasurembrs = pathwaycountermeasurembrRepository.findAll();
        assertThat(pathwaycountermeasurembrs).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPathwaycountermeasurembr() throws Exception {
        // Initialize the database
        pathwaycountermeasurembrRepository.saveAndFlush(pathwaycountermeasurembr);
        pathwaycountermeasurembrSearchRepository.save(pathwaycountermeasurembr);

        // Search the pathwaycountermeasurembr
        restPathwaycountermeasurembrMockMvc.perform(get("/api/_search/pathwaycountermeasurembrs?query=id:" + pathwaycountermeasurembr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pathwaycountermeasurembr.getId().intValue())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}
