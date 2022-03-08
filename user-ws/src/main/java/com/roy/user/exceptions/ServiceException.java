package com.roy.user.exceptions;

public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 8519978079638046921L;

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(Exceptions exception) {
		super(exception.getMessage());
	}
}
