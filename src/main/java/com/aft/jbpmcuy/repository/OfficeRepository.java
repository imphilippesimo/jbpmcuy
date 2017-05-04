package com.aft.jbpmcuy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aft.jbpmcuy.domain.Office;

/**
 * Spring Data JPA repository for the Office entity.
 */

public interface OfficeRepository extends JpaRepository<Office, Long> {

	@Query("select distinct office from Office office left join fetch office.people")
	List<Office> findAllWithEagerRelationships();

	@Query("select office from Office office left join fetch office.people where office.id =:id")
	Office findOneWithEagerRelationships(@Param("id") Long id);

	Optional<Office> findOneByName(String officeName);

}
