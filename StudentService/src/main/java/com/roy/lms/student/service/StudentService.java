package com.roy.lms.student.service;

import java.util.List;

import com.roy.lms.student.model.Student;

public interface StudentService {
	public List<Student> getAllStudents();
	public Student getStudentById(Long studentId);
	public Student saveStudent(Student student);
	public void deleteStudentById(Long studentId);
}
