package com.aft.jbpmcuy.web.rest.errors;

import java.io.Serializable;
import java.util.Map;

/**
 * View Model for sending a parameterized error message.
 */
public class ParameterizedErrorVM implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String message;
	private final String description;
	private final Map<String, String> paramMap;

	public ParameterizedErrorVM(String message, String description, Map<String, String> paramMap) {
		this.message = message;
		this.description = description;
		this.paramMap = paramMap;
	}

	public String getMessage() {
		return message;
	}

	public Map<String, String> getParams() {
		return paramMap;
	}

	public String getDescription() {
		return description;
	}

}
