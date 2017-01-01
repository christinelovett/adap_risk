package com.innvo.web.rest;

import com.innvo.AdapRiskApp;
import com.innvo.domain.Pathwaypathwaymbr;
import com.innvo.domain.Pathway;
import com.innvo.domain.Pathway;
import com.innvo.repository.PathwaypathwaymbrRepository;
import com.innvo.repository.search.PathwaypathwaymbrSearchRepository;

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

import com.innvo.domain.enumeration.Operator;
/**
 * Test class for the PathwaypathwaymbrResource REST controller.
 *
 * @see PathwaypathwaymbrResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdapRiskApp.class)
public class PathwaypathwaymbrResourceIntTest {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));
    private static final String DEFAULT_COMMENT = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    private static final Operator DEFAULT_LOGICOPERATOR = Operator.And;
    private static final Operator UPDATED_LOGICOPERATOR = Operator.Or;
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
    private PathwaypathwaymbrRepository pathwaypathwaymbrRepository;

    @Inject
    private PathwaypathwaymbrSearchRepository pathwaypathwaymbrSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restPathwaypathwaymbrMockMvc;

    private Pathwaypathwaymbr pathwaypathwaymbr;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PathwaypathwaymbrResource pathwaypathwaymbrResource = new PathwaypathwaymbrResource();
        ReflectionTestUtils.setField(pathwaypathwaymbrResource, "pathwaypathwaymbrSearchRepository", pathwaypathwaymbrSearchRepository);
        ReflectionTestUtils.setField(pathwaypathwaymbrResource, "pathwaypathwaymbrRepository", pathwaypathwaymbrRepository);
        this.restPathwaypathwaymbrMockMvc = MockMvcBuilders.standaloneSetup(pathwaypathwaymbrResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pathwaypathwaymbr createEntity(EntityManager em) {
        Pathwaypathwaymbr pathwaypathwaymbr = new Pathwaypathwaymbr();
        pathwaypathwaymbr = new Pathwaypathwaymbr();
        pathwaypathwaymbr.setComment(DEFAULT_COMMENT);
        pathwaypathwaymbr.setLogicoperator(DEFAULT_LOGICOPERATOR);
        pathwaypathwaymbr.setStatus(DEFAULT_STATUS);
        pathwaypathwaymbr.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        pathwaypathwaymbr.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        pathwaypathwaymbr.setDomain(DEFAULT_DOMAIN);
        // Add required entity
        Pathway parentpathway = PathwayResourceIntTest.createEntity(em);
        em.persist(parentpathway);
        em.flush();
        pathwaypathwaymbr.setParentpathway(parentpathway);
        // Add required entity
        Pathway childpathway = PathwayResourceIntTest.createEntity(em);
        em.persist(childpathway);
        em.flush();
        pathwaypathwaymbr.setChildpathway(childpathway);
        return pathwaypathwaymbr;
    }

    @Before
    public void initTest() {
        pathwaypathwaymbrSearchRepository.deleteAll();
        pathwaypathwaymbr = createEntity(em);
    }

    @Test
    @Transactional
    public void createPathwaypathwaymbr() throws Exception {
        int databaseSizeBeforeCreate = pathwaypathwaymbrRepository.findAll().size();

        // Create the Pathwaypathwaymbr

        restPathwaypathwaymbrMockMvc.perform(post("/api/pathwaypathwaymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pathwaypathwaymbr)))
                .andExpect(status().isCreated());

        // Validate the Pathwaypathwaymbr in the database
        List<Pathwaypathwaymbr> pathwaypathwaymbrs = pathwaypathwaymbrRepository.findAll();
        assertThat(pathwaypathwaymbrs).hasSize(databaseSizeBeforeCreate + 1);
        Pathwaypathwaymbr testPathwaypathwaymbr = pathwaypathwaymbrs.get(pathwaypathwaymbrs.size() - 1);
        assertThat(testPathwaypathwaymbr.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testPathwaypathwaymbr.getLogicoperator()).isEqualTo(DEFAULT_LOGICOPERATOR);
        assertThat(testPathwaypathwaymbr.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testPathwaypathwaymbr.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testPathwaypathwaymbr.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testPathwaypathwaymbr.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Pathwaypathwaymbr in ElasticSearch
        Pathwaypathwaymbr pathwaypathwaymbrEs = pathwaypathwaymbrSearchRepository.findOne(testPathwaypathwaymbr.getId());
        assertThat(pathwaypathwaymbrEs).isEqualToComparingFieldByField(testPathwaypathwaymbr);
    }

    @Test
    @Transactional
    public void checkLogicoperatorIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaypathwaymbrRepository.findAll().size();
        // set the field null
        pathwaypathwaymbr.setLogicoperator(null);

        // Create the Pathwaypathwaymbr, which fails.

        restPathwaypathwaymbrMockMvc.perform(post("/api/pathwaypathwaymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pathwaypathwaymbr)))
                .andExpect(status().isBadRequest());

        List<Pathwaypathwaymbr> pathwaypathwaymbrs = pathwaypathwaymbrRepository.findAll();
        assertThat(pathwaypathwaymbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaypathwaymbrRepository.findAll().size();
        // set the field null
        pathwaypathwaymbr.setStatus(null);

        // Create the Pathwaypathwaymbr, which fails.

        restPathwaypathwaymbrMockMvc.perform(post("/api/pathwaypathwaymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pathwaypathwaymbr)))
                .andExpect(status().isBadRequest());

        List<Pathwaypathwaymbr> pathwaypathwaymbrs = pathwaypathwaymbrRepository.findAll();
        assertThat(pathwaypathwaymbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaypathwaymbrRepository.findAll().size();
        // set the field null
        pathwaypathwaymbr.setLastmodifiedby(null);

        // Create the Pathwaypathwaymbr, which fails.

        restPathwaypathwaymbrMockMvc.perform(post("/api/pathwaypathwaymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pathwaypathwaymbr)))
                .andExpect(status().isBadRequest());

        List<Pathwaypathwaymbr> pathwaypathwaymbrs = pathwaypathwaymbrRepository.findAll();
        assertThat(pathwaypathwaymbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaypathwaymbrRepository.findAll().size();
        // set the field null
        pathwaypathwaymbr.setLastmodifieddatetime(null);

        // Create the Pathwaypathwaymbr, which fails.

        restPathwaypathwaymbrMockMvc.perform(post("/api/pathwaypathwaymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pathwaypathwaymbr)))
                .andExpect(status().isBadRequest());

        List<Pathwaypathwaymbr> pathwaypathwaymbrs = pathwaypathwaymbrRepository.findAll();
        assertThat(pathwaypathwaymbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwaypathwaymbrRepository.findAll().size();
        // set the field null
        pathwaypathwaymbr.setDomain(null);

        // Create the Pathwaypathwaymbr, which fails.

        restPathwaypathwaymbrMockMvc.perform(post("/api/pathwaypathwaymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pathwaypathwaymbr)))
                .andExpect(status().isBadRequest());

        List<Pathwaypathwaymbr> pathwaypathwaymbrs = pathwaypathwaymbrRepository.findAll();
        assertThat(pathwaypathwaymbrs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPathwaypathwaymbrs() throws Exception {
        // Initialize the database
        pathwaypathwaymbrRepository.saveAndFlush(pathwaypathwaymbr);

        // Get all the pathwaypathwaymbrs
        restPathwaypathwaymbrMockMvc.perform(get("/api/pathwaypathwaymbrs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(pathwaypathwaymbr.getId().intValue())))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
                .andExpect(jsonPath("$.[*].logicoperator").value(hasItem(DEFAULT_LOGICOPERATOR.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getPathwaypathwaymbr() throws Exception {
        // Initialize the database
        pathwaypathwaymbrRepository.saveAndFlush(pathwaypathwaymbr);

        // Get the pathwaypathwaymbr
        restPathwaypathwaymbrMockMvc.perform(get("/api/pathwaypathwaymbrs/{id}", pathwaypathwaymbr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(pathwaypathwaymbr.getId().intValue()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.logicoperator").value(DEFAULT_LOGICOPERATOR.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPathwaypathwaymbr() throws Exception {
        // Get the pathwaypathwaymbr
        restPathwaypathwaymbrMockMvc.perform(get("/api/pathwaypathwaymbrs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePathwaypathwaymbr() throws Exception {
        // Initialize the database
        pathwaypathwaymbrRepository.saveAndFlush(pathwaypathwaymbr);
        pathwaypathwaymbrSearchRepository.save(pathwaypathwaymbr);
        int databaseSizeBeforeUpdate = pathwaypathwaymbrRepository.findAll().size();

        // Update the pathwaypathwaymbr
        Pathwaypathwaymbr updatedPathwaypathwaymbr = pathwaypathwaymbrRepository.findOne(pathwaypathwaymbr.getId());
        updatedPathwaypathwaymbr.setComment(UPDATED_COMMENT);
        updatedPathwaypathwaymbr.setLogicoperator(UPDATED_LOGICOPERATOR);
        updatedPathwaypathwaymbr.setStatus(UPDATED_STATUS);
        updatedPathwaypathwaymbr.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedPathwaypathwaymbr.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedPathwaypathwaymbr.setDomain(UPDATED_DOMAIN);

        restPathwaypathwaymbrMockMvc.perform(put("/api/pathwaypathwaymbrs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPathwaypathwaymbr)))
                .andExpect(status().isOk());

        // Validate the Pathwaypathwaymbr in the database
        List<Pathwaypathwaymbr> pathwaypathwaymbrs = pathwaypathwaymbrRepository.findAll();
        assertThat(pathwaypathwaymbrs).hasSize(databaseSizeBeforeUpdate);
        Pathwaypathwaymbr testPathwaypathwaymbr = pathwaypathwaymbrs.get(pathwaypathwaymbrs.size() - 1);
        assertThat(testPathwaypathwaymbr.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testPathwaypathwaymbr.getLogicoperator()).isEqualTo(UPDATED_LOGICOPERATOR);
        assertThat(testPathwaypathwaymbr.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPathwaypathwaymbr.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testPathwaypathwaymbr.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testPathwaypathwaymbr.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Pathwaypathwaymbr in ElasticSearch
        Pathwaypathwaymbr pathwaypathwaymbrEs = pathwaypathwaymbrSearchRepository.findOne(testPathwaypathwaymbr.getId());
        assertThat(pathwaypathwaymbrEs).isEqualToComparingFieldByField(testPathwaypathwaymbr);
    }

    @Test
    @Transactional
    public void deletePathwaypathwaymbr() throws Exception {
        // Initialize the database
        pathwaypathwaymbrRepository.saveAndFlush(pathwaypathwaymbr);
        pathwaypathwaymbrSearchRepository.save(pathwaypathwaymbr);
        int databaseSizeBeforeDelete = pathwaypathwaymbrRepository.findAll().size();

        // Get the pathwaypathwaymbr
        restPathwaypathwaymbrMockMvc.perform(delete("/api/pathwaypathwaymbrs/{id}", pathwaypathwaymbr.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean pathwaypathwaymbrExistsInEs = pathwaypathwaymbrSearchRepository.exists(pathwaypathwaymbr.getId());
        assertThat(pathwaypathwaymbrExistsInEs).isFalse();

        // Validate the database is empty
        List<Pathwaypathwaymbr> pathwaypathwaymbrs = pathwaypathwaymbrRepository.findAll();
        assertThat(pathwaypathwaymbrs).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPathwaypathwaymbr() throws Exception {
        // Initialize the database
        pathwaypathwaymbrRepository.saveAndFlush(pathwaypathwaymbr);
        pathwaypathwaymbrSearchRepository.save(pathwaypathwaymbr);

        // Search the pathwaypathwaymbr
        restPathwaypathwaymbrMockMvc.perform(get("/api/_search/pathwaypathwaymbrs?query=id:" + pathwaypathwaymbr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pathwaypathwaymbr.getId().intValue())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].logicoperator").value(hasItem(DEFAULT_LOGICOPERATOR.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}
