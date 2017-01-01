package com.innvo.repository;

import com.innvo.domain.Scenario;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Scenario entity.
 */
@SuppressWarnings("unused")
public interface ScenarioRepository extends JpaRepository<Scenario,Long> {

    @Query("select distinct scenario from Scenario scenario left join fetch scenario.categories left join fetch scenario.subcategories")
    List<Scenario> findAllWithEagerRelationships();

    @Query("select scenario from Scenario scenario left join fetch scenario.categories left join fetch scenario.subcategories where scenario.id =:id")
    Scenario findOneWithEagerRelationships(@Param("id") Long id);

}
