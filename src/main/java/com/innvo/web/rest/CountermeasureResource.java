package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Countermeasure;

import com.innvo.repository.CountermeasureRepository;
import com.innvo.repository.search.CountermeasureSearchRepository;
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
 * REST controller for managing Countermeasure.
 */
@RestController
@RequestMapping("/api")
public class CountermeasureResource {

    private final Logger log = LoggerFactory.getLogger(CountermeasureResource.class);
        
    @Inject
    private CountermeasureRepository countermeasureRepository;

    @Inject
    private CountermeasureSearchRepository countermeasureSearchRepository;

    /**
     * POST  /countermeasures : Create a new countermeasure.
     *
     * @param countermeasure the countermeasure to create
     * @return the ResponseEntity with status 201 (Created) and with body the new countermeasure, or with status 400 (Bad Request) if the countermeasure has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/countermeasures",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Countermeasure> createCountermeasure(@Valid @RequestBody Countermeasure countermeasure) throws URISyntaxException {
        log.debug("REST request to save Countermeasure : {}", countermeasure);
        if (countermeasure.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("countermeasure", "idexists", "A new countermeasure cannot already have an ID")).body(null);
        }
        Countermeasure result = countermeasureRepository.save(countermeasure);
        countermeasureSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/countermeasures/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("countermeasure", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /countermeasures : Updates an existing countermeasure.
     *
     * @param countermeasure the countermeasure to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated countermeasure,
     * or with status 400 (Bad Request) if the countermeasure is not valid,
     * or with status 500 (Internal Server Error) if the countermeasure couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/countermeasures",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Countermeasure> updateCountermeasure(@Valid @RequestBody Countermeasure countermeasure) throws URISyntaxException {
        log.debug("REST request to update Countermeasure : {}", countermeasure);
        if (countermeasure.getId() == null) {
            return createCountermeasure(countermeasure);
        }
        Countermeasure result = countermeasureRepository.save(countermeasure);
        countermeasureSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("countermeasure", countermeasure.getId().toString()))
            .body(result);
    }

    /**
     * GET  /countermeasures : get all the countermeasures.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of countermeasures in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/countermeasures",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Countermeasure>> getAllCountermeasures(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Countermeasures");
        Page<Countermeasure> page = countermeasureRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/countermeasures");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /countermeasures/:id : get the "id" countermeasure.
     *
     * @param id the id of the countermeasure to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the countermeasure, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/countermeasures/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Countermeasure> getCountermeasure(@PathVariable Long id) {
        log.debug("REST request to get Countermeasure : {}", id);
        Countermeasure countermeasure = countermeasureRepository.findOneWithEagerRelationships(id);
        return Optional.ofNullable(countermeasure)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /countermeasures/:id : delete the "id" countermeasure.
     *
     * @param id the id of the countermeasure to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/countermeasures/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteCountermeasure(@PathVariable Long id) {
        log.debug("REST request to delete Countermeasure : {}", id);
        countermeasureRepository.delete(id);
        countermeasureSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("countermeasure", id.toString())).build();
    }

    /**
     * SEARCH  /_search/countermeasures?query=:query : search for the countermeasure corresponding
     * to the query.
     *
     * @param query the query of the countermeasure search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/countermeasures",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Countermeasure>> searchCountermeasures(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Countermeasures for query {}", query);
        Page<Countermeasure> page = countermeasureSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/countermeasures");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
