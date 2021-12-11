package com.roy.lms.reservation.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.roy.lms.reservation.entity.ReservationEntity;

@Repository
public interface ReservationRepository extends CrudRepository<ReservationEntity, Long> {
	List<ReservationEntity> findAll();
	ReservationEntity findByReservationId(long reservationId);
	List<ReservationEntity> findByStudentId(long studentId);
	List<ReservationEntity> findByBookId(long bookId);
}
