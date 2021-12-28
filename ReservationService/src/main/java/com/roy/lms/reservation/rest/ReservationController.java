package com.roy.lms.reservation.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.roy.lms.reservation.model.Book;
import com.roy.lms.reservation.model.Reservation;
import com.roy.lms.reservation.model.Student;
import com.roy.lms.reservation.service.ReservationService;
import com.roy.lms.reservation.util.AppProperties;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
	private final Logger LOGGER = LoggerFactory.getLogger(ReservationController.class);

	@Autowired
	private ReservationService reservationService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private AppProperties properties;
	
	@Autowired
	private final Environment env;
	
	// constructor to inject environment variable
	public ReservationController(Environment env) {
		this.env = env;
	}
	
	@GetMapping
	// get all reservations - implementing bulkhead pattern using thread pool concept per method like bulkhead compartments in a ship
	@HystrixCommand(defaultFallback = "serviceFallback",
		threadPoolKey = "allReservationsThreadPool",
		threadPoolProperties = {
				@HystrixProperty(name = "coreSize", value = "20"),
				@HystrixProperty(name = "maxQueueSize", value = "10")
		})
	public ReservationResponse getAllReservations() {
		// TODO - add logic to get user details and book details along with reservation details
		LOGGER.info("Entering getAllReservations()");
		ReservationResponse reservationResponse = getRestResponse();
		reservationResponse.setReservations(reservationService.getAllReservations());
		LOGGER.trace("Reservation count: " + reservationResponse.getReservations().size());
		LOGGER.info("Returning from getAllReservations()");
		return reservationResponse;
	}

	@PostMapping
	// add or update reservation
	public ReservationResponse addReservation(@RequestBody Reservation reservation) {
		// TODO - add logic to allow reservation only if book copies available is > 0 and user reservation is < 2.
		ReservationResponse reservationResponse = getRestResponse();
		Reservation updatedReservation = reservationService.saveReservation(reservation);
		reservationResponse.setReservation(updatedReservation);
		return reservationResponse;
	}
	
	@GetMapping("/{reservationId}")
	// get reservation by id
	public ReservationResponse getReservation(@PathVariable Long reservationId) {
		ReservationResponse reservationResponse = getRestResponse();
		reservationResponse.setReservation(reservationService.getReservationById(reservationId));
		return reservationResponse;
	}
	
	@DeleteMapping("/{reservationId}")
	// delete reservation by id
	public ReservationResponse deleteReservation(@PathVariable Long reservationId) {
		ReservationResponse reservationResponse = getRestResponse();
		reservationService.deleteReservationById(reservationId);
		return reservationResponse;
	}
	
	@GetMapping("/students/{studentId}")
	// get all reservations by student id - implementing circuit-breaker patterns
	@HystrixCommand(defaultFallback = "serviceFallback",
			commandProperties = {
					@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
					@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
					@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
					@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000") })
	public ReservationResponse getAllReservationsByStudentId(@PathVariable Long studentId) {
		System.out.println("Fetching student details for Id: " + studentId);
		StudentResponse response = restTemplate.getForObject(properties.getStudentServiceUrl() + studentId, StudentResponse.class);
		if(response != null && response.getStudent() != null) {
			Student student = response.getStudent();
			System.out.println("Received student details: " + student.getFirstName() + " " + student.getLastName());
		}
		ReservationResponse reservationResponse = getRestResponse();
		reservationResponse.setReservations(reservationService.getReservationsByStudentId(studentId));
		return reservationResponse;
	}
	
	@GetMapping("/books/{bookId}")
	// get all reservations by student id - implementing circuit-breaker patterns
	@HystrixCommand(defaultFallback = "serviceFallback",
			commandProperties = {
					@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
					@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
					@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
					@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000") })
	public ReservationResponse getAllReservationsByBookId(@PathVariable Long bookId) {
		System.out.println("Fetching book details for Id: " + bookId);
		BookResponse response = restTemplate.getForObject(properties.getBookServiceUrl() + bookId, BookResponse.class);
		if(response != null && response.getBook() != null) {
			Book book = response.getBook();
			System.out.println("Received book details: " + book.getTitle() + " by " + book.getAuthor());
		}
		ReservationResponse reservationResponse = getRestResponse();
		reservationResponse.setReservations(reservationService.getReservationsByBookId(bookId));
		return reservationResponse;
	}
	
	// default fallback method by Hystrix
	public ReservationResponse serviceFallback() {
		System.err.println("Inside Fallback");
		ReservationResponse reservationResponse = getRestResponse();
		reservationResponse.setStatus("Failure");
		reservationResponse.setError("Unable to process request due to service error! Please try after sometime.");
		return reservationResponse; 
	}
	
	// build response with default values
	private ReservationResponse getRestResponse() {
		System.out.println("Serving from Port: " + env.getProperty("local.server.port"));
		ReservationResponse reservationResponse = new ReservationResponse();
		reservationResponse.setStatus("Success : Port - " + env.getProperty("local.server.port"));
		return reservationResponse;
	}
	
}
