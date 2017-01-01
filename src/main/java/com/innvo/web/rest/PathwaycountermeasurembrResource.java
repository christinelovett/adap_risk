package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Pathwaycountermeasurembr;

import com.innvo.repository.PathwaycountermeasurembrRepository;
import com.innvo.repository.search.PathwaycountermeasurembrSearchRepository;
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
 * REST controller for managing Pathwaycountermeasurembr.
 */
@RestController
@RequestMapping("/api")
public class PathwaycountermeasurembrResource {

    private final Logger log = LoggerFactory.getLogger(PathwaycountermeasurembrResource.class);
        
    @Inject
    private PathwaycountermeasurembrRepository pathwaycountermeasurembrRepository;

    @Inject
    private PathwaycountermeasurembrSearchRepository pathwaycountermeasurembrSearchRepository;

    /**
     * POST  /pathwaycountermeasurembrs : Create a new pathwaycountermeasurembr.
     *
     * @param pathwaycountermeasurembr the pathwaycountermeasurembr to create
     * @return the ResponseEntity with status 201 (Created) and with body the new pathwaycountermeasurembr, or with status 400 (Bad Request) if the pathwaycountermeasurembr has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/pathwaycountermeasurembrs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Pathwaycountermeasurembr> createPathwaycountermeasurembr(@Valid @RequestBody Pathwaycountermeasurembr pathwaycountermeasurembr) throws URISyntaxException {
        log.debug("REST request to save Pathwaycountermeasurembr : {}", pathwaycountermeasurembr);
        if (pathwaycountermeasurembr.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("pathwaycountermeasurembr", "idexists", "A new pathwaycountermeasurembr cannot already have an ID")).body(null);
        }
        Pathwaycountermeasurembr result = pathwaycountermeasurembrRepository.save(pathwaycountermeasurembr);
        pathwaycountermeasurembrSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/pathwaycountermeasurembrs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("pathwaycountermeasurembr", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /pathwaycountermeasurembrs : Updates an existing pathwaycountermeasurembr.
     *
     * @param pathwaycountermeasurembr the pathwaycountermeasurembr to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated pathwaycountermeasurembr,
     * or with status 400 (Bad Request) if the pathwaycountermeasurembr is not valid,
     * or with status 500 (Internal Server Error) if the pathwaycountermeasurembr couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/pathwaycountermeasurembrs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Pathwaycountermeasurembr> updatePathwaycountermeasurembr(@Valid @RequestBody Pathwaycountermeasurembr pathwaycountermeasurembr) throws URISyntaxException {
        log.debug("REST request to update Pathwaycountermeasurembr : {}", pathwaycountermeasurembr);
        if (pathwaycountermeasurembr.getId() == null) {
            return createPathwaycountermeasurembr(pathwaycountermeasurembr);
        }
        Pathwaycountermeasurembr result = pathwaycountermeasurembrRepository.save(pathwaycountermeasurembr);
        pathwaycountermeasurembrSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("pathwaycountermeasurembr", pathwaycountermeasurembr.getId().toString()))
            .body(result);
    }

    /**
     * GET  /pathwaycountermeasurembrs : get all the pathwaycountermeasurembrs.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of pathwaycountermeasurembrs in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/pathwaycountermeasurembrs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Pathwaycountermeasurembr>> getAllPathwaycountermeasurembrs(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Pathwaycountermeasurembrs");
        Page<Pathwaycountermeasurembr> page = pathwaycountermeasurembrRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/pathwaycountermeasurembrs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /pathwaycountermeasurembrs/:id : get the "id" pathwaycountermeasurembr.
     *
     * @param id the id of the pathwaycountermeasurembr to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the pathwaycountermeasurembr, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/pathwaycountermeasurembrs/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Pathwaycountermeasurembr> getPathwaycountermeasurembr(@PathVariable Long id) {
        log.debug("REST request to get Pathwaycountermeasurembr : {}", id);
        Pathwaycountermeasurembr pathwaycountermeasurembr = pathwaycountermeasurembrRepository.findOne(id);
        return Optional.ofNullable(pathwaycountermeasurembr)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /pathwaycountermeasurembrs/:id : delete the "id" pathwaycountermeasurembr.
     *
     * @param id the id of the pathwaycountermeasurembr to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/pathwaycountermeasurembrs/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePathwaycountermeasurembr(@PathVariable Long id) {
        log.debug("REST request to delete Pathwaycountermeasurembr : {}", id);
        pathwaycountermeasurembrRepository.delete(id);
        pathwaycountermeasurembrSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("pathwaycountermeasurembr", id.toString())).build();
    }

    /**
     * SEARCH  /_search/pathwaycountermeasurembrs?query=:query : search for the pathwaycountermeasurembr corresponding
     * to the query.
     *
     * @param query the query of the pathwaycountermeasurembr search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/pathwaycountermeasurembrs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Pathwaycountermeasurembr>> searchPathwaycountermeasurembrs(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Pathwaycountermeasurembrs for query {}", query);
        Page<Pathwaycountermeasurembr> page = pathwaycountermeasurembrSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/pathwaycountermeasurembrs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
