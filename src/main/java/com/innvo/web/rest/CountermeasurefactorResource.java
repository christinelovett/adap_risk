package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Countermeasurefactor;

import com.innvo.repository.CountermeasurefactorRepository;
import com.innvo.repository.search.CountermeasurefactorSearchRepository;
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
 * REST controller for managing Countermeasurefactor.
 */
@RestController
@RequestMapping("/api")
public class CountermeasurefactorResource {

    private final Logger log = LoggerFactory.getLogger(CountermeasurefactorResource.class);
        
    @Inject
    private CountermeasurefactorRepository countermeasurefactorRepository;

    @Inject
    private CountermeasurefactorSearchRepository countermeasurefactorSearchRepository;

    /**
     * POST  /countermeasurefactors : Create a new countermeasurefactor.
     *
     * @param countermeasurefactor the countermeasurefactor to create
     * @return the ResponseEntity with status 201 (Created) and with body the new countermeasurefactor, or with status 400 (Bad Request) if the countermeasurefactor has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/countermeasurefactors",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Countermeasurefactor> createCountermeasurefactor(@Valid @RequestBody Countermeasurefactor countermeasurefactor) throws URISyntaxException {
        log.debug("REST request to save Countermeasurefactor : {}", countermeasurefactor);
        if (countermeasurefactor.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("countermeasurefactor", "idexists", "A new countermeasurefactor cannot already have an ID")).body(null);
        }
        Countermeasurefactor result = countermeasurefactorRepository.save(countermeasurefactor);
        countermeasurefactorSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/countermeasurefactors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("countermeasurefactor", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /countermeasurefactors : Updates an existing countermeasurefactor.
     *
     * @param countermeasurefactor the countermeasurefactor to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated countermeasurefactor,
     * or with status 400 (Bad Request) if the countermeasurefactor is not valid,
     * or with status 500 (Internal Server Error) if the countermeasurefactor couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/countermeasurefactors",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Countermeasurefactor> updateCountermeasurefactor(@Valid @RequestBody Countermeasurefactor countermeasurefactor) throws URISyntaxException {
        log.debug("REST request to update Countermeasurefactor : {}", countermeasurefactor);
        if (countermeasurefactor.getId() == null) {
            return createCountermeasurefactor(countermeasurefactor);
        }
        Countermeasurefactor result = countermeasurefactorRepository.save(countermeasurefactor);
        countermeasurefactorSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("countermeasurefactor", countermeasurefactor.getId().toString()))
            .body(result);
    }

    /**
     * GET  /countermeasurefactors : get all the countermeasurefactors.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of countermeasurefactors in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/countermeasurefactors",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Countermeasurefactor>> getAllCountermeasurefactors(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Countermeasurefactors");
        Page<Countermeasurefactor> page = countermeasurefactorRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/countermeasurefactors");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /countermeasurefactors/:id : get the "id" countermeasurefactor.
     *
     * @param id the id of the countermeasurefactor to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the countermeasurefactor, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/countermeasurefactors/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Countermeasurefactor> getCountermeasurefactor(@PathVariable Long id) {
        log.debug("REST request to get Countermeasurefactor : {}", id);
        Countermeasurefactor countermeasurefactor = countermeasurefactorRepository.findOneWithEagerRelationships(id);
        return Optional.ofNullable(countermeasurefactor)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /countermeasurefactors/:id : delete the "id" countermeasurefactor.
     *
     * @param id the id of the countermeasurefactor to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/countermeasurefactors/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteCountermeasurefactor(@PathVariable Long id) {
        log.debug("REST request to delete Countermeasurefactor : {}", id);
        countermeasurefactorRepository.delete(id);
        countermeasurefactorSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("countermeasurefactor", id.toString())).build();
    }

    /**
     * SEARCH  /_search/countermeasurefactors?query=:query : search for the countermeasurefactor corresponding
     * to the query.
     *
     * @param query the query of the countermeasurefactor search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/countermeasurefactors",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Countermeasurefactor>> searchCountermeasurefactors(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Countermeasurefactors for query {}", query);
        Page<Countermeasurefactor> page = countermeasurefactorSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/countermeasurefactors");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
