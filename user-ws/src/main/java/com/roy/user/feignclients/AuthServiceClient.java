package com.roy.user.feignclients;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.roy.user.rest.UserCredential;
import com.roy.user.rest.UserInfo;

@FeignClient(name="auth-service")
public interface AuthServiceClient {
	
	@PostMapping("/api/authenticateJwt")
	public UserInfo authenticateJwtCred(@RequestBody UserCredential credential, @RequestHeader Map<String, String> headers);
}
