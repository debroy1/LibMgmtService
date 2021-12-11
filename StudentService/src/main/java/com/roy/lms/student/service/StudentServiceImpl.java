package com.roy.lms.student.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.roy.lms.student.entity.StudentEntity;
import com.roy.lms.student.model.Student;
import com.roy.lms.student.repository.StudentRepository;

@Service
public class StudentServiceImpl implements StudentService {
	@Autowired
	StudentRepository studentRepository;

	@Override
	public List<Student> getAllStudents() {
		List<Student> students = new ArrayList<>();
		List<StudentEntity> studentEntities = studentRepository.findAll();
		if(studentEntities != null && studentEntities.size() > 0) {
			for (StudentEntity studentEntity : studentEntities) {
				Student student = new Student();
				BeanUtils.copyProperties(studentEntity, student);
				students.add(student);
			}
		}
		return students;
	}

	@Override
	public Student getStudentById(Long studentId) {
		StudentEntity studentEntity = studentRepository.findByStudentId(studentId);
		Student student = new Student();
		if(studentEntity != null && studentEntity.getStudentId() != 0) {
			BeanUtils.copyProperties(studentEntity, student);
		} else {
			throw new RuntimeException("Unable to complete requested operation as record not found!");
		}
		return student;
	}

	@Override
	public Student saveStudent(Student student) {
		StudentEntity entity = new StudentEntity();
		BeanUtils.copyProperties(student, entity);
		StudentEntity studentEntity = studentRepository.save(entity);
		if(studentEntity != null && studentEntity.getStudentId() != 0) {
			BeanUtils.copyProperties(studentEntity, student);
		} else {
			throw new RuntimeException("Unable to complete requested operation due to service error!");
		}
		return student;
	}

	@Override
	public void deleteStudentById(Long studentId) {
		StudentEntity studentEntity = studentRepository.findByStudentId(studentId);
		if(studentEntity != null && studentEntity.getStudentId() != 0) {
			studentRepository.delete(studentEntity);
		} else {
			throw new RuntimeException("Unable to complete requested operation as record not found!");
		}
	}
	
}
