package com.roy.lms.student.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roy.lms.student.model.Student;
import com.roy.lms.student.service.StudentService;

@RestController
@RequestMapping("/api/students")
public class StudentController {

	@Autowired
	private StudentService studentService;
	
	@Autowired
	private final Environment env;
	
	// constructor to inject environment variable
	public StudentController(Environment env) {
		this.env = env;
	}
	
	@GetMapping
	// get all students
	public StudentResponse getAllStudents() {
		StudentResponse studentResponse = getRestResponse();
		studentResponse.setStudents(studentService.getAllStudents());
		return studentResponse;
	}

	@PostMapping
	// add or update student
	public StudentResponse addStudent(@RequestBody Student student) {
		StudentResponse studentResponse = getRestResponse();
		Student updatedStudent = studentService.saveStudent(student);
		studentResponse.setStudent(updatedStudent);
		return studentResponse;
	}
	
	@GetMapping("/{studentId}")
	// get student by id
	public StudentResponse getStudent(@PathVariable Long studentId) {
		StudentResponse studentResponse = getRestResponse();
		studentResponse.setStudent(studentService.getStudentById(studentId));
		return studentResponse;
	}
	
	@DeleteMapping("/{studentId}")
	// delete student by id
	public StudentResponse deleteStudent(@PathVariable Long studentId) {
		StudentResponse studentResponse = getRestResponse();
		studentService.deleteStudentById(studentId);
		return studentResponse;
	}
	
	// build response with default values
	private StudentResponse getRestResponse() {
		System.out.println("Serving from Port: " + env.getProperty("local.server.port"));
		StudentResponse studentResponse = new StudentResponse();
		studentResponse.setStatus("Success : Port - " + env.getProperty("local.server.port"));
		return studentResponse;
	}
		
}
