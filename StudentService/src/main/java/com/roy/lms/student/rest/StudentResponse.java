package com.roy.lms.student.rest;

import java.util.List;

import com.roy.lms.student.model.Student;

import lombok.Data;

@Data
public class StudentResponse {
	private String status;
	private String error;
	private List<Student> students;
	private Student student;
}
