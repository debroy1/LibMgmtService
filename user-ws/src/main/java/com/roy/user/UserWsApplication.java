package com.roy.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class UserWsApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserWsApplication.class, args);
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
