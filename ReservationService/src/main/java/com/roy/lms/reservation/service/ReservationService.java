package com.roy.lms.reservation.service;

import java.util.List;

import com.roy.lms.reservation.model.Reservation;

public interface ReservationService {
	public List<Reservation> getAllReservations();
	public Reservation getReservationById(Long reservationId);
	public Reservation saveReservation(Reservation reservation);
	public void deleteReservationById(Long reservationId);
	public List<Reservation> getReservationsByStudentId(Long studentId);
	public List<Reservation> getReservationsByBookId(Long bookId);
}
