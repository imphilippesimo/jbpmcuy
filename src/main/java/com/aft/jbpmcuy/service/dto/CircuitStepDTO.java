/**
 * 
 */
package com.aft.jbpmcuy.service.dto;

import java.util.Date;

import org.kie.api.task.model.Task;

/**
 * @author Philippe Simo
 *
 */
public class CircuitStepDTO {

	private Task stepTask;
	// The start date is already in the TaskSummary
	private Date endDate;
	private Boolean treatable;

	/**
	 * @param awaitingTask
	 * @param treatable
	 */
	public CircuitStepDTO(Task awaitingTask, Boolean treatable) {
		this(awaitingTask);
		this.treatable = treatable;

	}

	/**
	 * @param awaitingTask
	 */
	public CircuitStepDTO(Task awaitingTask) {
		super();
		this.stepTask = awaitingTask;
	}

	/**
	 * @return the stepTask
	 */
	public Task getStepTask() {
		return stepTask;
	}

	/**
	 * @param stepTask
	 *            the stepTask to set
	 */
	public void setStepTask(Task stepTask) {
		this.stepTask = stepTask;
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

	public Boolean getTreatable() {
		return treatable;
	}

	public void setTreatable(Boolean treatable) {
		this.treatable = treatable;
	}

}
