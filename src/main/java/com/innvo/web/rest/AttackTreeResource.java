package com.innvo.web.rest;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
//import javax.jms.JMSException;
import javax.validation.Valid;

//import org.boon.core.Sys;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innvo.YamlReciever;
import com.innvo.domain.Category;
import com.innvo.domain.Pathway;
import com.innvo.domain.Pathwaycountermeasurembr;
import com.innvo.domain.Pathwaypathwaymbr;
import com.innvo.domain.Scenariopathwaymbr;
import com.innvo.repository.PathwayRepository;
import com.innvo.repository.PathwaycountermeasurembrRepository;
import com.innvo.repository.PathwaypathwaymbrRepository;
import com.innvo.repository.ScenariopathwaymbrRepository;
import com.innvo.web.rest.util.PathwayCountermeasureUtil;

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
    private PathwayRepository pathwayRepository;
    
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
    @RequestMapping(value = "/getPathway/{parentId}/{scenarioId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
        @Timed
        public List<PathwayCountermeasureUtil> getPathway(@PathVariable("parentId") Long parentId,@PathVariable("scenarioId") Long scenarioId) throws IOException{
            log.debug("REST request to get logic operator : {}", scenarioId);
            List<PathwayCountermeasureUtil> pathwayCountermeasureUtils=new ArrayList<PathwayCountermeasureUtil>();
            List<Pathwaypathwaymbr>  pathwaypathwaymbrs=pathwaypathwaymbrRepository.findByParentpathwayIdAndScenarioId(parentId, scenarioId);
            for(Pathwaypathwaymbr pathwaypathwaymbr:pathwaypathwaymbrs){
            	   		     
            	List<Pathwaycountermeasurembr>  pathwaycountermeasurembr=pathwaycountermeasurembrRepository.findByPathwayIdAndScenarioId(pathwaypathwaymbr.getChildpathway().getId(),scenarioId);
                PathwayCountermeasureUtil pathwayCountermeasureUtil=new PathwayCountermeasureUtil();
                pathwayCountermeasureUtil.setPathwaypathwaymbr(pathwaypathwaymbr);
                pathwayCountermeasureUtil.setPathwaycountermeasurembrs(pathwaycountermeasurembr);
                
                
                
                 YamlReciever yamlReciever=new YamlReciever();
      		     ObjectMapper oMapper = new ObjectMapper();
      		     Map<String, Object> maps = oMapper.convertValue(yamlReciever.getData(), Map.class);
      		   
      		     String color = (String) maps.get(pathwaypathwaymbr.getChildpathway().getRecordtype().getName());
		    	 pathwayCountermeasureUtil.setColor(color);
                 pathwayCountermeasureUtils.add(pathwayCountermeasureUtil);
            }
            
            return pathwayCountermeasureUtils;
      }
    
    
    /**
     * 
     * @param id
     * @return
     */
    /**
    @RequestMapping(value = "/getCounterMeasure/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
        @Timed
        public List<Pathwaycountermeasurembr> getCounterMeasure(@PathVariable Long id) {
            log.debug("REST request to get logic operator : {}", id);
            List<Pathwaycountermeasurembr>  pathwaycountermeasurembr=pathwaycountermeasurembrRepository.findByPathwayId(id);
            return pathwaycountermeasurembr;
      }
    **/
    
    /**
     * 
     * @param recordtype
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getColor/{recordtype}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
        @Timed
        public String getColor(@PathVariable String recordtype) throws   IOException {
    	     YamlReciever yamlReciever=new YamlReciever();
		     ObjectMapper oMapper = new ObjectMapper();
		     Map<String, Object> maps = oMapper.convertValue(yamlReciever.getData(), Map.class);
		     String color = (String) maps.get(recordtype);	
		     System.out.println(color);
		     return color;
    }
    
    
    /**
     * 
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getPathways",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
        @Timed
        public List<Pathway> getPathways() throws   IOException {
        List<Pathway>  pathways=pathwayRepository.findAll();
        return pathways;
    }
    
 
    /**
     * 
     * @param scenarioId
     * @param parentId
     * @param childId
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/getLineData/{scenarioId}/{parentId}/{childId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
        @Timed
        public Pathwaypathwaymbr getLineData(@PathVariable("scenarioId") Long scenarioId,@PathVariable("parentId") Long parentId,
        		  @PathVariable("childId") Long childId) throws   IOException {

    	Pathwaypathwaymbr pathwaypathwaymbr= pathwaypathwaymbrRepository.findByScenarioIdAndParentpathwayIdAndChildpathwayId(scenarioId, parentId, childId);
    	return pathwaypathwaymbr;
    }
    
    
    @RequestMapping(value = "/removeRoot/{scenarioId}/{pathwayId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
        @Timed
        public void removeRoot(@PathVariable("scenarioId") Long scenarioId,@PathVariable("pathwayId") Long pathwayId) throws   IOException {

    	scenariopathwaymbrRepository.deleteByScenarioIdAndPathwayId(scenarioId, pathwayId);

    }
    
    /**
     * 
     * @param scenarioId
     * @param parentId
     * @param childId
     * @throws IOException
     */
    @RequestMapping(value = "/removeLine/{scenarioId}/{parentId}/{childId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
        @Timed
        public void removeLine(@PathVariable("scenarioId") Long scenarioId,@PathVariable("parentId") Long parentId,
        		  @PathVariable("childId") Long childId) throws   IOException {

    	pathwaypathwaymbrRepository.deleteByScenarioIdAndParentpathwayIdAndChildpathwayId(scenarioId, parentId, childId);

    }
      
    /**
     * 
     * @param name
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/pathwayByRecordtype/{name}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
        @Timed
        public List<Pathway> getPathwayByRecordtype(@PathVariable("name") String name) throws   IOException {

    	List<Pathway> pathways= pathwayRepository.findByRecordtypeName(name);
    	return pathways;
    }
}
