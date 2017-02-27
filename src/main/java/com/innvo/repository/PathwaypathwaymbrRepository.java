package com.innvo.repository;

import com.innvo.domain.Pathwaypathwaymbr;
import com.innvo.domain.Scenariopathwaymbr;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Pathwaypathwaymbr entity.
 */
@SuppressWarnings("unused")
public interface PathwaypathwaymbrRepository extends JpaRepository<Pathwaypathwaymbr,Long> {

	List<Pathwaypathwaymbr> findByParentpathwayId(long id);

	Pathwaypathwaymbr findByScenarioIdAndParentpathwayIdAndChildpathwayId(long scenarioId,long parentId,long childId);
}
