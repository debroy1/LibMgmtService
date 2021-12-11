package com.roy.lms.reservation.exception;

public enum Exceptions {
	AUTHENTICATION_FAILED("Authentication failed."),
	AUTHORIZATION_VIOLATION("Authorization policy violated - operation not allowed."),
	RECORD_NOT_FOUND("Invalid input. Record not found for supplied parameter(s)."),
	RECORD_ALREADY_EXISTS("Record already exists for given parameter(s). Try again."),
	MISSING_REQUIRED_FIELD("Missing some or all required fields. Please try again."),
	SERVICE_ERROR("Unable to process the request due to service error."),
	SERVICE_NOT_READY("Service is under construction."),
	SERVICE_NOT_AVAILABLE("Service is not available at this time.");
	
	private String message;
	
	Exceptions(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
