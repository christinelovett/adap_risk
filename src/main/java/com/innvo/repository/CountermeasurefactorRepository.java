package com.innvo.repository;

import com.innvo.domain.Countermeasurefactor;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Countermeasurefactor entity.
 */
@SuppressWarnings("unused")
public interface CountermeasurefactorRepository extends JpaRepository<Countermeasurefactor,Long> {

}
