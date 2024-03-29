package com.dailycodebuffer.CloudGateway.security;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.dailycodebuffer.CloudGateway.model.AuthorizeException;
import com.dailycodebuffer.CloudGateway.model.UserDTO;
import com.dailycodebuffer.CloudGateway.service.RedisService;
import com.dailycodebuffer.CloudGateway.util.GwTokenUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TokenFilter extends AbstractGatewayFilterFactory<TokenFilter.Config> {

	private final String GATEWAY_TOKEN = "gw_token";
	private final String TOKEN = "token";
	
	@Autowired
	private RedisService redisService;

	public static class Config {
	}

	public TokenFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return ((exchange, chain) -> {
			try {
				if (exchange.getRequest().getHeaders().get(TOKEN) != null) {
					String jwtToken = exchange.getRequest().getHeaders().getFirst(TOKEN);
					String base64EncodedBody = jwtToken.split("\\.")[1];
					String body = new String(Base64.getDecoder().decode(base64EncodedBody));
					ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);;
					UserDTO user = objectMapper.readValue(body, UserDTO.class);

					String secret = (String) redisService.getValue("AUTH_" + user.getClient());
					log.info("Secret in redis: [{}]", secret);
					JWT.require(Algorithm.HMAC256(secret)).build().verify(exchange.getRequest().getHeaders().getFirst(TOKEN));
					exchange.getRequest().mutate().header(GATEWAY_TOKEN, GwTokenUtil.generateGwToken());
					log.info("Filtering: {} for {}", exchange.getRequest().getMethodValue(),exchange.getRequest().getURI());
					return chain.filter(exchange);
				}
			} catch (Exception e) {
				log.error("Error: {}", e);
			}
			throw new AuthorizeException("Token invalid", HttpStatus.FORBIDDEN);
		});
	}
}
