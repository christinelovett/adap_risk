package com.innvo.repository;

import com.innvo.domain.Scenariopathwaymbr;

import org.springframework.data.jpa.repository.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data JPA repository for the Scenariopathwaymbr entity.
 */
@SuppressWarnings("unused")
public interface ScenariopathwaymbrRepository extends JpaRepository<Scenariopathwaymbr,Long> {

	Scenariopathwaymbr findByScenarioIdAndPathwayIsrootnode(long id,boolean isrootnode);

	List<Scenariopathwaymbr> findByScenarioId(long id);
	
	@Transactional
	void deleteByScenarioIdAndPathwayId(long scenarioId,long pathwayId);                     

}
