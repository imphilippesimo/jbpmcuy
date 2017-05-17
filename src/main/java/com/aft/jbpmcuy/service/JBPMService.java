package com.aft.jbpmcuy.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
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
import com.aft.jbpmcuy.domain.Office;
import com.aft.jbpmcuy.domain.Person;
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
	private InternalTaskService taskService;

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

	public List<ProcessInstanceLog> getAllCircuitInstances() {
		List<ProcessInstanceLog> instances = new ArrayList<ProcessInstanceLog>();
		for (CircuitDTO circuit : getAllCircuits())
			instances.addAll(getAuditService().findProcessInstances(circuit.getCircuitId()));
		return instances;
	}

	public List<CircuitInstanceDTO> getCircuitInstances(String potentialOwner) {

		// list of signature circuit instances on which <user> can act
		List<CircuitInstanceDTO> results = new ArrayList<CircuitInstanceDTO>();

		CircuitInstanceDTO sCircuitIns = null;

		// Process instance Starter
		String starter = null;

		// Process instance document reference
		String documentRef = null;

		// Setting the task Service from the runtime engine
		TaskService taskService = getTaskService();

		AuditService auditService = getAuditService();

		// List of active process instances
		List<? extends ProcessInstanceLog> processesList;

		// List of tasks in the base
		// List<Task> tasks = new ArrayList<Task>();

		// List of use's tasks
		// List<Task> userTasks = new ArrayList<Task>();

		// For each signature circuit...
		for (CircuitDTO circuit : getAllCircuits()) {

			// get its active process instances list
			// processesList =
			// auditService.findActiveProcessInstances(circuit.getCircuitId());
			processesList = auditService.findProcessInstances(circuit.getCircuitId());

			// For each active process instance...
			for (ProcessInstanceLog pInstance : processesList) {

				Boolean treatable = Boolean.FALSE;

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

				sCircuitIns = new CircuitInstanceDTO(pInstance, starter, documentRef, null);

				// get user's tasks
				// for (TaskSummary userTask :
				// getTaskService().getTasksAssignedAsPotentialOwner(potentialOwner,
				// null,
				// null, null))
				// userTasks.add(getTaskService().getTaskById(userTask.getId()));

				// if the duration is null, the process instance is still
				// active, so we can retrieve its current awaiting task
				// if (pInstance.getDuration() == null) {
				// among awaiting task in the base...
				for (Long awaitingTaskId : taskService.getTasksByProcessInstanceId(pInstance.getProcessInstanceId())) {
					// logger.debug(awaitingTask);

					Task awaitingTask = taskService.getTaskById(awaitingTaskId);

					// tasks.add(awaitingTask);

					// keep the one belonging to this process instance
					// if
					// (((Long)(awaitingTask.getTaskData().getProcessInstanceId()).equals(pInstance.getProcessInstanceId())))
					// {
					// List<String> tasksPotOwners = new
					// ArrayList<String>();
					for (OrganizationalEntity ownerGroup : awaitingTask.getPeopleAssignments().getPotentialOwners())
						if (isUserInGroup(potentialOwner, ownerGroup.getId())) {
							// tasksPotOwners.add(owner.getId());

							// if (userTasks.contains(awaitingTask))
							treatable = Boolean.TRUE;
						} else {
							logger.info("unfortunately user is not " + ownerGroup);
						}
					sCircuitIns.setCurrentStep(new CircuitStepDTO(awaitingTask, treatable));

					// }

				}
				// }

				// if there is no current step for <user>, he is not
				// involved on this instance right now
				// if (sCircuitIns.getCurrentStep() != null)
				results.add(sCircuitIns);

			}
		}

		// getRuntimeManager().close();

		// returning the list of <user>'s circuit instances
		return results;

	}

	public void delegateTask(Long instanceId, String userId, String targetUserId) {
		Boolean delegatable = Boolean.FALSE;

		Person user = personRepository.findOneByUserLogin(userId);
		Set<Office> userIdGroups = user.getOffices();

		// check if targetUserId is within one of userId groups
		for (Office group : userIdGroups)
			if (isUserInGroup(targetUserId, group.getName())) {
				delegatable = Boolean.TRUE;
				break;
			}

		if (delegatable)
			getTaskService().delegate(getCircuitInstanceById(instanceId, userId).getCurrentStep().getStepTask().getId(),
					userId, targetUserId);
		else {
			logger.info("=======can not delegate task to the user=========");
			throw new RuntimeException();
		}

	}

	private boolean isUserInGroup(String potentialOwner, String ownerGroupIdAsString) {

		Person person = personRepository.findOneByUserLogin(potentialOwner);
		for (Office office : person.getOffices()) {
			if (office.getName().equals(ownerGroupIdAsString)) {
				logger.info("effectively user is in " + ownerGroupIdAsString);
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;

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

		// claiming the task first if it is not already reserved
		if (!currentStep.getStepTask().getTaskData().getStatus().equals(Status.Reserved))
			taskService.claim(currentStep.getStepTask().getId(), starter);

		// starting the task
		taskService.start(currentStep.getStepTask().getId(), starter);

		// Now the task is in 'InProgress' state ...

	}

	private InternalTaskService getTaskService() {

		if (taskService != null)
			return taskService;

		return taskService = (InternalTaskService) getRuntimeEngine().getTaskService();
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

		List<Task> tasks = new ArrayList<Task>();

		List<TaskSummary> taskSummaries = getTaskService()
				.getCompletedTasksByProcessId(instance.getProcessInstanceInfo().getProcessInstanceId());

		for (TaskSummary task : taskSummaries) {
			tasks.add(0, getTaskService().getTaskById(task.getId()));
		}

		List<CircuitStepDTO> result = new ArrayList<CircuitStepDTO>();

		// we store task creation dates to set them as previous tasks end dates
		List<Date> taskEndDates = new ArrayList<Date>();

		for (Task _task : tasks) {
			logger.info("Task completed : " + _task.getName());
			// storing the creation date (it will be the previous task end date)
			taskEndDates.add(_task.getTaskData().getCreatedOn());
			result.add(new CircuitStepDTO(_task));
		}

		Date nextEndDate = instance.getProcessInstanceInfo().getEnd();

		if (nextEndDate == null) {

			// add the creation date of the next non completed task, to be set
			// as the end date of the last completed task
			taskEndDates.add(instance.getCurrentStep().getStepTask().getTaskData().getCreatedOn());
		} else {
			taskEndDates.add(nextEndDate);
		}

		// update the completed tasks while setting their end dates
		for (int i = 0; i < result.size(); i++) {
			CircuitStepDTO step = result.get(i);
			logger.info("Setting end date to " + taskEndDates.get(i + 1));
			step.setEndDate(taskEndDates.get(i + 1));
			result.set(i, step);
		}

		logger.info("results======== " + result.toString());

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
