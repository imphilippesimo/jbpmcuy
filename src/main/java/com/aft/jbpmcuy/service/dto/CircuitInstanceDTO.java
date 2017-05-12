/**
 * 
 */
package com.aft.jbpmcuy.service.dto;

import org.kie.api.runtime.manager.audit.ProcessInstanceLog;

/**
 * @author Philippe Simo
 *
 */
public class CircuitInstanceDTO {
	private ProcessInstanceLog processInstanceInfo;
	private CircuitDTO circuit;
	private String starter;
	private String documentRef;
	private CircuitStepDTO currentStep;
	// private List<CircuitStepDTO> previousSteps;

	/**
	 * @param processInstanceInfo
	 * @param starter
	 * @param documentRef
	 * @param circuit
	 * @param currentStep
	 * @param previousSteps
	 */
	public CircuitInstanceDTO(ProcessInstanceLog processInstanceInfo, String starter, String documentRef,
			CircuitDTO circuit, CircuitStepDTO currentStep) {
		super();
		this.processInstanceInfo = processInstanceInfo;
		this.starter = starter;
		this.documentRef = documentRef;
		this.currentStep = currentStep;
		this.circuit = circuit;
		// this.previousSteps = previousSteps;
	}

	public CircuitInstanceDTO() {
		// TODO Auto-generated constructor stub
	}

	public String getStarter() {
		return starter;
	}

	/**
	 * @return the processInstanceInfo
	 */
	public ProcessInstanceLog getProcessInstanceInfo() {
		return processInstanceInfo;
	}

	/**
	 * @return the circuit referenced by this instance
	 */
	public CircuitDTO getCircuit() {
		return this.circuit;
	}

	/**
	 * @return the currentStep
	 */
	public CircuitStepDTO getCurrentStep() {
		return currentStep;
	}

	/**
	 * @param currentStep
	 *            the currentStep to set
	 */
	public void setCurrentStep(CircuitStepDTO currentStep) {
		this.currentStep = currentStep;
	}

	// public List<CircuitStepDTO> getPreviousSteps() {
	// return previousSteps;
	// }

	public String getDocumentRef() {
		return documentRef;
	}

	@Override
	public String toString() {
		return "CircuitInstanceDTO [processInstanceInfo=" + processInstanceInfo + ", circuit=" + circuit + ", starter="
				+ starter + ", documentRef=" + documentRef + ", currentStep=" + currentStep + "]";
	}

}
