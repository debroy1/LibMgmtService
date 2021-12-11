package com.roy.lms.reservation.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:custom.properties")
public class AppProperties {

	@Value("${url.student-service}")
	private String STUDENT_SERVICE_URL;

	@Value("${url.book-service}")
	private String BOOK_SERVICE_URL;

	public String getStudentServiceUrl() {
		return STUDENT_SERVICE_URL;
	}
	
	public String getBookServiceUrl() {
		return BOOK_SERVICE_URL;
	}

}
