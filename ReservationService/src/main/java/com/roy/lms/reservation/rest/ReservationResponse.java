package com.roy.lms.reservation.rest;

import java.util.List;

import com.roy.lms.reservation.model.Reservation;

import lombok.Data;

@Data
public class ReservationResponse {
	private String status;
	private String error;
	private List<Reservation> reservations;
	private Reservation reservation;
}
