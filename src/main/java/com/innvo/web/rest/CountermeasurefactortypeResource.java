package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Countermeasurefactortype;

import com.innvo.repository.CountermeasurefactortypeRepository;
import com.innvo.repository.search.CountermeasurefactortypeSearchRepository;
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
 * REST controller for managing Countermeasurefactortype.
 */
@RestController
@RequestMapping("/api")
public class CountermeasurefactortypeResource {

    private final Logger log = LoggerFactory.getLogger(CountermeasurefactortypeResource.class);
        
    @Inject
    private CountermeasurefactortypeRepository countermeasurefactortypeRepository;

    @Inject
    private CountermeasurefactortypeSearchRepository countermeasurefactortypeSearchRepository;

    /**
     * POST  /countermeasurefactortypes : Create a new countermeasurefactortype.
     *
     * @param countermeasurefactortype the countermeasurefactortype to create
     * @return the ResponseEntity with status 201 (Created) and with body the new countermeasurefactortype, or with status 400 (Bad Request) if the countermeasurefactortype has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/countermeasurefactortypes",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Countermeasurefactortype> createCountermeasurefactortype(@Valid @RequestBody Countermeasurefactortype countermeasurefactortype) throws URISyntaxException {
        log.debug("REST request to save Countermeasurefactortype : {}", countermeasurefactortype);
        if (countermeasurefactortype.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("countermeasurefactortype", "idexists", "A new countermeasurefactortype cannot already have an ID")).body(null);
        }
        Countermeasurefactortype result = countermeasurefactortypeRepository.save(countermeasurefactortype);
        countermeasurefactortypeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/countermeasurefactortypes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("countermeasurefactortype", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /countermeasurefactortypes : Updates an existing countermeasurefactortype.
     *
     * @param countermeasurefactortype the countermeasurefactortype to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated countermeasurefactortype,
     * or with status 400 (Bad Request) if the countermeasurefactortype is not valid,
     * or with status 500 (Internal Server Error) if the countermeasurefactortype couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/countermeasurefactortypes",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Countermeasurefactortype> updateCountermeasurefactortype(@Valid @RequestBody Countermeasurefactortype countermeasurefactortype) throws URISyntaxException {
        log.debug("REST request to update Countermeasurefactortype : {}", countermeasurefactortype);
        if (countermeasurefactortype.getId() == null) {
            return createCountermeasurefactortype(countermeasurefactortype);
        }
        Countermeasurefactortype result = countermeasurefactortypeRepository.save(countermeasurefactortype);
        countermeasurefactortypeSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("countermeasurefactortype", countermeasurefactortype.getId().toString()))
            .body(result);
    }

    /**
     * GET  /countermeasurefactortypes : get all the countermeasurefactortypes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of countermeasurefactortypes in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/countermeasurefactortypes",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Countermeasurefactortype>> getAllCountermeasurefactortypes(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Countermeasurefactortypes");
        Page<Countermeasurefactortype> page = countermeasurefactortypeRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/countermeasurefactortypes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /countermeasurefactortypes/:id : get the "id" countermeasurefactortype.
     *
     * @param id the id of the countermeasurefactortype to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the countermeasurefactortype, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/countermeasurefactortypes/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Countermeasurefactortype> getCountermeasurefactortype(@PathVariable Long id) {
        log.debug("REST request to get Countermeasurefactortype : {}", id);
        Countermeasurefactortype countermeasurefactortype = countermeasurefactortypeRepository.findOne(id);
        return Optional.ofNullable(countermeasurefactortype)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /countermeasurefactortypes/:id : delete the "id" countermeasurefactortype.
     *
     * @param id the id of the countermeasurefactortype to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/countermeasurefactortypes/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteCountermeasurefactortype(@PathVariable Long id) {
        log.debug("REST request to delete Countermeasurefactortype : {}", id);
        countermeasurefactortypeRepository.delete(id);
        countermeasurefactortypeSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("countermeasurefactortype", id.toString())).build();
    }

    /**
     * SEARCH  /_search/countermeasurefactortypes?query=:query : search for the countermeasurefactortype corresponding
     * to the query.
     *
     * @param query the query of the countermeasurefactortype search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/countermeasurefactortypes",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Countermeasurefactortype>> searchCountermeasurefactortypes(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Countermeasurefactortypes for query {}", query);
        Page<Countermeasurefactortype> page = countermeasurefactortypeSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/countermeasurefactortypes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
