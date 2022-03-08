package com.roy.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class ApiGatewayGlobalFilter implements GlobalFilter {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Value("${auth-svc.url.authenticate}")
	public String authSvcAuthenticateUrl;
	@Value("${auth-svc.url.signup}")
	public String authSvcSignupUrl;
	@Value("${auth-svc.url.userauth}")
	public String authSvcUserAuthUrl;
	@Value("${auth-svc.url.jwtauth}")
	public String authSvcJwtAuthUrl;
	@Value("${actuator.url.path}")
	public String actuatorPathUrl;
	@Value("${gateway.header.token}")
	public String gatewayHeaderToken;
	@Value("${gateway.header.token.value}")
	public String gatewayHeaderTokenValue;

	@Autowired
	private TokenUtil tokenUtil;

	@Autowired
	public RestTemplate restTemplate;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String path = exchange.getRequest().getPath().toString();
		log.info("Global filter intercepted -> " + path);

		log.info("Printing headers");
		HttpHeaders headers = exchange.getRequest().getHeaders();
		headers.forEach((key, value) -> {
			log.info(key + " -> " + value);
		});

		// Allow some predefined paths without auth header - else authorize all other requests here
		if (path.equals(authSvcSignupUrl) || path.equals(authSvcUserAuthUrl) || path.equals(authSvcJwtAuthUrl) || path.equals(actuatorPathUrl)) {
			log.info("Allow request: " + path);
		} else {
			log.info("Need to authorize request: " + path);
			if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
				log.error("Missing required auth header!");
				return onError(exchange, "Missing required auth header!", HttpStatus.UNAUTHORIZED);
				//throw new RuntimeException("Missing required auth header!");
			} else {
				try {
					String authJwtToken = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
					
					// keep local validation or service side validation
					String token = extractJwtToken(authJwtToken);
					String email = tokenUtil.extractTokenUserEmail(token);
					if(email != null && !email.isBlank() && tokenUtil.validateToken(token, email)) {
						log.info("retrieved associated email: " + email);
						log.info("token is authenticated");						
					} else {
						log.error("Unable to authenticate requested token!");
						return onError(exchange, "Unable to authenticate requested token!", HttpStatus.UNAUTHORIZED);
						//throw new RuntimeException("Unable to authenticate requested token!");						
					}
					
					// call auth-service to validate the token
					/*UserCredential credential = new UserCredential();
					credential.setJwt(authJwtToken);
					log.info("About to invoke auth-service api to validate the jwt access token");
					HttpHeaders httpHeaders = new HttpHeaders();
					httpHeaders.add(HttpHeaders.AUTHORIZATION, authJwtToken);
					httpHeaders.add(gatewayHeaderToken, gatewayHeaderTokenValue);
			        HttpEntity<UserCredential> entity = new HttpEntity<UserCredential>(credential, httpHeaders);
			        UserInfo user = restTemplate.postForObject(authSvcAuthenticateUrl, entity, UserInfo.class);
					if (user != null && !user.getEmail().isBlank()) {
						log.info("retrieved associated email: " + user.getEmail());
						log.info("jwt token is authenticated");
					} else {
						log.error("Unable to authenticate requested token!");
						throw new RuntimeException("Unable to authenticate requested token!");						
					}*/
				}
				catch (Exception e) {
					e.printStackTrace();
					log.error("invalid token - error in validating token -> " + e.getMessage());
					log.error("detail stacktrace: " + e.getStackTrace());
					return onError(exchange, "Unable to authenticate requested token!", HttpStatus.UNAUTHORIZED);
					//throw new RuntimeException("Unable to authenticate requested token!");
				}
				
				log.info("Request is authorized: " + path);
			}
		}

		// Add API Gateway signature token
		log.info("Adding API Gateway signature token into header");
		exchange.getRequest().mutate().header(gatewayHeaderToken, gatewayHeaderTokenValue);

		return chain.filter(exchange);
	}

	private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(status);
		return response.setComplete();
	}

	private String extractJwtToken(String jwt) {
		String token = jwt;
		if(jwt != null) {
			token = jwt.replace(TokenUtil.TOKEN_PREFIX, "");
		}
		return token;
	}
}
