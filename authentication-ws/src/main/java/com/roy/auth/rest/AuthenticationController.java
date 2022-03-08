package com.roy.auth.rest;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roy.auth.atlas.AtlasService;
import com.roy.auth.atlas.UserItem;
import com.roy.auth.security.TokenUtil;

@RestController
@RequestMapping("/api")
public class AuthenticationController {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Value("${gateway.header.token}")
	public String gatewayHeaderToken;
	@Value("${gateway.header.token.value}")
	public String gatewayHeaderTokenValue;

	@Autowired
	private AtlasService atlasService;
	
	@Autowired
	private TokenUtil tokenUtil;
	
	@Autowired
	BCryptPasswordEncoder passwordEncoder;
	
    @GetMapping("/health")
    public String healthCheck() {
    	return "Healthy!";
    }

	@PostMapping("/signup")
	ResponseEntity<UserInfo> signupUser(@RequestBody UserCredential credential, @RequestHeader Map<String, String> headers) {
		log.info("received request to create user");
		if(!hasValidRequestHeader(headers)) {
			log.warn("unauthorized request");
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);			
		}
		if(!isValidUserCredential(credential)) {
			log.warn("invalid request - check input");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		try {
			Optional<UserItem> user = atlasService.getUserByEmail(credential.getEmail());
			if(user.isPresent()) {
				log.error("existing user found");
				return new ResponseEntity<>(HttpStatus.CONFLICT);
			}
			UserItem userItem = new UserItem();
			BeanUtils.copyProperties(credential, userItem);
			userItem.setPassword(passwordEncoder.encode(credential.getPassword()));
			UserItem _userItem = new UserItem();
			_userItem = atlasService.saveUser(userItem);
			if(_userItem == null) {
				log.warn("unable to save user");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			} else {
				log.info("able to save user with id: " + _userItem.getId());
				UserInfo _userInfo = new UserInfo();
				BeanUtils.copyProperties(_userItem, _userInfo);
				_userInfo.setJwt(addJwtToken(credential.getEmail()));
				return new ResponseEntity<>(_userInfo, HttpStatus.CREATED);
			}
		} catch (Exception e) {
			log.warn("error encountered while saving user -> " + e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/authenticateUser")
	ResponseEntity<UserInfo> authenticateUserCred(@RequestBody UserCredential credential, @RequestHeader Map<String, String> headers) {
		if(!hasValidRequestHeader(headers)) {
			log.warn("unauthorized request - missing ot invalid header");
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		if(!isValidUserCredential(credential)) {
			log.warn("invalid request - check input");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		Optional<UserItem> user = atlasService.getUserByEmail(credential.getEmail());
		if(user.isPresent()) {
			UserItem _userItem = user.get();
			if(passwordEncoder.matches(credential.getPassword(), _userItem.getPassword())) {
				log.info("user is authenticated");
				UserInfo _userInfo = new UserInfo();
				BeanUtils.copyProperties(_userItem, _userInfo);
				_userInfo.setJwt(addJwtToken(credential.getEmail()));
				return new ResponseEntity<>(_userInfo, HttpStatus.OK);
			}
		}
		log.info("user is not able to be authenticated");
		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	}

	@PostMapping("/authenticateJwt")
	ResponseEntity<UserInfo> authenticateJwtCred(@RequestBody UserCredential credential, @RequestHeader Map<String, String> headers) {
		if(!hasValidRequestHeader(headers)) {
			log.warn("unauthorized request - missing ot invalid header");
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);			
		}
		if(!isValidJwtCredential(credential)) {
			log.warn("invalid request - check input");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		try {
			String jwt = credential.getJwt();
			String token = extractJwtToken(jwt);
			String email = tokenUtil.extractTokenUserEmail(token);
			if(email != null && !email.isBlank() && tokenUtil.validateToken(token, email)) {
				Optional<UserItem> user = atlasService.getUserByEmail(email);
				if(user.isPresent()) {
					UserItem _userItem = user.get();
					UserInfo _userInfo = new UserInfo();
					BeanUtils.copyProperties(_userItem, _userInfo);
					_userInfo.setJwt(jwt);
					log.info("token is authenticated");
					return new ResponseEntity<>(_userInfo, HttpStatus.OK);
				} else {
					log.warn("invalid token - user email not found");
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			log.warn("invalid token - error in validating token -> " + e.getMessage());
		}
		log.info("token is not able to be authenticated");
		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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

	private boolean isValidUserCredential(UserCredential credential) {
		if(credential == null) {
			log.warn("invalid request - credential is empty");
			return false;
		} else if (credential.getEmail() == null || credential.getEmail().isBlank()
				|| credential.getPassword() == null || credential.getPassword().isBlank()) {
			log.warn("invalid request - email and/or password is/are empty");
			return false;
		}
		return true;
	}
	
	private boolean isValidJwtCredential(UserCredential credential) {
		if(credential == null) {
			log.warn("invalid request - credential is empty");
			return false;
		} else if (credential.getJwt() == null || credential.getJwt().isBlank()) {
			log.warn("invalid request - token is empty");
			return false;
		}
		return true;
	}
	
	private String addJwtToken(String email) {
		String token = tokenUtil.generateToken(email);
		return (TokenUtil.TOKEN_PREFIX + token);
	}
	
	private String extractJwtToken(String jwt) {
		String token = jwt;
		if(jwt != null) {
			token = jwt.replace(TokenUtil.TOKEN_PREFIX, "");
		}
		return token;
	}

}
