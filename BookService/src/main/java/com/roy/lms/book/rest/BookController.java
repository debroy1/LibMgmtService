package com.roy.lms.book.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roy.lms.book.model.Book;
import com.roy.lms.book.service.BookService;

@RestController
@RequestMapping("/api/books")
public class BookController {
	@Autowired
	private BookService bookService;
	
	@Autowired
	private final Environment env;
	
	// constructor to inject environment variable
	public BookController(Environment env) {
		this.env = env;
	}
	
	@GetMapping
	// get all book
	public BookResponse getAllBooks() {
		BookResponse bookResponse = getRestResponse();
		bookResponse.setBooks(bookService.getAllBooks());
		return bookResponse;
	}

	@PostMapping
	// add or update book
	public BookResponse addBook(@RequestBody Book book) {
		BookResponse bookResponse = getRestResponse();
		Book updatedBook = bookService.saveBook(book);
		bookResponse.setBook(updatedBook);
		return bookResponse;
	}
	
	@GetMapping("/{bookId}")
	// get book by id
	public BookResponse getBook(@PathVariable Long bookId) {
		BookResponse bookResponse = getRestResponse();
		bookResponse.setBook(bookService.getBookById(bookId));
		return bookResponse;
	}
	
	@DeleteMapping("/{bookId}")
	// delete book by id
	public BookResponse deleteBook(@PathVariable Long bookId) {
		BookResponse bookResponse = getRestResponse();
		bookService.deleteBookById(bookId);
		return bookResponse;
	}
	
	// build response with default values
	private BookResponse getRestResponse() {
		System.out.println("Serving from Port: " + env.getProperty("local.server.port"));
		BookResponse bookResponse = new BookResponse();
		bookResponse.setStatus("Success : Port - " + env.getProperty("local.server.port"));
		return bookResponse;
	}
}
