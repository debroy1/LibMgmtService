package com.roy.user.exceptions;

public enum Exceptions {
	MISSING_REQUIRED_FIELD("Missing some or all required fields. Please try again."),
	AUTHENTICATION_FAILED("Authentication failed."),
	AUTHORIZATION_VIOLATION("Authorization policy violated - operation not allowed."),
	USER_NOT_FOUND("Invalid User. User not found for supplied parameter(s)."),
	UNKNOWN_CLIENT("Authentication failed. Unknown client."),
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
