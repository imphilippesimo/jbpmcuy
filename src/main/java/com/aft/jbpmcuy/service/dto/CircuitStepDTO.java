/**
 * 
 */
package com.aft.jbpmcuy.service.dto;

import org.kie.api.task.model.TaskSummary;

/**
 * @author Philippe Simo
 *
 */
public class CircuitStepDTO {
	private CircuitDTO circuit;

	private TaskSummary stepTask;

	/**
	 * @param circuit
	 * @param stepFolder
	 * @param stepTask
	 * @param banettes
	 */
	public CircuitStepDTO(CircuitDTO circuit,
			TaskSummary stepTask) {
		super();
		this.circuit = circuit;

		this.stepTask = stepTask;

	}

	/**
	 * @return the circuit
	 */
	public CircuitDTO getCircuit() {
		return circuit;
	}

	/**
	 * @param circuit
	 *            the circuit to set
	 */
	public void setCircuit(CircuitDTO circuit) {
		this.circuit = circuit;
	}

	/**
	 * @return the stepTask
	 */
	public TaskSummary getStepTask() {
		return stepTask;
	}

	/**
	 * @param stepTask
	 *            the stepTask to set
	 */
	public void setStepTask(TaskSummary stepTask) {
		this.stepTask = stepTask;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SignatureCircuitStepDTO [circuit=" + circuit + ",  stepTask="
				+ stepTask + "]";
	}

	/**
	 * @return
	 */
	public String getName() {

		return this.stepTask.getName();
	}

}
