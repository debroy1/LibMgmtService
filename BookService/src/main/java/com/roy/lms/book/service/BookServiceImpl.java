package com.roy.lms.book.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.roy.lms.book.entity.BookEntity;
import com.roy.lms.book.model.Book;
import com.roy.lms.book.repository.BookRepository;

@Service
public class BookServiceImpl implements BookService {

	@Autowired
	BookRepository bookRepository;
	
	@Override
	public List<Book> getAllBooks() {
		List<Book> books = new ArrayList<>();
		List<BookEntity> bookEntities = bookRepository.findAll();
		if(bookEntities != null && bookEntities.size() > 0) {
			for (BookEntity bookEntity : bookEntities) {
				Book book = new Book();
				BeanUtils.copyProperties(bookEntity, book);
				books.add(book);
			}
		}
		return books;
	}

	@Override
	public Book getBookById(Long bookId) {
		BookEntity bookEntity = bookRepository.findByBookId(bookId);
		Book book = new Book();
		if(bookEntity != null && bookEntity.getBookId() != 0) {
			BeanUtils.copyProperties(bookEntity, book);
		} else {
			throw new RuntimeException("Unable to complete requested operation as record not found!");
		}
		return book;
	}

	@Override
	public Book saveBook(Book book) {
		BookEntity entity = new BookEntity();
		BeanUtils.copyProperties(book, entity);
		BookEntity bookEntity = bookRepository.save(entity);
		if(bookEntity != null && bookEntity.getBookId() != 0) {
			BeanUtils.copyProperties(bookEntity, book);
		} else {
			throw new RuntimeException("Unable to complete requested operation due to service error!");
		}
		return book;
	}

	@Override
	public void deleteBookById(Long bookId) {
		BookEntity bookEntity = bookRepository.findByBookId(bookId);
		if(bookEntity != null && bookEntity.getBookId() != 0) {
			bookRepository.delete(bookEntity);
		} else {
			throw new RuntimeException("Unable to complete requested operation as record not found!");
		}
	}

}
