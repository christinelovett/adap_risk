package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Weapon;

import com.innvo.repository.WeaponRepository;
import com.innvo.repository.search.WeaponSearchRepository;
import com.innvo.web.rest.util.HeaderUtil;
import com.innvo.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Weapon.
 */
@RestController
@RequestMapping("/api")
public class WeaponResource {

    private final Logger log = LoggerFactory.getLogger(WeaponResource.class);
        
    @Inject
    private WeaponRepository weaponRepository;

    @Inject
    private WeaponSearchRepository weaponSearchRepository;

    /**
     * POST  /weapons : Create a new weapon.
     *
     * @param weapon the weapon to create
     * @return the ResponseEntity with status 201 (Created) and with body the new weapon, or with status 400 (Bad Request) if the weapon has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/weapons",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Weapon> createWeapon(@Valid @RequestBody Weapon weapon) throws URISyntaxException {
        log.debug("REST request to save Weapon : {}", weapon);
        if (weapon.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("weapon", "idexists", "A new weapon cannot already have an ID")).body(null);
        }
        Weapon result = weaponRepository.save(weapon);
        weaponSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/weapons/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("weapon", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /weapons : Updates an existing weapon.
     *
     * @param weapon the weapon to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated weapon,
     * or with status 400 (Bad Request) if the weapon is not valid,
     * or with status 500 (Internal Server Error) if the weapon couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/weapons",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Weapon> updateWeapon(@Valid @RequestBody Weapon weapon) throws URISyntaxException {
        log.debug("REST request to update Weapon : {}", weapon);
        if (weapon.getId() == null) {
            return createWeapon(weapon);
        }
        Weapon result = weaponRepository.save(weapon);
        weaponSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("weapon", weapon.getId().toString()))
            .body(result);
    }

    /**
     * GET  /weapons : get all the weapons.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of weapons in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/weapons",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Weapon>> getAllWeapons(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Weapons");
        Page<Weapon> page = weaponRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/weapons");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /weapons/:id : get the "id" weapon.
     *
     * @param id the id of the weapon to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the weapon, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/weapons/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Weapon> getWeapon(@PathVariable Long id) {
        log.debug("REST request to get Weapon : {}", id);
        Weapon weapon = weaponRepository.findOneWithEagerRelationships(id);
        return Optional.ofNullable(weapon)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /weapons/:id : delete the "id" weapon.
     *
     * @param id the id of the weapon to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/weapons/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteWeapon(@PathVariable Long id) {
        log.debug("REST request to delete Weapon : {}", id);
        weaponRepository.delete(id);
        weaponSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("weapon", id.toString())).build();
    }

    /**
     * SEARCH  /_search/weapons?query=:query : search for the weapon corresponding
     * to the query.
     *
     * @param query the query of the weapon search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/weapons",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Weapon>> searchWeapons(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Weapons for query {}", query);
        Page<Weapon> page = weaponSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/weapons");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
