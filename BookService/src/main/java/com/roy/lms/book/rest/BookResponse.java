package com.roy.lms.book.rest;

import java.util.List;

import com.roy.lms.book.model.Book;

import lombok.Data;

@Data
public class BookResponse {
	private String status;
	private String error;
	private List<Book> books;
	private Book book;
}
