package com.innvo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Target;

import com.innvo.repository.TargetRepository;
import com.innvo.repository.search.TargetSearchRepository;
import com.innvo.web.rest.util.HeaderUtil;
import com.innvo.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
 * REST controller for managing Target.
 */
@RestController
@RequestMapping("/api")
public class TargetResource {

    private final Logger log = LoggerFactory.getLogger(TargetResource.class);
        
    @Inject
    private TargetRepository targetRepository;

    @Inject
    private TargetSearchRepository targetSearchRepository;

    /**
     * POST  /targets : Create a new target.
     *
     * @param target the target to create
     * @return the ResponseEntity with status 201 (Created) and with body the new target, or with status 400 (Bad Request) if the target has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/targets")
    @Timed
    public ResponseEntity<Target> createTarget(@Valid @RequestBody Target target) throws URISyntaxException {
        log.debug("REST request to save Target : {}", target);
        if (target.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("target", "idexists", "A new target cannot already have an ID")).body(null);
        }
        Target result = targetRepository.save(target);
        targetSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/targets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("target", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /targets : Updates an existing target.
     *
     * @param target the target to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated target,
     * or with status 400 (Bad Request) if the target is not valid,
     * or with status 500 (Internal Server Error) if the target couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/targets")
    @Timed
    public ResponseEntity<Target> updateTarget(@Valid @RequestBody Target target) throws URISyntaxException {
        log.debug("REST request to update Target : {}", target);
        if (target.getId() == null) {
            return createTarget(target);
        }
        Target result = targetRepository.save(target);
        targetSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("target", target.getId().toString()))
            .body(result);
    }

    /**
     * GET  /targets : get all the targets.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of targets in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/targets")
    @Timed
    public ResponseEntity<List<Target>> getAllTargets(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Targets");
        Page<Target> page = targetRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/targets");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /targets/:id : get the "id" target.
     *
     * @param id the id of the target to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the target, or with status 404 (Not Found)
     */
    @GetMapping("/targets/{id}")
    @Timed
    public ResponseEntity<Target> getTarget(@PathVariable Long id) {
        log.debug("REST request to get Target : {}", id);
        Target target = targetRepository.findOneWithEagerRelationships(id);
        return Optional.ofNullable(target)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /targets/:id : delete the "id" target.
     *
     * @param id the id of the target to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/targets/{id}")
    @Timed
    public ResponseEntity<Void> deleteTarget(@PathVariable Long id) {
        log.debug("REST request to delete Target : {}", id);
        targetRepository.delete(id);
        targetSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("target", id.toString())).build();
    }

    /**
     * SEARCH  /_search/targets?query=:query : search for the target corresponding
     * to the query.
     *
     * @param query the query of the target search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/targets")
    @Timed
    public ResponseEntity<List<Target>> searchTargets(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Targets for query {}", query);
        Page<Target> page = targetSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/targets");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
