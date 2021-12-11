package com.roy.lms.student.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.roy.lms.student.entity.StudentEntity;

@Repository
public interface StudentRepository extends CrudRepository<StudentEntity, Long> {
	List<StudentEntity> findAll();
	StudentEntity findByStudentId(long studentId);
}
