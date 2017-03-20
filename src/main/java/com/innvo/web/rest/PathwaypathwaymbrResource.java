package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Pathwaypathwaymbr;

import com.innvo.repository.PathwaypathwaymbrRepository;
import com.innvo.repository.search.PathwaypathwaymbrSearchRepository;
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
 * REST controller for managing Pathwaypathwaymbr.
 */
@RestController
@RequestMapping("/api")
public class PathwaypathwaymbrResource {

    private final Logger log = LoggerFactory.getLogger(PathwaypathwaymbrResource.class);
        
    @Inject
    private PathwaypathwaymbrRepository pathwaypathwaymbrRepository;

    @Inject
    private PathwaypathwaymbrSearchRepository pathwaypathwaymbrSearchRepository;

    /**
     * POST  /pathwaypathwaymbrs : Create a new pathwaypathwaymbr.
     *
     * @param pathwaypathwaymbr the pathwaypathwaymbr to create
     * @return the ResponseEntity with status 201 (Created) and with body the new pathwaypathwaymbr, or with status 400 (Bad Request) if the pathwaypathwaymbr has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/pathwaypathwaymbrs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Pathwaypathwaymbr> createPathwaypathwaymbr(@Valid @RequestBody Pathwaypathwaymbr pathwaypathwaymbr) throws URISyntaxException {
        log.debug("REST request to save Pathwaypathwaymbr : {}", pathwaypathwaymbr);
        if (pathwaypathwaymbr.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("pathwaypathwaymbr", "idexists", "A new pathwaypathwaymbr cannot already have an ID")).body(null);
        }
        System.out.println(pathwaypathwaymbr.getScenario());
        System.out.println(pathwaypathwaymbr.getParentpathway());
        System.out.println(pathwaypathwaymbr.getChildpathway());
        Pathwaypathwaymbr result = pathwaypathwaymbrRepository.save(pathwaypathwaymbr);
        pathwaypathwaymbrSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/pathwaypathwaymbrs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("pathwaypathwaymbr", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /pathwaypathwaymbrs : Updates an existing pathwaypathwaymbr.
     *
     * @param pathwaypathwaymbr the pathwaypathwaymbr to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated pathwaypathwaymbr,
     * or with status 400 (Bad Request) if the pathwaypathwaymbr is not valid,
     * or with status 500 (Internal Server Error) if the pathwaypathwaymbr couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/pathwaypathwaymbrs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Pathwaypathwaymbr> updatePathwaypathwaymbr(@Valid @RequestBody Pathwaypathwaymbr pathwaypathwaymbr) throws URISyntaxException {
        log.debug("REST request to update Pathwaypathwaymbr : {}", pathwaypathwaymbr);
        if (pathwaypathwaymbr.getId() == null) {
            return createPathwaypathwaymbr(pathwaypathwaymbr);
        }
        Pathwaypathwaymbr result = pathwaypathwaymbrRepository.save(pathwaypathwaymbr);
        pathwaypathwaymbrSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("pathwaypathwaymbr", pathwaypathwaymbr.getId().toString()))
            .body(result);
    }

    /**
     * GET  /pathwaypathwaymbrs : get all the pathwaypathwaymbrs.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of pathwaypathwaymbrs in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/pathwaypathwaymbrs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Pathwaypathwaymbr>> getAllPathwaypathwaymbrs(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Pathwaypathwaymbrs");
        Page<Pathwaypathwaymbr> page = pathwaypathwaymbrRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/pathwaypathwaymbrs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /pathwaypathwaymbrs/:id : get the "id" pathwaypathwaymbr.
     *
     * @param id the id of the pathwaypathwaymbr to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the pathwaypathwaymbr, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/pathwaypathwaymbrs/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Pathwaypathwaymbr> getPathwaypathwaymbr(@PathVariable Long id) {
        log.debug("REST request to get Pathwaypathwaymbr : {}", id);
        Pathwaypathwaymbr pathwaypathwaymbr = pathwaypathwaymbrRepository.findOne(id);
        return Optional.ofNullable(pathwaypathwaymbr)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /pathwaypathwaymbrs/:id : delete the "id" pathwaypathwaymbr.
     *
     * @param id the id of the pathwaypathwaymbr to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/pathwaypathwaymbrs/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePathwaypathwaymbr(@PathVariable Long id) {
        log.debug("REST request to delete Pathwaypathwaymbr : {}", id);
        pathwaypathwaymbrRepository.delete(id);
        pathwaypathwaymbrSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("pathwaypathwaymbr", id.toString())).build();
    }

    /**
     * SEARCH  /_search/pathwaypathwaymbrs?query=:query : search for the pathwaypathwaymbr corresponding
     * to the query.
     *
     * @param query the query of the pathwaypathwaymbr search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/pathwaypathwaymbrs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Pathwaypathwaymbr>> searchPathwaypathwaymbrs(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Pathwaypathwaymbrs for query {}", query);
        Page<Pathwaypathwaymbr> page = pathwaypathwaymbrSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/pathwaypathwaymbrs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
