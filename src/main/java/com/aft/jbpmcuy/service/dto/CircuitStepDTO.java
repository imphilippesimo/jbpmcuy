/**
 * 
 */
package com.aft.jbpmcuy.service.dto;

import java.util.Date;
import java.util.Map;

import org.kie.api.task.model.TaskSummary;

/**
 * @author Philippe Simo
 *
 */
public class CircuitStepDTO {
	private CircuitDTO circuit;
	private TaskSummary stepTask;
	private Map<String, Object> stepContent;
	// The start date is already in the TaskSummary
	private Date endDate;

	/**
	 * @param circuit
	 * @param stepFolder
	 * @param stepTask
	 * @param map
	 * @param banettes
	 */
	public CircuitStepDTO(CircuitDTO circuit, TaskSummary stepTask, Map<String, Object> stepContent) {
		super();
		this.circuit = circuit;
		this.stepTask = stepTask;
		this.stepContent = stepContent;

	}

	public Map<String, Object> getStepContent() {
		return stepContent;
	}

	public void setStepContent(Map<String, Object> stepContent) {
		this.stepContent = stepContent;
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
		return "SignatureCircuitStepDTO [circuit=" + circuit + ",  stepTask=" + stepTask + "]";
	}

	/**
	 * @return
	 */
	public String getName() {

		return this.stepTask.getName();
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
