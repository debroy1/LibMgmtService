package com.roy.lms.book.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity(name="Books")
public class BookEntity implements Serializable {

	private static final long serialVersionUID = -4451499288872918278L;

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private long bookId;
	@Column(nullable = false, length = 250)
	private String title;
	@Column(nullable = false, length = 250)
	private String author;

	@Column(nullable = true, length = 100)
	private String category;
	@Column(nullable = true, length = 1000)
	private String description;
	@Column(nullable = true, length = 100)
	private String publisher;

	@Column(nullable = false, columnDefinition = "float default 99.99")
	private float price;
	@Column(nullable = false, columnDefinition = "integer default 1")
	private int copies;
}
