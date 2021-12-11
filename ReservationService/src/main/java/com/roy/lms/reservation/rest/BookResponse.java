package com.roy.lms.reservation.rest;

import java.util.List;

import com.roy.lms.reservation.model.Book;

import lombok.Data;

@Data
public class BookResponse {
	private String status;
	private String error;
	private List<Book> books;
	private Book book;
}
