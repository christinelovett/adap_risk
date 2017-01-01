package com.innvo.web.rest;

import com.innvo.AdapRiskApp;
import com.innvo.domain.Weapon;
import com.innvo.repository.WeaponRepository;
import com.innvo.repository.search.WeaponSearchRepository;

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
 * Test class for the WeaponResource REST controller.
 *
 * @see WeaponResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdapRiskApp.class)
public class WeaponResourceIntTest {
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
    private WeaponRepository weaponRepository;

    @Inject
    private WeaponSearchRepository weaponSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restWeaponMockMvc;

    private Weapon weapon;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        WeaponResource weaponResource = new WeaponResource();
        ReflectionTestUtils.setField(weaponResource, "weaponSearchRepository", weaponSearchRepository);
        ReflectionTestUtils.setField(weaponResource, "weaponRepository", weaponRepository);
        this.restWeaponMockMvc = MockMvcBuilders.standaloneSetup(weaponResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Weapon createEntity(EntityManager em) {
        Weapon weapon = new Weapon();
        weapon = new Weapon();
        weapon.setName(DEFAULT_NAME);
        weapon.setNameshort(DEFAULT_NAMESHORT);
        weapon.setDescription(DEFAULT_DESCRIPTION);
        weapon.setIsabstract(DEFAULT_ISABSTRACT);
        weapon.setStatus(DEFAULT_STATUS);
        weapon.setLastmodifiedby(DEFAULT_LASTMODIFIEDBY);
        weapon.setLastmodifieddatetime(DEFAULT_LASTMODIFIEDDATETIME);
        weapon.setDomain(DEFAULT_DOMAIN);
        return weapon;
    }

    @Before
    public void initTest() {
        weaponSearchRepository.deleteAll();
        weapon = createEntity(em);
    }

    @Test
    @Transactional
    public void createWeapon() throws Exception {
        int databaseSizeBeforeCreate = weaponRepository.findAll().size();

        // Create the Weapon

        restWeaponMockMvc.perform(post("/api/weapons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(weapon)))
                .andExpect(status().isCreated());

        // Validate the Weapon in the database
        List<Weapon> weapons = weaponRepository.findAll();
        assertThat(weapons).hasSize(databaseSizeBeforeCreate + 1);
        Weapon testWeapon = weapons.get(weapons.size() - 1);
        assertThat(testWeapon.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testWeapon.getNameshort()).isEqualTo(DEFAULT_NAMESHORT);
        assertThat(testWeapon.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testWeapon.isIsabstract()).isEqualTo(DEFAULT_ISABSTRACT);
        assertThat(testWeapon.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testWeapon.getLastmodifiedby()).isEqualTo(DEFAULT_LASTMODIFIEDBY);
        assertThat(testWeapon.getLastmodifieddatetime()).isEqualTo(DEFAULT_LASTMODIFIEDDATETIME);
        assertThat(testWeapon.getDomain()).isEqualTo(DEFAULT_DOMAIN);

        // Validate the Weapon in ElasticSearch
        Weapon weaponEs = weaponSearchRepository.findOne(testWeapon.getId());
        assertThat(weaponEs).isEqualToComparingFieldByField(testWeapon);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = weaponRepository.findAll().size();
        // set the field null
        weapon.setName(null);

        // Create the Weapon, which fails.

        restWeaponMockMvc.perform(post("/api/weapons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(weapon)))
                .andExpect(status().isBadRequest());

        List<Weapon> weapons = weaponRepository.findAll();
        assertThat(weapons).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameshortIsRequired() throws Exception {
        int databaseSizeBeforeTest = weaponRepository.findAll().size();
        // set the field null
        weapon.setNameshort(null);

        // Create the Weapon, which fails.

        restWeaponMockMvc.perform(post("/api/weapons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(weapon)))
                .andExpect(status().isBadRequest());

        List<Weapon> weapons = weaponRepository.findAll();
        assertThat(weapons).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = weaponRepository.findAll().size();
        // set the field null
        weapon.setStatus(null);

        // Create the Weapon, which fails.

        restWeaponMockMvc.perform(post("/api/weapons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(weapon)))
                .andExpect(status().isBadRequest());

        List<Weapon> weapons = weaponRepository.findAll();
        assertThat(weapons).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifiedbyIsRequired() throws Exception {
        int databaseSizeBeforeTest = weaponRepository.findAll().size();
        // set the field null
        weapon.setLastmodifiedby(null);

        // Create the Weapon, which fails.

        restWeaponMockMvc.perform(post("/api/weapons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(weapon)))
                .andExpect(status().isBadRequest());

        List<Weapon> weapons = weaponRepository.findAll();
        assertThat(weapons).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastmodifieddatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = weaponRepository.findAll().size();
        // set the field null
        weapon.setLastmodifieddatetime(null);

        // Create the Weapon, which fails.

        restWeaponMockMvc.perform(post("/api/weapons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(weapon)))
                .andExpect(status().isBadRequest());

        List<Weapon> weapons = weaponRepository.findAll();
        assertThat(weapons).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = weaponRepository.findAll().size();
        // set the field null
        weapon.setDomain(null);

        // Create the Weapon, which fails.

        restWeaponMockMvc.perform(post("/api/weapons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(weapon)))
                .andExpect(status().isBadRequest());

        List<Weapon> weapons = weaponRepository.findAll();
        assertThat(weapons).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllWeapons() throws Exception {
        // Initialize the database
        weaponRepository.saveAndFlush(weapon);

        // Get all the weapons
        restWeaponMockMvc.perform(get("/api/weapons?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(weapon.getId().intValue())))
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
    public void getWeapon() throws Exception {
        // Initialize the database
        weaponRepository.saveAndFlush(weapon);

        // Get the weapon
        restWeaponMockMvc.perform(get("/api/weapons/{id}", weapon.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(weapon.getId().intValue()))
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
    public void getNonExistingWeapon() throws Exception {
        // Get the weapon
        restWeaponMockMvc.perform(get("/api/weapons/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWeapon() throws Exception {
        // Initialize the database
        weaponRepository.saveAndFlush(weapon);
        weaponSearchRepository.save(weapon);
        int databaseSizeBeforeUpdate = weaponRepository.findAll().size();

        // Update the weapon
        Weapon updatedWeapon = weaponRepository.findOne(weapon.getId());
        updatedWeapon.setName(UPDATED_NAME);
        updatedWeapon.setNameshort(UPDATED_NAMESHORT);
        updatedWeapon.setDescription(UPDATED_DESCRIPTION);
        updatedWeapon.setIsabstract(UPDATED_ISABSTRACT);
        updatedWeapon.setStatus(UPDATED_STATUS);
        updatedWeapon.setLastmodifiedby(UPDATED_LASTMODIFIEDBY);
        updatedWeapon.setLastmodifieddatetime(UPDATED_LASTMODIFIEDDATETIME);
        updatedWeapon.setDomain(UPDATED_DOMAIN);

        restWeaponMockMvc.perform(put("/api/weapons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedWeapon)))
                .andExpect(status().isOk());

        // Validate the Weapon in the database
        List<Weapon> weapons = weaponRepository.findAll();
        assertThat(weapons).hasSize(databaseSizeBeforeUpdate);
        Weapon testWeapon = weapons.get(weapons.size() - 1);
        assertThat(testWeapon.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testWeapon.getNameshort()).isEqualTo(UPDATED_NAMESHORT);
        assertThat(testWeapon.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testWeapon.isIsabstract()).isEqualTo(UPDATED_ISABSTRACT);
        assertThat(testWeapon.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testWeapon.getLastmodifiedby()).isEqualTo(UPDATED_LASTMODIFIEDBY);
        assertThat(testWeapon.getLastmodifieddatetime()).isEqualTo(UPDATED_LASTMODIFIEDDATETIME);
        assertThat(testWeapon.getDomain()).isEqualTo(UPDATED_DOMAIN);

        // Validate the Weapon in ElasticSearch
        Weapon weaponEs = weaponSearchRepository.findOne(testWeapon.getId());
        assertThat(weaponEs).isEqualToComparingFieldByField(testWeapon);
    }

    @Test
    @Transactional
    public void deleteWeapon() throws Exception {
        // Initialize the database
        weaponRepository.saveAndFlush(weapon);
        weaponSearchRepository.save(weapon);
        int databaseSizeBeforeDelete = weaponRepository.findAll().size();

        // Get the weapon
        restWeaponMockMvc.perform(delete("/api/weapons/{id}", weapon.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean weaponExistsInEs = weaponSearchRepository.exists(weapon.getId());
        assertThat(weaponExistsInEs).isFalse();

        // Validate the database is empty
        List<Weapon> weapons = weaponRepository.findAll();
        assertThat(weapons).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchWeapon() throws Exception {
        // Initialize the database
        weaponRepository.saveAndFlush(weapon);
        weaponSearchRepository.save(weapon);

        // Search the weapon
        restWeaponMockMvc.perform(get("/api/_search/weapons?query=id:" + weapon.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(weapon.getId().intValue())))
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
