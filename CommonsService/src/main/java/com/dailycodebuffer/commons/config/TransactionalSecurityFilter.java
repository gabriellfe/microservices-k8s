package com.dailycodebuffer.commons.config;

import java.io.IOException;
import java.util.Base64;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.dailycodebuffer.commons.dto.UserDTO;
import com.dailycodebuffer.commons.service.RedisService;
import com.dailycodebuffer.commons.utils.GwTokenUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@ConditionalOnProperty(value = "enable.transaction.filter", havingValue = "true", matchIfMissing = false)
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class TransactionalSecurityFilter implements Filter {
	
	private final String TOKEN = "token";
	
	@Autowired
	private RedisService redisService;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		if (req.getRequestURI().contains("/livenessProbe")) {
			chain.doFilter(request, response);
			return;
		}
		if (req.getRequestURI().contains("/actuator/**")) {
			chain.doFilter(request, response);
			return;
		}
		if (req.getRequestURI().contains("/user/login")) {
			chain.doFilter(request, response);
			return;
		}
		boolean isGwToken = GwTokenUtil.validateGwToken(req);
		if (isGwToken) {
			log.info("Authorized and Called by Gateway");
			chain.doFilter(request, response);
			return;
		}

		boolean isAuthorization = this.validateAuthorization(req);
		if (isAuthorization) {
			chain.doFilter(request, response);
			return;
		}

		HttpServletResponse res = (HttpServletResponse) response;
		res.setStatus(401);

	}

	private boolean validateAuthorization(HttpServletRequest req) {
		try {
			String jwtToken = req.getHeader(TOKEN);
			log.info("Token: [{}]", jwtToken);
			String base64EncodedBody = jwtToken.split("\\.")[1];
			String body = new String(Base64.getDecoder().decode(base64EncodedBody));
			ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			UserDTO user = objectMapper.readValue(body, UserDTO.class);
			log.info("Client: [{}]", user.getClient());
			log.info("Secret: [{}]", redisService.getValue("AUTH_" + user.getClient()));
			String secret = (String) redisService.getValue("AUTH_" + user.getClient());
			JWT.require(Algorithm.HMAC256(secret)).build().verify(jwtToken);
			return true;
		} catch (Exception e) {
			log.error("Error: {}", e);
		}
		return false;
	}

}