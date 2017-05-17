package com.aft.jbpmcuy.web.rest.errors;

public final class ErrorConstants {

	public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
	public static final String ERR_ACCESS_DENIED = "error.accessDenied";
	public static final String ERR_VALIDATION = "error.validation";
	public static final String ERR_METHOD_NOT_SUPPORTED = "error.methodNotSupported";
	public static final String ERR_INTERNAL_SERVER_ERROR = "error.internalServerError";

	// TODO: Configure internationalization for these messages
	public static final String ERR_COMPLETE_TASK_SERVER_ERROR = "An error occured when trying to achieve a folder treatment";
	public static final String ERR_COMPLETE_TASK_SERVER_ERROR_DESC = "You may not be allowed to treat this folder";
	public static final String ERR_START_TASK_SERVER_ERROR = "An error occured when trying to claim a folder treatment";
	public static final String ERR_START_TASK_SERVER_ERROR_DESCR = "You may not be allowed to start this task";
	public static final String ERR_DELEGATE_TASK_SERVER_ERROR = "An error occured when trying to claim a folder treatment";
	public static final String ERR_DELEGATE_TASK_SERVER_ERROR_DESCR = "You may not be allowed to start this task";
	public static final String ERR_DELEGATE_TASK_SERVER_PERMISSION_ERROR_DESCR = "You can not delegate this task to that user cause he is not one of your office mates";

	private ErrorConstants() {
	}

}
