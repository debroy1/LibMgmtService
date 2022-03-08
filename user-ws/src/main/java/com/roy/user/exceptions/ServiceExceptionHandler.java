package com.roy.user.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ServiceExceptionHandler {
	private final Logger log = LoggerFactory.getLogger(getClass());

	public ServiceExceptionHandler() {
		log.info("Initializing AppExceptionHandler");
	}

	@ExceptionHandler(value = {ServiceException.class})
	public ResponseEntity<Object> handleUserServiceExceptions(RuntimeException exception, WebRequest request) {
		String errorMessage = exception.getMessage();
		log.error("*** EXCEPTION *** " + errorMessage);
		if(errorMessage.equals(Exceptions.USER_NOT_FOUND.getMessage())) {
			return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = {Exception.class})
	public ResponseEntity<Object> handleOtherExceptions(Exception exception, WebRequest request) {
		log.error("*** EXCEPTION *** " + exception.getMessage());
		return new ResponseEntity<>(exception.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
