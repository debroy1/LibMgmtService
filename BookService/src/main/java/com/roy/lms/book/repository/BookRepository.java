package com.roy.lms.book.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.roy.lms.book.entity.BookEntity;

@Repository
public interface BookRepository extends CrudRepository<BookEntity, Long> {
	List<BookEntity> findAll();
	BookEntity findByBookId(long bookId);
}
