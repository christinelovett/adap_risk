package com.innvo.repository;

import com.innvo.domain.Pathway;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Pathway entity.
 */
@SuppressWarnings("unused")
public interface PathwayRepository extends JpaRepository<Pathway,Long> {

    @Query("select distinct pathway from Pathway pathway left join fetch pathway.categories left join fetch pathway.subcategories left join fetch pathway.weapons left join fetch pathway.targets")
    List<Pathway> findAllWithEagerRelationships();

    @Query("select pathway from Pathway pathway left join fetch pathway.categories left join fetch pathway.subcategories left join fetch pathway.weapons left join fetch pathway.targets where pathway.id =:id")
    Pathway findOneWithEagerRelationships(@Param("id") Long id);

}
