package com.roy.lms.reservation.model;

import lombok.Data;

@Data
public class Book {
	private long bookId;
	private String title;
	private String author;
	private String category;
	private String description;
	private String publisher;
	private float price;
	private int copies;
}
