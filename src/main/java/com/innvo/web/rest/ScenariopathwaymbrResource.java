package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Scenariopathwaymbr;

import com.innvo.repository.ScenariopathwaymbrRepository;
import com.innvo.repository.search.ScenariopathwaymbrSearchRepository;
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
 * REST controller for managing Scenariopathwaymbr.
 */
@RestController
@RequestMapping("/api")
public class ScenariopathwaymbrResource {

    private final Logger log = LoggerFactory.getLogger(ScenariopathwaymbrResource.class);
        
    @Inject
    private ScenariopathwaymbrRepository scenariopathwaymbrRepository;

    @Inject
    private ScenariopathwaymbrSearchRepository scenariopathwaymbrSearchRepository;

    /**
     * POST  /scenariopathwaymbrs : Create a new scenariopathwaymbr.
     *
     * @param scenariopathwaymbr the scenariopathwaymbr to create
     * @return the ResponseEntity with status 201 (Created) and with body the new scenariopathwaymbr, or with status 400 (Bad Request) if the scenariopathwaymbr has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/scenariopathwaymbrs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Scenariopathwaymbr> createScenariopathwaymbr(@Valid @RequestBody Scenariopathwaymbr scenariopathwaymbr) throws URISyntaxException {
        log.debug("REST request to save Scenariopathwaymbr : {}", scenariopathwaymbr);
        if (scenariopathwaymbr.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("scenariopathwaymbr", "idexists", "A new scenariopathwaymbr cannot already have an ID")).body(null);
        }
        Scenariopathwaymbr result = scenariopathwaymbrRepository.save(scenariopathwaymbr);
        scenariopathwaymbrSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/scenariopathwaymbrs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("scenariopathwaymbr", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /scenariopathwaymbrs : Updates an existing scenariopathwaymbr.
     *
     * @param scenariopathwaymbr the scenariopathwaymbr to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated scenariopathwaymbr,
     * or with status 400 (Bad Request) if the scenariopathwaymbr is not valid,
     * or with status 500 (Internal Server Error) if the scenariopathwaymbr couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/scenariopathwaymbrs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Scenariopathwaymbr> updateScenariopathwaymbr(@Valid @RequestBody Scenariopathwaymbr scenariopathwaymbr) throws URISyntaxException {
        log.debug("REST request to update Scenariopathwaymbr : {}", scenariopathwaymbr);
        if (scenariopathwaymbr.getId() == null) {
            return createScenariopathwaymbr(scenariopathwaymbr);
        }
        Scenariopathwaymbr result = scenariopathwaymbrRepository.save(scenariopathwaymbr);
        scenariopathwaymbrSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("scenariopathwaymbr", scenariopathwaymbr.getId().toString()))
            .body(result);
    }

    /**
     * GET  /scenariopathwaymbrs : get all the scenariopathwaymbrs.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of scenariopathwaymbrs in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/scenariopathwaymbrs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Scenariopathwaymbr>> getAllScenariopathwaymbrs(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Scenariopathwaymbrs");
        Page<Scenariopathwaymbr> page = scenariopathwaymbrRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/scenariopathwaymbrs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /scenariopathwaymbrs/:id : get the "id" scenariopathwaymbr.
     *
     * @param id the id of the scenariopathwaymbr to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the scenariopathwaymbr, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/scenariopathwaymbrs/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Scenariopathwaymbr> getScenariopathwaymbr(@PathVariable Long id) {
        log.debug("REST request to get Scenariopathwaymbr : {}", id);
        Scenariopathwaymbr scenariopathwaymbr = scenariopathwaymbrRepository.findOne(id);
        return Optional.ofNullable(scenariopathwaymbr)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /scenariopathwaymbrs/:id : delete the "id" scenariopathwaymbr.
     *
     * @param id the id of the scenariopathwaymbr to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/scenariopathwaymbrs/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteScenariopathwaymbr(@PathVariable Long id) {
        log.debug("REST request to delete Scenariopathwaymbr : {}", id);
        scenariopathwaymbrRepository.delete(id);
        scenariopathwaymbrSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("scenariopathwaymbr", id.toString())).build();
    }

    /**
     * SEARCH  /_search/scenariopathwaymbrs?query=:query : search for the scenariopathwaymbr corresponding
     * to the query.
     *
     * @param query the query of the scenariopathwaymbr search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/scenariopathwaymbrs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Scenariopathwaymbr>> searchScenariopathwaymbrs(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Scenariopathwaymbrs for query {}", query);
        Page<Scenariopathwaymbr> page = scenariopathwaymbrSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/scenariopathwaymbrs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
