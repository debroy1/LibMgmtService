package com.roy.lms.reservation.model;

import lombok.Data;

@Data
public class Student {
	private long studentId;
	private String firstName;
	private String lastName;
	private String address;
	private String zip;
	private String city;
	private String country;
	private String mobile;
	private String email;
}
