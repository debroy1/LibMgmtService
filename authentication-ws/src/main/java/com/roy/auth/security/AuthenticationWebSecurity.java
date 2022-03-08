package com.roy.auth.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class AuthenticationWebSecurity extends WebSecurityConfigurerAdapter {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Value("${gateway.ip}")
	public String gatewayIp;
	@Value("${gateway.subnet}")
	public String gatewaySubnet;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		log.info("gatewayIp - > " + gatewayIp);
		http.csrf().disable();
		//http.authorizeHttpRequests().antMatchers("/api/**").permitAll();
		http.authorizeRequests().antMatchers("/**").hasIpAddress(gatewaySubnet);
	}
}
