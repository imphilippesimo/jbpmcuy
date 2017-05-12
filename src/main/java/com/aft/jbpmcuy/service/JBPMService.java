package com.aft.jbpmcuy.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.jbpm.workflow.core.node.CompositeContextNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.kie.api.KieBase;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.task.api.InternalTaskService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import com.aft.jbpmcuy.domain.Document;
import com.aft.jbpmcuy.repository.DocumentRepository;
import com.aft.jbpmcuy.repository.OfficeRepository;
import com.aft.jbpmcuy.repository.PersonRepository;
import com.aft.jbpmcuy.repository.UserRepository;
import com.aft.jbpmcuy.service.dto.CircuitDTO;
import com.aft.jbpmcuy.service.dto.CircuitInstanceDTO;
import com.aft.jbpmcuy.service.dto.CircuitStepDTO;
import com.aft.jbpmcuy.service.util.DBUserGroupCallBack;

/**
 * @author root
 * 
 */
// TODO: Do not forget to handle/personalize exceptions for this service
@Service
@Transactional
public class JBPMService implements DisposableBean {

	/**
	 * 
	 */
	// private static final long serialVersionUID = 1L;
	static Logger logger = Logger.getLogger(JBPMService.class);
	private ApplicationContext context = new ClassPathXmlApplicationContext("spring-jBPMConfig.xml");
	private PlatformTransactionManager tm;
	private EntityManagerFactory emf;
	private RuntimeEnvironmentBuilder runtimeEnvBuilder;
	private RuntimeManager manager;
	private String workflowDefsPath = "workflows";
	private RuntimeEnvironment runtimeEnvironment;
	private KieBase kBase;

	private OfficeRepository officeRepository;

	private UserRepository userRepository;

	private PersonRepository personRepository;

	private DocumentRepository documentRepository;

	public JBPMService(OfficeRepository officeRepository, UserRepository userRepository,
			PersonRepository personRepository, DocumentRepository documentRepository) throws IOException {

		this.officeRepository = officeRepository;
		this.userRepository = userRepository;
		this.personRepository = personRepository;
		this.documentRepository = documentRepository;
		// Getting the transaction manager to do jBPM persistence
		// transactions
		tm = (PlatformTransactionManager) context.getBean("transactionManager");

		logger.info(this.officeRepository.toString());

		// Getting the entity manager factory to set the persistence
		emf = (EntityManagerFactory) context.getBean("entityManagerFactory");

		// Setting the jBPM runtime environment builder (to be done once during
		// app life time)
		runtimeEnvBuilder = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder().entityManagerFactory(emf)
				.addEnvironmentEntry(EnvironmentName.TRANSACTION_MANAGER, tm).userGroupCallback(
						new DBUserGroupCallBack(this.officeRepository, this.userRepository, this.personRepository));

		runtimeEnvironment = runtimeEnvBuilder.get();

		// this operation already sets the runtime manager
		addWorkflowDefs(workflowDefsPath);

		kBase = runtimeEnvironment.getKieBase();

	}

	// public void disposeJBPMResources() {
	// manager.disposeRuntimeEngine(getRuntimeEngine());
	// }

	public RuntimeManager getRuntimeManager() {
		return manager;
	}

	private void setRuntimeManager() {
		manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(runtimeEnvironment);
	}

	public List<Node> getHumanTaskNodes(String processIdAsString) {

		List<Node> humanTasks = new ArrayList<Node>();
		WorkflowProcess workflowProcess = (WorkflowProcess) runtimeEnvironment.getKieBase()
				.getProcess(processIdAsString);

		for (Node node : workflowProcess.getNodes()) {
			if (node instanceof CompositeContextNode) {
				for (Node inNode : ((CompositeContextNode) node).getNodes())
					if (inNode instanceof HumanTaskNode)

						humanTasks.add(inNode);

			} else if (node instanceof HumanTaskNode)
				humanTasks.add(node);

		}
		return humanTasks;

	}

	private void addWorkflowDefs(String workflowDefsPath) throws IOException {

		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		// get the set of all bpmn resources under
		// classpath/workflowDefsPath
		Resource[] resources = resolver.getResources("/" + workflowDefsPath + "/**");

		// for each resource definition get the process file name
		for (Resource resource : resources)

			runtimeEnvBuilder.addAsset(
					ResourceFactory.newClassPathResource(workflowDefsPath + "/" + resource.getFilename()),
					ResourceType.BPMN2);
		setRuntimeManager();

	}

	public List<CircuitInstanceDTO> getCircuitInstances(String potentialOwner) {

		// list of signature circuit instances on which <user> can act
		List<CircuitInstanceDTO> results = new ArrayList<CircuitInstanceDTO>();

		CircuitInstanceDTO sCircuitIns = null;

		// Getting the runtime engine out of the runtime manager
		RuntimeEngine engine = getRuntimeEngine();

		// Process instance Starter
		String starter = null;

		// Process instance document reference
		String documentRef = null;

		// Setting the task Service from the runtime engine
		TaskService taskService = engine.getTaskService();

		AuditService auditService = getAuditService();

		// List of active process instances
		List<? extends ProcessInstanceLog> processesList;

		// For each signature circuit...
		for (CircuitDTO circuit : getAllCircuits()) {

			// get its active process instances list
			processesList = auditService.findActiveProcessInstances(circuit.getCircuitId());

			// For each active process instance...
			for (ProcessInstanceLog pInstance : processesList) {

				/** get the process properties and feed the processInstance **/

				if (!(auditService.findVariableInstances(pInstance.getProcessInstanceId(), "starter").isEmpty())
						&& !(auditService.findVariableInstances(pInstance.getProcessInstanceId(), "documentRef")
								.isEmpty())) {

					List<? extends VariableInstanceLog> starters = auditService
							.findVariableInstances(pInstance.getProcessInstanceId(), "starter");

					// get the last(the most up to date) starter
					starter = starters.get(starters.size() - 1).getValue();

					List<? extends VariableInstanceLog> documents = auditService
							.findVariableInstances(pInstance.getProcessInstanceId(), "documentRef");

					// get the last(the most up to date) documentRef
					documentRef = documents.get(starters.size() - 1).getValue();
				}

				// List<CircuitStepDTO> previousSteps =
				// getCompletedStepsByProcessInstanceId(
				// pInstance.getProcessInstanceId());

				sCircuitIns = new CircuitInstanceDTO(pInstance, starter, documentRef, circuit, null);

				// among <user>'s awaiting task in the base...
				for (TaskSummary awaitingTask : taskService.getTasksAssignedAsPotentialOwner(potentialOwner, null)) {
					// logger.debug(awaitingTask);

					// keep the one belonging to this process instance
					if ((awaitingTask.getProcessInstanceId().equals(pInstance.getProcessInstanceId()))) {

						sCircuitIns.setCurrentStep(
								new CircuitStepDTO(circuit, awaitingTask, getStepContent(awaitingTask.getId())));
					}

				}
				// if there is no current step for <user>, he is not
				// involved on this instance right now
				if (sCircuitIns.getCurrentStep() != null)
					results.add(sCircuitIns);

			}
		}

		// getRuntimeManager().close();

		// returning the list of <user>'s circuit instances
		return results;

	}

	public RuntimeEngine getRuntimeEngine() {

		return getRuntimeManager().getRuntimeEngine(EmptyContext.get());
	}

	public List<CircuitDTO> getAllCircuits() {

		List<CircuitDTO> results = new ArrayList<CircuitDTO>();

		for (Process process : kBase.getProcesses())
			results.add(new CircuitDTO(process.getName(), process.getId()));

		return results;
	}

	private AuditService getAuditService() {

		return getRuntimeEngine().getAuditService();
	}

	public void startCircuit(String circuitId, String starter, String documentRef) {

		// Map of process instance variables
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("starter", starter);
		properties.put("documentRef", documentRef);

		// start the process with these variables
		getkieSession().startProcess(circuitId, properties);

	}

	private KieSession getkieSession() {
		return getRuntimeEngine().getKieSession();
	}

	public void startCurrentStep(CircuitStepDTO currentStep, String starter) {

		// Getting the task service out of the runtime manager
		TaskService taskService = getTaskService();

		// claiming the task
		taskService.claim(currentStep.getStepTask().getId(), starter);

		// starting the task
		taskService.start(currentStep.getStepTask().getId(), starter);

		// Now the task is in 'InProgress' state ...

	}

	private InternalTaskService getTaskService() {

		return (InternalTaskService) getRuntimeEngine().getTaskService();
	}

	public CircuitInstanceDTO getCircuitInstanceById(Long circuitInstanceId, String potentialOwner) {
		for (CircuitInstanceDTO cInstance : getCircuitInstances(potentialOwner))
			if (cInstance.getProcessInstanceInfo().getProcessInstanceId().equals(circuitInstanceId))
				return cInstance;
		return null;
	}

	public void goToNextStep(CircuitInstanceDTO currentCircuitInstance, String userName) {

		// Get the document Reference
		String docRef = currentCircuitInstance.getDocumentRef();

		// Get the document Object
		Document document = documentRepository.findOneByDocRef(docRef);

		CircuitStepDTO currentStep = currentCircuitInstance.getCurrentStep();

		// Get the action to be done
		String action = currentStep.getStepTask().getName();

		// Simulate checking logic
		if (action.contains("technique")) {
			document.setTreated(Boolean.TRUE);
		} else {
			document.setValidated(Boolean.TRUE);
		}
		documentRepository.save(document);

		// Getting the task service out of the runtime manager
		TaskService taskService = getTaskService();
		taskService.complete(currentStep.getStepTask().getId(), userName, null);

	}

	public Map<String, Object> getStepContent(Long taskId) {

		Map<String, Object> result = ((InternalTaskService) getTaskService()).getTaskContent(taskId);
		for (String entry : result.keySet())
			logger.info(entry + " : " + result.get(entry));

		return result;

	}

	// Retrieve step execution history
	public List<CircuitStepDTO> getCompletedStepsByProcessInstanceId(CircuitInstanceDTO instance) {

		List<TaskSummary> tasks = getTaskService()
				.getCompletedTasksByProcessId(instance.getProcessInstanceInfo().getProcessInstanceId());
		List<CircuitStepDTO> result = new ArrayList<CircuitStepDTO>();

		// we store task creation dates to set them as previous tasks end dates
		List<Date> taskEndDates = new ArrayList<Date>();

		for (TaskSummary taskSummary : tasks) {
			logger.info("Task completed : " + taskSummary.toString());
			String circuitId = taskSummary.getProcessId();
			// storing the creation date (it will be the previous task end date)
			taskEndDates.add(taskSummary.getCreatedOn());
			CircuitDTO circuit = getCircuitById(circuitId);
			result.add(new CircuitStepDTO(circuit, taskSummary, getStepContent(taskSummary.getId())));
		}

		// add the creation date of the next non completed task, to be set as
		// the end date of the last completed task
		taskEndDates.add(instance.getCurrentStep().getStepTask().getCreatedOn());

		// update the completed tasks while setting their end dates
		for (int i = 0; i < result.size(); i++) {
			CircuitStepDTO step = result.get(i);
			step.setEndDate(taskEndDates.get(i + 1));
			result.set(i, step);
		}

		return result;

	}

	public CircuitDTO getCircuitById(String circuitId) {
		for (CircuitDTO circuit : getAllCircuits())
			if (circuit.getCircuitId().equals(circuitId))
				return circuit;
		return null;
	}

	@Override
	public void destroy() throws Exception {
		logger.debug("\n=====Closing manager ...");
		manager.close();

	}

}
