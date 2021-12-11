package com.roy.lms.reservation.model;

import java.util.Date;

import lombok.Data;

@Data
public class Reservation {
	private long reservationId;
	private long studentId;
	private long bookId;
	private Date reservedOn;
	private Date returnedOn;
}
