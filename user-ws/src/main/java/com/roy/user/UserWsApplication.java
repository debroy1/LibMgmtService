package com.roy.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.roy.user.security.HttpRequestInterceptor;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class UserWsApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserWsApplication.class, args);
	}
	
	@Autowired
	HttpRequestInterceptor httpRequestInterceptor;

	@Bean
	@LoadBalanced
	public RestTemplate getRestTemplate() {
		// set a timeout of 3 seconds for any rest call to avoid blocked thread
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		// this timeout setting is not required here and it can be handled by circuit breaker if there is any
		clientHttpRequestFactory.setConnectionRequestTimeout(3000);
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(httpRequestInterceptor);
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
	}

}
