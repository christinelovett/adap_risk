package com.innvo.repository;

import com.innvo.domain.Countermeasure;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Countermeasure entity.
 */
@SuppressWarnings("unused")
public interface CountermeasureRepository extends JpaRepository<Countermeasure,Long> {

    @Query("select distinct countermeasure from Countermeasure countermeasure left join fetch countermeasure.categories left join fetch countermeasure.subcategories")
    List<Countermeasure> findAllWithEagerRelationships();

    @Query("select countermeasure from Countermeasure countermeasure left join fetch countermeasure.categories left join fetch countermeasure.subcategories where countermeasure.id =:id")
    Countermeasure findOneWithEagerRelationships(@Param("id") Long id);

}
