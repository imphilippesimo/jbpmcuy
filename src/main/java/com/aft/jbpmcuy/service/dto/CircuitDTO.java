/**
 * 
 */
package com.aft.jbpmcuy.service.dto;

/**
 * @author Philippe Simo
 *
 */
public class CircuitDTO {

	private String circuitName;
	private String circuitId;

	/**
	 * @param circuitName
	 * @param circuitId
	 */
	public CircuitDTO(String circuitName, String circuitId) {
		super();
		this.circuitName = circuitName;
		this.circuitId = circuitId;
	}

	/**
	 * @return the circuitName
	 */
	public String getCircuitName() {
		return circuitName;
	}

	/**
	 * @return the circuitId
	 */
	public String getCircuitId() {
		return circuitId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SignatureCircuitDTO [circuitName=" + circuitName
				+ ", circuitId=" + circuitId + "]";
	}

}
