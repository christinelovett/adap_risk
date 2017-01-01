package com.innvo.web.rest;

import com.innvo.AdapRiskApp;
import com.innvo.domain.Pathway;
import com.innvo.repository.PathwayRepository;
import com.innvo.repository.search.PathwaySearchRepository;

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
 * Test class for the PathwayResource REST controller.
 *
 * @see PathwayResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdapRiskApp.class)
public class PathwayResourceIntTest {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));
    private static final String DEFAULT_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_NAMESHORT = "AAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAMESHORT = "BBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    private static final Boolean DEFAULT_ISROOTNODE = false;
    private static final Boolean UPDATED_ISROOTNODE = true;

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
    private PathwayRepository pathwayRepository;

    @Inject
    private PathwaySearchRepository pathwaySearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restPathwayMockMvc;

    private Pathway pathway;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PathwayResource pathwayResource = new PathwayResource();
        ReflectionTestUtils.setField(pathwayResource, "pathwaySearchRepository", pathwaySearchRepository);
        ReflectionTestUtils.setField(pathwayResource, "pathwayRepository", pathwayRepository);
        this.restPathwayMockMvc = MockMvcBuilders.standaloneSetup(pathwayResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pathway createEntity(EntityManager em) {
        Pathway pathway = new Pathway();
        pathway = new Pathway();
        pathway.setName(DEFAULT_NAME);
        pathway.setNameshort(DEFAULT_NAMESHORT);
        pathway.setDescription(DEFAULT_DESCRIPTION);
        pathway.setIsrootnode(DEFAULT_ISROOTNODE);
        pathway.setIsabstract(DEFAULT_ISABSTRACT);
        pathway.setStatus(DEFAULT_STATUS);
        pathway.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        pathway.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        pathway.setDomain(DEFAULT_DOMAIN);
        return pathway;
    }

    @Before
    public void initTest() {
        pathwaySearchRepository.deleteAll();
        pathway = createEntity(em);
    }

    @Test
    @Transactional
    public void createPathway() throws Exception {
        int databaseSizeBeforeCreate = pathwayRepository.findAll().size();

        // Create the Pathway

        restPathwayMockMvc.perform(post("/api/pathways")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pathway)))
                .andExpect(status().isCreated());

        // Validate the Pathway in the database
        List<Pathway> pathways = pathwayRepository.findAll();
        assertThat(pathways).hasSize(databaseSizeBeforeCreate + 1);
        Pathway testPathway = pathways.get(pathways.size() - 1);
        assertThat(testPathway.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPathway.getNameshort()).isEqualTo(DEFAULT_NAMESHORT);
        assertThat(testPathway.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPathway.isIsrootnode()).isEqualTo(DEFAULT_ISROOTNODE);
        assertThat(testPathway.isIsabstract()).isEqualTo(DEFAULT_ISABSTRACT);
        assertThat(testPathway.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testPathway.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testPathway.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testPathway.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Pathway in ElasticSearch
        Pathway pathwayEs = pathwaySearchRepository.findOne(testPathway.getId());
        assertThat(pathwayEs).isEqualToComparingFieldByField(testPathway);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwayRepository.findAll().size();
        // set the field null
        pathway.setName(null);

        // Create the Pathway, which fails.

        restPathwayMockMvc.perform(post("/api/pathways")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pathway)))
                .andExpect(status().isBadRequest());

        List<Pathway> pathways = pathwayRepository.findAll();
        assertThat(pathways).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameshortIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwayRepository.findAll().size();
        // set the field null
        pathway.setNameshort(null);

        // Create the Pathway, which fails.

        restPathwayMockMvc.perform(post("/api/pathways")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pathway)))
                .andExpect(status().isBadRequest());

        List<Pathway> pathways = pathwayRepository.findAll();
        assertThat(pathways).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwayRepository.findAll().size();
        // set the field null
        pathway.setStatus(null);

        // Create the Pathway, which fails.

        restPathwayMockMvc.perform(post("/api/pathways")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pathway)))
                .andExpect(status().isBadRequest());

        List<Pathway> pathways = pathwayRepository.findAll();
        assertThat(pathways).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwayRepository.findAll().size();
        // set the field null
        pathway.setLastmodifiedby(null);

        // Create the Pathway, which fails.

        restPathwayMockMvc.perform(post("/api/pathways")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pathway)))
                .andExpect(status().isBadRequest());

        List<Pathway> pathways = pathwayRepository.findAll();
        assertThat(pathways).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwayRepository.findAll().size();
        // set the field null
        pathway.setLastmodifieddatetime(null);

        // Create the Pathway, which fails.

        restPathwayMockMvc.perform(post("/api/pathways")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pathway)))
                .andExpect(status().isBadRequest());

        List<Pathway> pathways = pathwayRepository.findAll();
        assertThat(pathways).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = pathwayRepository.findAll().size();
        // set the field null
        pathway.setDomain(null);

        // Create the Pathway, which fails.

        restPathwayMockMvc.perform(post("/api/pathways")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pathway)))
                .andExpect(status().isBadRequest());

        List<Pathway> pathways = pathwayRepository.findAll();
        assertThat(pathways).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPathways() throws Exception {
        // Initialize the database
        pathwayRepository.saveAndFlush(pathway);

        // Get all the pathways
        restPathwayMockMvc.perform(get("/api/pathways?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(pathway.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].nameshort").value(hasItem(DEFAULT_NAMESHORT.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].isrootnode").value(hasItem(DEFAULT_ISROOTNODE.booleanValue())))
                .andExpect(jsonPath("$.[*].isabstract").value(hasItem(DEFAULT_ISABSTRACT.booleanValue())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
                .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
                .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getPathway() throws Exception {
        // Initialize the database
        pathwayRepository.saveAndFlush(pathway);

        // Get the pathway
        restPathwayMockMvc.perform(get("/api/pathways/{id}", pathway.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(pathway.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.nameshort").value(DEFAULT_NAMESHORT.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.isrootnode").value(DEFAULT_ISROOTNODE.booleanValue()))
            .andExpect(jsonPath("$.isabstract").value(DEFAULT_ISABSTRACT.booleanValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(DEFAULT_LASTMODIFIEDDATETIME_STR))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPathway() throws Exception {
        // Get the pathway
        restPathwayMockMvc.perform(get("/api/pathways/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePathway() throws Exception {
        // Initialize the database
        pathwayRepository.saveAndFlush(pathway);
        pathwaySearchRepository.save(pathway);
        int databaseSizeBeforeUpdate = pathwayRepository.findAll().size();

        // Update the pathway
        Pathway updatedPathway = pathwayRepository.findOne(pathway.getId());
        updatedPathway.setName(UPDATED_NAME);
        updatedPathway.setNameshort(UPDATED_NAMESHORT);
        updatedPathway.setDescription(UPDATED_DESCRIPTION);
        updatedPathway.setIsrootnode(UPDATED_ISROOTNODE);
        updatedPathway.setIsabstract(UPDATED_ISABSTRACT);
        updatedPathway.setStatus(UPDATED_STATUS);
        updatedPathway.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedPathway.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedPathway.setDomain(UPDATED_DOMAIN);

        restPathwayMockMvc.perform(put("/api/pathways")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPathway)))
                .andExpect(status().isOk());

        // Validate the Pathway in the database
        List<Pathway> pathways = pathwayRepository.findAll();
        assertThat(pathways).hasSize(databaseSizeBeforeUpdate);
        Pathway testPathway = pathways.get(pathways.size() - 1);
        assertThat(testPathway.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPathway.getNameshort()).isEqualTo(UPDATED_NAMESHORT);
        assertThat(testPathway.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPathway.isIsrootnode()).isEqualTo(UPDATED_ISROOTNODE);
        assertThat(testPathway.isIsabstract()).isEqualTo(UPDATED_ISABSTRACT);
        assertThat(testPathway.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testPathway.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testPathway.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testPathway.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Pathway in ElasticSearch
        Pathway pathwayEs = pathwaySearchRepository.findOne(testPathway.getId());
        assertThat(pathwayEs).isEqualToComparingFieldByField(testPathway);
    }

    @Test
    @Transactional
    public void deletePathway() throws Exception {
        // Initialize the database
        pathwayRepository.saveAndFlush(pathway);
        pathwaySearchRepository.save(pathway);
        int databaseSizeBeforeDelete = pathwayRepository.findAll().size();

        // Get the pathway
        restPathwayMockMvc.perform(delete("/api/pathways/{id}", pathway.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean pathwayExistsInEs = pathwaySearchRepository.exists(pathway.getId());
        assertThat(pathwayExistsInEs).isFalse();

        // Validate the database is empty
        List<Pathway> pathways = pathwayRepository.findAll();
        assertThat(pathways).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPathway() throws Exception {
        // Initialize the database
        pathwayRepository.saveAndFlush(pathway);
        pathwaySearchRepository.save(pathway);

        // Search the pathway
        restPathwayMockMvc.perform(get("/api/_search/pathways?query=id:" + pathway.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pathway.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].nameshort").value(hasItem(DEFAULT_NAMESHORT.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].isrootnode").value(hasItem(DEFAULT_ISROOTNODE.booleanValue())))
            .andExpect(jsonPath("$.[*].isabstract").value(hasItem(DEFAULT_ISABSTRACT.booleanValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(DEFAULT_LASTMODIFIEDDATETIME_STR)))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}
