package com.roy.user.security;

import java.io.IOException;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

@Configuration
public class HttpRequestInterceptor implements ClientHttpRequestInterceptor {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Value("${gateway.header.token}")
	public String gatewayHeaderToken;
	@Value("${gateway.header.token.value}")
	public String gatewayHeaderTokenValue;

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		log.info("Intercepted: " + request.getURI() + " Method " + request.getMethod());
		// append MS controller headers in all outgoing requests
		request.getHeaders().add(gatewayHeaderToken, gatewayHeaderTokenValue);
		long start = Instant.now().toEpochMilli();
		ClientHttpResponse response = execution.execute(request, body);
		long end = Instant.now().toEpochMilli();
		long elapsed = end - start;
		log.info("Invoked: " + request.getURI() + " Method " + request.getMethod() + " Elapsed: " + elapsed + " millis");
		// TODO - check the response before return
		return response;
	}
	
}
