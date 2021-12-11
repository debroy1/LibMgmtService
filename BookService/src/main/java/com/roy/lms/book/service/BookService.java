package com.roy.lms.book.service;

import java.util.List;

import com.roy.lms.book.model.Book;

public interface BookService {
	public List<Book> getAllBooks();
	public Book getBookById(Long bookId);
	public Book saveBook(Book book);
	public void deleteBookById(Long bookId);
}
