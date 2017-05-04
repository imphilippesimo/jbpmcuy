package com.aft.jbpmcuy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aft.jbpmcuy.domain.Person;

/**
 * Spring Data JPA repository for the Person entity.
 */

public interface PersonRepository extends JpaRepository<Person, Long> {

	@Query("select person from Person person where person.user.id = (select user.id from User user where user.login= :userLogin)")
	Person findOneByUserLogin(@Param("userLogin") String userLogin);

}
