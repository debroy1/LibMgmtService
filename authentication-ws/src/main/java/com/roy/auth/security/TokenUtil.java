package com.roy.auth.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenUtil {
	private final Logger log = LoggerFactory.getLogger(getClass());

	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";

	@Value("${token.secret}")
	public String TOKEN_SECRET;
	@Value("${token.expiry}")
	public Long TOKEN_EXPIRY;

	public TokenUtil() {
		log.info("Initializing TokenUtil");
	}

	public String extractTokenUserEmail(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	public Date extractTokenExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
	
	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	
	private Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(TOKEN_SECRET).parseClaimsJws(token).getBody();
	}
	
	public Boolean isTokenExpired(String token) {
		return extractTokenExpiration(token).before(new Date());
	}
	
	public String generateToken(String email) {
		log.info("Generating JWT token for email: " + email);
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, email);
	}
	
	private String createToken(Map<String, Object> claims, String email) {
		log.info("Creating jwt with expiry: " + TOKEN_EXPIRY);
		return Jwts.builder().setSubject(email)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRY))
				.signWith(SignatureAlgorithm.HS512, TOKEN_SECRET).compact();
	}

	public Boolean validateToken(String token, String email) {
		log.info("Validating JWT token for email: " + email);
		String tokenUserEmail = extractTokenUserEmail(token);
		return (email.equals(tokenUserEmail) && !isTokenExpired(token));
	}
	
	public String extractToken(HttpServletRequest request) {
		log.info("Extracting JWT token");
		String token = request.getHeader(HEADER_STRING);
		if(token != null) {
			token = token.replace(TOKEN_PREFIX, "");
		}
		return token;
	}

}
