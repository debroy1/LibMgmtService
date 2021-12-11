package com.roy.lms.reservation.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.roy.lms.reservation.entity.ReservationEntity;
import com.roy.lms.reservation.model.Reservation;
import com.roy.lms.reservation.repository.ReservationRepository;

@Service
public class ReservationServiceImpl implements ReservationService {
	
	@Autowired
	ReservationRepository reservationRepository;

	@Override
	public List<Reservation> getAllReservations() {
		List<Reservation> reservations = new ArrayList<>();
		List<ReservationEntity> reservationEntites = reservationRepository.findAll();
		if(reservationEntites != null && reservationEntites.size() > 0) {
			for (ReservationEntity reservationEntity : reservationEntites) {
				Reservation reservation = new Reservation();
				BeanUtils.copyProperties(reservationEntity, reservation);
				reservations.add(reservation);
			}
		}
		return reservations;
	}

	@Override
	public Reservation getReservationById(Long reservationId) {
		ReservationEntity reservationEntity = reservationRepository.findByReservationId(reservationId);
		Reservation reservation = new Reservation();
		if(reservationEntity != null && reservationEntity.getStudentId() != 0) {
			BeanUtils.copyProperties(reservationEntity, reservation);
		} else {
			throw new RuntimeException("Unable to complete requested operation as record not found!");
		}
		return reservation;
	}

	@Override
	public Reservation saveReservation(Reservation reservation) {
		ReservationEntity entity = new ReservationEntity();
		BeanUtils.copyProperties(reservation, entity);
		// update Reserved On date as present date and Returned On date as null for new reservation
		if(reservation != null && reservation.getReservationId() == 0) {
			entity.setReservedOn(new Date());
			entity.setReturnedOn(null);
		}
		ReservationEntity studentEntity = reservationRepository.save(entity);
		if(studentEntity != null && studentEntity.getStudentId() != 0) {
			BeanUtils.copyProperties(studentEntity, reservation);
		} else {
			throw new RuntimeException("Unable to complete requested operation due to service error!");
		}
		return reservation;
	}

	@Override
	public void deleteReservationById(Long reservationId) {
		ReservationEntity studentEntity = reservationRepository.findByReservationId(reservationId);
		if(studentEntity != null && studentEntity.getStudentId() != 0 && studentEntity.getReturnedOn() == null) {
			// do not delete but just update Returned On date as present date
			studentEntity.setReturnedOn(new Date());
			ReservationEntity entity = reservationRepository.save(studentEntity);
			if(entity == null || entity.getStudentId() == 0 || studentEntity.getReturnedOn() == null) {
				throw new RuntimeException("Unable to complete requested operation die to service error!");
			}
		} else {
			throw new RuntimeException("Unable to complete requested operation as record not found!");
		}
	}

	@Override
	public List<Reservation> getReservationsByStudentId(Long studentId) {
		List<Reservation> reservations = new ArrayList<>();
		List<ReservationEntity> reservationEntites = reservationRepository.findByStudentId(studentId);
		if(reservationEntites != null && reservationEntites.size() > 0) {
			for (ReservationEntity reservationEntity : reservationEntites) {
				Reservation reservation = new Reservation();
				BeanUtils.copyProperties(reservationEntity, reservation);
				reservations.add(reservation);
			}
		}
		return reservations;
	}

	@Override
	public List<Reservation> getReservationsByBookId(Long bookId) {
		List<Reservation> reservations = new ArrayList<>();
		List<ReservationEntity> reservationEntites = reservationRepository.findByBookId(bookId);
		if(reservationEntites != null && reservationEntites.size() > 0) {
			for (ReservationEntity reservationEntity : reservationEntites) {
				Reservation reservation = new Reservation();
				BeanUtils.copyProperties(reservationEntity, reservation);
				reservations.add(reservation);
			}
		}
		return reservations;
	}

}
