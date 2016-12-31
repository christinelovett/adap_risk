package com.innvo.repository;

import com.innvo.domain.Countermeasurefactor;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Countermeasurefactor entity.
 */
@SuppressWarnings("unused")
public interface CountermeasurefactorRepository extends JpaRepository<Countermeasurefactor,Long> {

    @Query("select distinct countermeasurefactor from Countermeasurefactor countermeasurefactor left join fetch countermeasurefactor.weapons")
    List<Countermeasurefactor> findAllWithEagerRelationships();

    @Query("select countermeasurefactor from Countermeasurefactor countermeasurefactor left join fetch countermeasurefactor.weapons where countermeasurefactor.id =:id")
    Countermeasurefactor findOneWithEagerRelationships(@Param("id") Long id);

}
