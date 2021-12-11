package com.roy.lms.reservation.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity(name="Reservations")
public class ReservationEntity implements Serializable {

	private static final long serialVersionUID = 3188092095882747908L;

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private long reservationId;

	@Column(nullable = false)
	private long studentId;
	@Column(nullable = false)
	private long bookId;

	@Column(nullable = false)
	private Date reservedOn;
	@Column(nullable = true)
	private Date returnedOn;

}
