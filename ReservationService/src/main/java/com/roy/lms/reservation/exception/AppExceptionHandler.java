package com.roy.lms.reservation.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.roy.lms.reservation.rest.ReservationResponse;

@ControllerAdvice
public class AppExceptionHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppExceptionHandler.class);

	public AppExceptionHandler() {
		System.out.println("Initializing AppExceptionHandler");
	}

	@ExceptionHandler
	public ResponseEntity<Object> handleUserServiceException(RuntimeException exception, WebRequest request) {
		ReservationResponse response = new ReservationResponse();
		response.setError(exception.getMessage());
		LOGGER.error("*** EXCEPTION *** " + exception.getMessage());
		return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
