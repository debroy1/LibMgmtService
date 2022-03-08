package com.roy.user.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.roy.user.atlas.AtlasService;
import com.roy.user.atlas.UserItem;
import com.roy.user.feignclients.AuthServiceClient;

@RestController
@RequestMapping("/api/users")
public class UserController {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Value("${auth-svc.url.authenticate}")
	public String authSvcAuthenticateUrl;
	@Value("${gateway.header.token}")
	public String gatewayHeaderToken;
	@Value("${gateway.header.token.value}")
	public String gatewayHeaderTokenValue;

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private AuthServiceClient authServiceClient;

	@Autowired
	private AtlasService atlasService;
	
    @GetMapping("/health")
    public String healthCheck() {
    	return "Healthy!";
    }

	@GetMapping("/{email}")
	ResponseEntity<UserInfo> getUser(@PathVariable String email, @RequestHeader Map<String, String> headers) {
		log.info("received request to fetch user details for email: " + email);
		if(!hasValidRequestHeader(headers)) {
			log.warn("unauthorized request - unsigned by ApiGateway");
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		if(!hasAuthorizedJwt(headers)) {
			log.warn("unauthorized request - jwt token not found");
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		if(email == null || email.isBlank()) {
			log.warn("invalid request - check input");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		try {
			Optional<UserItem> user = atlasService.getUserByEmail(email);
			if(!user.isPresent()) {
				log.error("user not found");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			} else {
				log.info("user found");
				UserInfo _userInfo = new UserInfo();
				UserItem _userItem = new UserItem();
				_userItem = user.get();
				BeanUtils.copyProperties(_userItem, _userInfo);
				return new ResponseEntity<>(_userInfo, HttpStatus.OK);
			}
		} catch (Exception e) {
			log.warn("error encountered while fetching user details -> " + e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping
	ResponseEntity<UserInfo> updateUser(@RequestBody UserInfo userInfo, @RequestHeader Map<String, String> headers) {
		log.info("received request to update user details");
		if(!hasValidRequestHeader(headers)) {
			log.warn("unauthorized request - unsigned by ApiGateway");
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		if(!hasAuthorizedJwt(headers)) {
			log.warn("unauthorized request - jwt token not found");
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		if(!isValidUserInfo(userInfo)) {
			log.warn("invalid request - check input");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		try {
			UserInfo _userInfo = new UserInfo();
			UserItem _userItem = new UserItem();
			BeanUtils.copyProperties(userInfo, _userItem);
			_userItem = atlasService.updateUser(_userItem);
			if(_userItem != null && _userItem.getId() != null) {
				log.info("update successful");
				BeanUtils.copyProperties(_userItem, _userInfo);
				return new ResponseEntity<>(_userInfo, HttpStatus.OK);
			} else {
				log.info("update failed");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			log.warn("error encountered while fetching user details -> " + e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/{id}")
	ResponseEntity<String> deleteUser(@PathVariable String id, @RequestHeader Map<String, String> headers) {
		log.info("received request to delete user details for id: " + id);
		if(!hasValidRequestHeader(headers)) {
			log.warn("unauthorized request - unsigned by ApiGateway");
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		if(!hasAuthorizedJwt(headers)) {
			log.warn("unauthorized request - jwt token not found");
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		if(id == null || id.isBlank()) {
			log.warn("invalid request - check input");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		try {
			atlasService.deleteUser(id);
			log.info("delete successful");
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			log.warn("error encountered while fetching user details -> " + e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private boolean isValidUserInfo(UserInfo userInfo) {
		if(userInfo == null) {
			log.warn("invalid request - userInfo is empty");
			return false;
		} else if (userInfo.getId() == null || userInfo.getId().isBlank()
				|| userInfo.getFirstName() == null || userInfo.getFirstName().isBlank()
					|| userInfo.getLastName() == null || userInfo.getLastName().isBlank()
						|| userInfo.getEmail() == null || userInfo.getEmail().isBlank()) {
			log.warn("invalid request - one or more required fields is/are empty");
			return false;
		}
		return true;
	}

	private boolean hasValidRequestHeader(Map<String, String> headers) {
		log.info("Checking request headers for authenticity");
		// check for specific header fields
		for(Map.Entry<String, String> entry : headers.entrySet()) {
			String key = (String) entry.getKey();
			String value = null;
			if(entry.getValue() != null) {
				value = (String) entry.getValue();
			}
			log.info(key + " -> " + value);
			if(key.equals(gatewayHeaderToken) && value.equals(gatewayHeaderTokenValue)) {
				log.info("found to be signed by API Gateway");
				return true;
			}
		}
		log.error("API Gateway signature not found");
		return false;
	}
	
	private boolean hasAuthorizedJwt(Map<String, String> headers) {
		log.info("Checking request headers is authorized with valid Jwt token");
		// check for authorization token from header fields
		String authJwtToken = headers.get(HttpHeaders.AUTHORIZATION);
		if(authJwtToken == null) {
			authJwtToken = headers.get("authorization");
		}
		if(authJwtToken == null || authJwtToken.isBlank()) {
			log.error("unable to retrieve the jwt token from header!");
			return false;
		}
		UserCredential credential = new UserCredential();
		credential.setJwt(authJwtToken);
		// check authenticity of the authorization token from auth-service
		log.info("About to invoke auth-service api to validate the jwt access token");
		
		// using rest template
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(HttpHeaders.AUTHORIZATION, authJwtToken);
		httpHeaders.add(gatewayHeaderToken, gatewayHeaderTokenValue);
        HttpEntity<UserCredential> entity = new HttpEntity<UserCredential>(credential, httpHeaders);
        UserInfo user = restTemplate.postForObject(authSvcAuthenticateUrl, entity, UserInfo.class);
		
		// using feign client
		/*Map<String, String> httpHeaders = new HashMap<>();
		httpHeaders.put(HttpHeaders.AUTHORIZATION, authJwtToken);
		httpHeaders.put(gatewayHeaderToken, gatewayHeaderTokenValue);		
		UserInfo user = authServiceClient.authenticateJwtCred(credential, headers);*/

		if (user != null && !user.getEmail().isBlank()) {
			log.info("retrieved associated email: " + user.getEmail());
			log.info("jwt token is authenticated");
			return true;
		}
		log.error("unable to authenticate requested jwt token!");
		return false;
	}

}
