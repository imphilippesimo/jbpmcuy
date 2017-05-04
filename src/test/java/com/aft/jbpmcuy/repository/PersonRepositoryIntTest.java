package com.aft.jbpmcuy.repository;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.aft.jbpmcuy.JbpmcuyApp;
import com.aft.jbpmcuy.domain.Person;
import com.aft.jbpmcuy.service.UserService;

/**
 * Test class for the PersonRespository .
 *
 * @see PersonRepository
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JbpmcuyApp.class)
@Transactional
public class PersonRepositoryIntTest {

	// private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	PersonRepository personRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserService userService;

	@Test
	public void assertThatPersonFromUserLoginIsNotNull() {

		// User philippe =
		userService.createUser("philippe", "philippe", "philippe", "philippe", "philippe.doe@localhost",
				"http://placehold.it/50x50", "en-US");

		Person person = new Person();
		person.setUser(userRepository.findOneByLogin("philippe").get());
		// person.setUser(philippe);

		personRepository.save(person);

		Person foundPerson = personRepository.findOneByUserLogin("philippe");

		System.out.println(foundPerson.getId().toString());

		assertTrue(foundPerson != null);
	}

}
