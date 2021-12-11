package com.roy.lms.student.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity(name="Students")
public class StudentEntity implements Serializable {

	private static final long serialVersionUID = 3761038290247062440L;

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private long studentId;
	@Column(nullable = false, length = 50)
	private String firstName;
	@Column(nullable = false, length = 50)
	private String lastName;

	@Column(nullable = false, length = 150)
	private String address;
	@Column(nullable = false, length = 10)
	private String zip;
	@Column(nullable = false, length = 50)
	private String city;
	@Column(nullable = false, length = 50)
	private String country;

	@Column(nullable = false, length = 25, unique = true)
	private String mobile;
	@Column(nullable = false, length = 100, unique = true)
	private String email;
}
