package com.aft.jbpmcuy.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.aft.jbpmcuy.JbpmcuyApp;
import com.aft.jbpmcuy.domain.Office;
import com.aft.jbpmcuy.domain.Person;
import com.aft.jbpmcuy.domain.User;
import com.aft.jbpmcuy.repository.OfficeRepository;
import com.aft.jbpmcuy.repository.PersonRepository;
import com.aft.jbpmcuy.service.dto.CircuitDTO;
import com.aft.jbpmcuy.service.dto.CircuitInstanceDTO;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JbpmcuyApp.class)
public class JbpmServiceTest {

	Logger logger = LoggerFactory.getLogger(JbpmServiceTest.class);

	@Autowired
	JBPMService jBPMService;

	@Autowired
	PersonRepository personRepository;

	@Autowired
	OfficeRepository officeRepository;

	@Autowired
	UserService userService;

	List<CircuitInstanceDTO> userCircuitInstances;
	RuntimeManager manager;
	RuntimeEngine engine;
	Person franklinp;
	Person jeannep;
	Person philippep;
	Office finances;
	Office sysadmins;
	Office softwares;

	@Before
	public void beforeTests() {
		createTestData();
		serviceMustBeReadyToStartCircuit();

	}

	// @After
	// public void afterTests() {
	// deleteTestData();
	// }

	// private void deleteTestData() {
	// officeRepository.delete(softwares);
	// officeRepository.delete(sysadmins);
	// officeRepository.delete(finances);
	//
	// personRepository.delete(franklinp);
	// personRepository.delete(philippep);
	// personRepository.delete(jeannep);
	//
	// userService.deleteUser("franklin");
	// userService.deleteUser("jeanne");
	// userService.deleteUser("philippe");
	//
	// }

	public void createTestData() {
		User franklin = userService.createUser("franklin", "franklin", "franklin", "franklin", "franklin.doe@localhost",
				"http://placehold.it/50x50", "en-US");

		User philippe = userService.createUser("philippe", "philippe", "philippe", "philippe", "philippe.doe@localhost",
				"http://placehold.it/50x50", "en-US");
		User jeanne = userService.createUser("jeanne", "jeanne", "Jeanne", "jeanne", "jeanne.doe@localhost",
				"http://placehold.it/50x50", "en-US");

		franklinp = new Person();
		franklinp.setUser(franklin);
		personRepository.save(franklinp);

		jeannep = new Person();
		jeannep.setUser(jeanne);
		personRepository.save(jeannep);

		philippep = new Person();
		philippep.setUser(philippe);
		personRepository.save(philippep);

		finances = new Office();
		finances.setName("finances");
		finances.addPerson(franklinp);
		finances.addPerson(jeannep);
		officeRepository.save(finances);

		sysadmins = new Office();
		sysadmins.setName("sysadmins");
		sysadmins.addPerson(philippep);
		officeRepository.save(sysadmins);

		softwares = new Office();
		softwares.setName("softwares");
		softwares.addPerson(philippep);
		softwares.addPerson(franklinp);
		officeRepository.save(softwares);

	}

	public void serviceMustBeReadyToStartCircuit() {
		manager = jBPMService.getRuntimeManager();
		engine = jBPMService.getRuntimeEngine();
		// start all available processes
		logger.info("Starting Circuits: \n");
		for (CircuitDTO circuit : jBPMService.getAllCircuits()) {
			logger.info("starting " + circuit.getCircuitName() + " ...");
			jBPMService.startCircuit(circuit, "jeanne", "x/y/z");
		}

	}

	@Test
	public void listTasksWithouterrors() {
		getTasksForUser("philippe");
		getTasksForUser("franklin");
	}

	/** Get users tasks **/
	public void getTasksForUser(String userName) {
		userCircuitInstances = new ArrayList<CircuitInstanceDTO>();
		for (CircuitInstanceDTO userInstance : jBPMService.getCircuitInstances(userName)) {
			userCircuitInstances.add(userInstance);
			logger.info("\n" + userInstance.toString());
		}
		logger.info("\n --------------------");
	}

}
