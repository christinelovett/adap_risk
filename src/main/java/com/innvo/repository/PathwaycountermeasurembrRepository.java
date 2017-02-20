package com.innvo.repository;

import com.innvo.domain.Pathwaycountermeasurembr;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Pathwaycountermeasurembr entity.
 */
@SuppressWarnings("unused")
public interface PathwaycountermeasurembrRepository extends JpaRepository<Pathwaycountermeasurembr,Long> {
	
	List<Pathwaycountermeasurembr> findByPathwayId(long id);
}
