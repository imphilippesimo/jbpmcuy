package com.aft.jbpmcuy.web.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aft.jbpmcuy.repository.OfficeRepository;
import com.aft.jbpmcuy.security.SecurityUtils;
import com.aft.jbpmcuy.service.JBPMService;
import com.aft.jbpmcuy.service.UserService;
import com.aft.jbpmcuy.service.dto.CircuitDTO;
import com.aft.jbpmcuy.service.dto.CircuitInstanceDTO;
import com.aft.jbpmcuy.service.dto.CircuitStepDTO;
import com.aft.jbpmcuy.web.rest.errors.CustomParameterizedException;
import com.aft.jbpmcuy.web.rest.errors.ErrorConstants;
import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing circuits.
 */
@RestController
@RequestMapping("/api")
public class JBPMResource {

	private final Logger log = LoggerFactory.getLogger(DocumentResource.class);

	private final JBPMService jbpmService;
	private static final String APPLICATION_CIRCUITS = "circuits";

	HttpSession session;

	// private String currentLogin;

	// private final UserService userService;
	//
	// private final OfficeRepository officeRepository;

	public JBPMResource(JBPMService jbpmService, UserService userService, OfficeRepository officeRepository) {

		this.jbpmService = jbpmService;
		// this.userService = userService;
		// this.officeRepository = officeRepository;
	}

	@GetMapping("/circuits")
	@Timed
	public List<CircuitDTO> getAllCircuits(HttpServletRequest request) {
		log.debug("REST request to get all exising circuits in the database");
		session = request.getSession();
		List<CircuitDTO> circuits = jbpmService.getAllCircuits();
		session.setAttribute(APPLICATION_CIRCUITS, circuits);
		return circuits;
	}

	@GetMapping("/circuits/start")
	@Timed
	public void startCircuit(@RequestParam(name = "circuitId") String circuitId,
			@RequestParam(name = "docRef") String docRef) {
		log.debug("REST request to start a given circuit");
		jbpmService.startCircuit(circuitId, SecurityUtils.getCurrentUserLogin(), docRef);

	}

	@GetMapping("/circuits/instances")
	@Timed
	public List<CircuitInstanceDTO> getUserTasks() {
		log.debug("REST request to get a given user awaiting's tasks");
		List<CircuitInstanceDTO> circuitInstances = jbpmService
				.getCircuitInstances(SecurityUtils.getCurrentUserLogin());
		return circuitInstances;

	}

	@GetMapping("/circuits/task/start")
	@Timed
	public void startTask(@RequestParam Long instanceId) {
		log.debug("REST request to start the current availaible task of a given instance");

		try {

			CircuitInstanceDTO currentCircuitInstance = jbpmService.getCircuitInstanceById(instanceId,
					SecurityUtils.getCurrentUserLogin());
			jbpmService.startCurrentStep(currentCircuitInstance.getCurrentStep(), SecurityUtils.getCurrentUserLogin());

		} catch (Exception e) {
			log.info("error", e);
			throw new CustomParameterizedException(ErrorConstants.ERR_START_TASK_SERVER_ERROR,
					ErrorConstants.ERR_START_TASK_SERVER_ERROR_DESCR, String.valueOf(instanceId));
		}

	}

	@GetMapping("/circuits/task/complete")
	@Timed
	public void completeTask(@RequestParam Long instanceId) {
		log.debug("REST request to complete a given task by doign a given action");

		try {
			CircuitInstanceDTO currentCircuitInstance = jbpmService.getCircuitInstanceById(instanceId,
					SecurityUtils.getCurrentUserLogin());
			jbpmService.goToNextStep(currentCircuitInstance, SecurityUtils.getCurrentUserLogin());

		} catch (Exception e) {
			throw new CustomParameterizedException(ErrorConstants.ERR_COMPLETE_TASK_SERVER_ERROR,
					ErrorConstants.ERR_COMPLETE_TASK_SERVER_ERROR_DESC, String.valueOf(instanceId));
		}

	}

	@GetMapping("/circuits/task/completed")
	@Timed
	public List<CircuitStepDTO> getCompletedTask(@RequestParam Long instanceId) {
		log.debug("REST request to get completed steps for a given circuit instance");
		CircuitInstanceDTO currentCircuitInstance = jbpmService.getCircuitInstanceById(instanceId,
				SecurityUtils.getCurrentUserLogin());
		return jbpmService.getCompletedStepsByProcessInstanceId(currentCircuitInstance);

	}

}
