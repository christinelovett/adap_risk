package com.innvo.web.rest;

import com.innvo.AdapRiskApp;

import com.innvo.domain.Target;
import com.innvo.repository.TargetRepository;
import com.innvo.repository.search.TargetSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static com.innvo.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TargetResource REST controller.
 *
 * @see TargetResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdapRiskApp.class)
public class TargetResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_NAMESHORT = "AAAAAAAAAA";
    private static final String UPDATED_NAMESHORT = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ISABSTRACT = false;
    private static final Boolean UPDATED_ISABSTRACT = true;

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_LASTMODIFIEDBY = "AAAAAAAAAA";
    private static final String UPDATED_LASTMODIFIEDBY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_LASTMODIFIEDDATETIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_LASTMODIFIEDDATETIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_DOMAIN = "AAAAAAAAAA";
    private static final String UPDATED_DOMAIN = "BBBBBBBBBB";

    @Inject
    private TargetRepository targetRepository;

    @Inject
    private TargetSearchRepository targetSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restTargetMockMvc;

    private Target target;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TargetResource targetResource = new TargetResource();
        ReflectionTestUtils.setField(targetResource, "targetSearchRepository", targetSearchRepository);
        ReflectionTestUtils.setField(targetResource, "targetRepository", targetRepository);
        this.restTargetMockMvc = MockMvcBuilders.standaloneSetup(targetResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Target createEntity(EntityManager em) {
        Target target = new Target();
        target.setName(DEFAULT_NAME);
        target.setNameshort(DEFAULT_NAMESHORT);
        target.setDescription(DEFAULT_DESCRIPTION);
        target.setIsabstract(DEFAULT_ISABSTRACT);
        target.setStatus(DEFAULT_STATUS);
        target.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        target.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        target.setDomain(DEFAULT_DOMAIN);
        return target;
    }

    @Before
    public void initTest() {
        targetSearchRepository.deleteAll();
        target = createEntity(em);
    }

    @Test
    @Transactional
    public void createTarget() throws Exception {
        int databaseSizeBeforeCreate = targetRepository.findAll().size();

        // Create the Target

        restTargetMockMvc.perform(post("/api/targets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(target)))
            .andExpect(status().isCreated());

        // Validate the Target in the database
        List<Target> targetList = targetRepository.findAll();
        assertThat(targetList).hasSize(databaseSizeBeforeCreate + 1);
        Target testTarget = targetList.get(targetList.size() - 1);
        assertThat(testTarget.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTarget.getNameshort()).isEqualTo(DEFAULT_NAMESHORT);
        assertThat(testTarget.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTarget.isIsabstract()).isEqualTo(DEFAULT_ISABSTRACT);
        assertThat(testTarget.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testTarget.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testTarget.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testTarget.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Target in ElasticSearch
        Target targetEs = targetSearchRepository.findOne(testTarget.getId());
        assertThat(targetEs).isEqualToComparingFieldByField(testTarget);
    }

    @Test
    @Transactional
    public void createTargetWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = targetRepository.findAll().size();

        // Create the Target with an existing ID
        Target existingTarget = new Target();
        existingTarget.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTargetMockMvc.perform(post("/api/targets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingTarget)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Target> targetList = targetRepository.findAll();
        assertThat(targetList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = targetRepository.findAll().size();
        // set the field null
        target.setName(null);

        // Create the Target, which fails.

        restTargetMockMvc.perform(post("/api/targets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(target)))
            .andExpect(status().isBadRequest());

        List<Target> targetList = targetRepository.findAll();
        assertThat(targetList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameshortIsRequired() throws Exception {
        int databaseSizeBeforeTest = targetRepository.findAll().size();
        // set the field null
        target.setNameshort(null);

        // Create the Target, which fails.

        restTargetMockMvc.perform(post("/api/targets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(target)))
            .andExpect(status().isBadRequest());

        List<Target> targetList = targetRepository.findAll();
        assertThat(targetList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = targetRepository.findAll().size();
        // set the field null
        target.setStatus(null);

        // Create the Target, which fails.

        restTargetMockMvc.perform(post("/api/targets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(target)))
            .andExpect(status().isBadRequest());

        List<Target> targetList = targetRepository.findAll();
        assertThat(targetList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = targetRepository.findAll().size();
        // set the field null
        target.setLastmodifiedby(null);

        // Create the Target, which fails.

        restTargetMockMvc.perform(post("/api/targets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(target)))
            .andExpect(status().isBadRequest());

        List<Target> targetList = targetRepository.findAll();
        assertThat(targetList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = targetRepository.findAll().size();
        // set the field null
        target.setLastmodifieddatetime(null);

        // Create the Target, which fails.

        restTargetMockMvc.perform(post("/api/targets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(target)))
            .andExpect(status().isBadRequest());

        List<Target> targetList = targetRepository.findAll();
        assertThat(targetList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = targetRepository.findAll().size();
        // set the field null
        target.setDomain(null);

        // Create the Target, which fails.

        restTargetMockMvc.perform(post("/api/targets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(target)))
            .andExpect(status().isBadRequest());

        List<Target> targetList = targetRepository.findAll();
        assertThat(targetList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTargets() throws Exception {
        // Initialize the database
        targetRepository.saveAndFlush(target);

        // Get all the targetList
        restTargetMockMvc.perform(get("/api/targets?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(target.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].nameshort").value(hasItem(DEFAULT_NAMESHORT.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].isabstract").value(hasItem(DEFAULT_ISABSTRACT.booleanValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(sameInstant(DEFAULT_LASTMODIFIEDDATETIME))))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }

    @Test
    @Transactional
    public void getTarget() throws Exception {
        // Initialize the database
        targetRepository.saveAndFlush(target);

        // Get the target
        restTargetMockMvc.perform(get("/api/targets/{id}", target.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(target.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.nameshort").value(DEFAULT_NAMESHORT.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.isabstract").value(DEFAULT_ISABSTRACT.booleanValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastmodifiedby").value(DEFAULT_LASTMODIFIEDBY.toString()))
            .andExpect(jsonPath("$.lastmodifieddatetime").value(sameInstant(DEFAULT_LASTMODIFIEDDATETIME)))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTarget() throws Exception {
        // Get the target
        restTargetMockMvc.perform(get("/api/targets/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTarget() throws Exception {
        // Initialize the database
        targetRepository.saveAndFlush(target);
        targetSearchRepository.save(target);
        int databaseSizeBeforeUpdate = targetRepository.findAll().size();

        // Update the target
        Target updatedTarget = targetRepository.findOne(target.getId());
        updatedTarget.setName(UPDATED_NAME);
        updatedTarget.setNameshort(UPDATED_NAMESHORT);
        updatedTarget.setDescription(UPDATED_DESCRIPTION);
        updatedTarget.setIsabstract(UPDATED_ISABSTRACT);
        updatedTarget.setStatus(UPDATED_STATUS);
        updatedTarget.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedTarget.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedTarget.setDomain(UPDATED_DOMAIN);

        restTargetMockMvc.perform(put("/api/targets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTarget)))
            .andExpect(status().isOk());

        // Validate the Target in the database
        List<Target> targetList = targetRepository.findAll();
        assertThat(targetList).hasSize(databaseSizeBeforeUpdate);
        Target testTarget = targetList.get(targetList.size() - 1);
        assertThat(testTarget.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTarget.getNameshort()).isEqualTo(UPDATED_NAMESHORT);
        assertThat(testTarget.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTarget.isIsabstract()).isEqualTo(UPDATED_ISABSTRACT);
        assertThat(testTarget.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testTarget.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testTarget.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testTarget.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Target in ElasticSearch
        Target targetEs = targetSearchRepository.findOne(testTarget.getId());
        assertThat(targetEs).isEqualToComparingFieldByField(testTarget);
    }

    @Test
    @Transactional
    public void updateNonExistingTarget() throws Exception {
        int databaseSizeBeforeUpdate = targetRepository.findAll().size();

        // Create the Target

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTargetMockMvc.perform(put("/api/targets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(target)))
            .andExpect(status().isCreated());

        // Validate the Target in the database
        List<Target> targetList = targetRepository.findAll();
        assertThat(targetList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteTarget() throws Exception {
        // Initialize the database
        targetRepository.saveAndFlush(target);
        targetSearchRepository.save(target);
        int databaseSizeBeforeDelete = targetRepository.findAll().size();

        // Get the target
        restTargetMockMvc.perform(delete("/api/targets/{id}", target.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean targetExistsInEs = targetSearchRepository.exists(target.getId());
        assertThat(targetExistsInEs).isFalse();

        // Validate the database is empty
        List<Target> targetList = targetRepository.findAll();
        assertThat(targetList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTarget() throws Exception {
        // Initialize the database
        targetRepository.saveAndFlush(target);
        targetSearchRepository.save(target);

        // Search the target
        restTargetMockMvc.perform(get("/api/_search/targets?query=id:" + target.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(target.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].nameshort").value(hasItem(DEFAULT_NAMESHORT.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].isabstract").value(hasItem(DEFAULT_ISABSTRACT.booleanValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastmodifiedby").value(hasItem(DEFAULT_LASTMODIFIEDBY.toString())))
            .andExpect(jsonPath("$.[*].lastmodifieddatetime").value(hasItem(sameInstant(DEFAULT_LASTMODIFIEDDATETIME))))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())));
    }
}
