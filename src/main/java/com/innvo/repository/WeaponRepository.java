package com.innvo.repository;

import com.innvo.domain.Weapon;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Weapon entity.
 */
@SuppressWarnings("unused")
public interface WeaponRepository extends JpaRepository<Weapon,Long> {

    @Query("select distinct weapon from Weapon weapon left join fetch weapon.categories left join fetch weapon.subcategories")
    List<Weapon> findAllWithEagerRelationships();

    @Query("select weapon from Weapon weapon left join fetch weapon.categories left join fetch weapon.subcategories where weapon.id =:id")
    Weapon findOneWithEagerRelationships(@Param("id") Long id);

}
