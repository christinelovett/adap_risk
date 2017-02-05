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
import javax.jms.JMSException;

import org.json.JSONException;
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
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innvo.YamlReciever;
import com.innvo.domain.Category;
import com.innvo.domain.Pathwaycountermeasurembr;
import com.innvo.domain.Pathwaypathwaymbr;
import com.innvo.domain.Scenariopathwaymbr;
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
        public List<PathwayCountermeasureUtil> getPathway(@PathVariable Long id) throws IOException{
            log.debug("REST request to get logic operator : {}", id);
            List<PathwayCountermeasureUtil> pathwayCountermeasureUtils=new ArrayList<PathwayCountermeasureUtil>();
            List<Pathwaypathwaymbr>  pathwaypathwaymbrs=pathwaypathwaymbrRepository.findByParentpathwayId(id);
            for(Pathwaypathwaymbr pathwaypathwaymbr:pathwaypathwaymbrs){
            	   		     
            	List<Pathwaycountermeasurembr>  pathwaycountermeasurembr=pathwaycountermeasurembrRepository.findByPathwayId(pathwaypathwaymbr.getChildpathway().getId());
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
    @RequestMapping(value = "/getCounterMeasure/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
        @Timed
        public List<Pathwaycountermeasurembr> getCounterMeasure(@PathVariable Long id) {
            log.debug("REST request to get logic operator : {}", id);
            List<Pathwaycountermeasurembr>  pathwaycountermeasurembr=pathwaycountermeasurembrRepository.findByPathwayId(id);
            return pathwaycountermeasurembr;
      }
    
    /**
     * 
     * @throws IOException
     */
    @RequestMapping(value = "/getColor",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
        @Timed
        public void getColor() throws   IOException {
    	     AttackTreeResource receiver=new AttackTreeResource();
		     String fullfilename =URLDecoder.decode(receiver.getClass().getResource("/config/application-dev.yml").getFile(), "UTF-8");
		
		     YamlReader reader = new YamlReader(new FileReader(fullfilename));
		     Object fileContent = reader.read();
		     Map map = (Map) fileContent;
		
		     Object colors = map.get("colors");
		     //System.out.println("////////////////////////////////*****");
		     //System.out.println(colors);
		     ObjectMapper oMapper = new ObjectMapper();
		     Map<String, Object> maps = oMapper.convertValue(colors, Map.class);
		     //System.out.println(maps);
		     for(Map.Entry m:maps.entrySet()){  
		    //	   System.out.println(m.getKey()+" "+m.getValue());  
		    	 } 
		   //  System.out.println("/////////////////////////////////////");
		     
    }
    
    
}
