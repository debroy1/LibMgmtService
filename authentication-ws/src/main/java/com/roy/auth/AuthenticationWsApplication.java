package com.roy.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
public class AuthenticationWsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationWsApplication.class, args);
	}
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@LoadBalanced
	public RestTemplate getRestTemplate() {
		/*
		// set a timeout of 3 seconds for any rest call to avoid blocked thread
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		// this timeout setting is not required here and it can be handled by circuit breaker
		clientHttpRequestFactory.setConnectionRequestTimeout(3000);
		return new RestTemplate(clientHttpRequestFactory);
		*/
		return new RestTemplate();
	}

}
