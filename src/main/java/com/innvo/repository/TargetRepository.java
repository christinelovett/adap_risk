package com.innvo.repository;

import com.innvo.domain.Target;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Target entity.
 */
@SuppressWarnings("unused")
public interface TargetRepository extends JpaRepository<Target,Long> {

    @Query("select distinct target from Target target left join fetch target.categories left join fetch target.subcategories")
    List<Target> findAllWithEagerRelationships();

    @Query("select target from Target target left join fetch target.categories left join fetch target.subcategories where target.id =:id")
    Target findOneWithEagerRelationships(@Param("id") Long id);

}
