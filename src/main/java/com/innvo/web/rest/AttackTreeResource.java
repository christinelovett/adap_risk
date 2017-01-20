package com.innvo.web.rest;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.Category;
import com.innvo.domain.Pathwaycountermeasurembr;
import com.innvo.domain.Pathwaypathwaymbr;
import com.innvo.domain.Scenariopathwaymbr;
import com.innvo.repository.PathwaycountermeasurembrRepository;
import com.innvo.repository.PathwaypathwaymbrRepository;
import com.innvo.repository.ScenariopathwaymbrRepository;

/**
 * 
 * @author ali
 * REST controller for managing Attack Tree
 */
@RestController
@RequestMapping("/api")
public class AttackTreeResource {

    private final Logger log = LoggerFactory.getLogger(AttackTreeResource.class);

    @Inject
    private ScenariopathwaymbrRepository scenariopathwaymbrRepository;

    @Inject
    private PathwaypathwaymbrRepository pathwaypathwaymbrRepository;
    
    @Inject
    private PathwaycountermeasurembrRepository pathwaycountermeasurembrRepository; 
    
    
    /**
     * 
     * @param id
     * @return
     */
    @RequestMapping(value = "/getData/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
        @Timed
        public List<Scenariopathwaymbr> getData(@PathVariable Long id) {
            log.debug("REST request to get AttackTree : {}", id);
            List<Scenariopathwaymbr> scenariopathwaymbr=scenariopathwaymbrRepository.findByScenarioId(id);
            return scenariopathwaymbr;
        }
    
    /**
     * 
     * @param id
     * @return
     */
    @RequestMapping(value = "/getRoot/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
        @Timed
        public Scenariopathwaymbr getRoot(@PathVariable Long id) {
            log.debug("REST request to get AttackTree : {}", id);
            Scenariopathwaymbr scenariopathwaymbr=scenariopathwaymbrRepository.findByScenarioIdAndPathwayIsrootnode(id,true);
            return scenariopathwaymbr;
        }
    
    /**
     * 
     * @param id
     * @return
     */
    @RequestMapping(value = "/getPathway/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
        @Timed
        public List<Pathwaypathwaymbr> getPathway(@PathVariable Long id) {
            log.debug("REST request to get logic operator : {}", id);
            List<Pathwaypathwaymbr>  pathwaypathwaymbrs=pathwaypathwaymbrRepository.findByParentpathwayId(id);
            return pathwaypathwaymbrs;
      }
    
    /**
     * 
     * @param id
     * @return
     */
    @RequestMapping(value = "/getCounterMeasure/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
        @Timed
        public Pathwaycountermeasurembr getCounterMeasure(@PathVariable Long id) {
            log.debug("REST request to get logic operator : {}", id);
            Pathwaycountermeasurembr  pathwaycountermeasurembr=pathwaycountermeasurembrRepository.findByPathwayId(id);
            return pathwaycountermeasurembr;
      }
    
}
