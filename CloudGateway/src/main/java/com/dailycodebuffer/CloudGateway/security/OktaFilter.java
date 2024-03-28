package com.dailycodebuffer.CloudGateway.security;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.dailycodebuffer.CloudGateway.model.AuthorizeException;
import com.dailycodebuffer.CloudGateway.util.GwTokenUtil;
import com.okta.jwt.AccessTokenVerifier;
import com.okta.jwt.Jwt;
import com.okta.jwt.JwtVerificationException;
import com.okta.jwt.JwtVerifiers;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OktaFilter extends AbstractGatewayFilterFactory<OktaFilter.Config> {

	private final String ACCESSTOKEN = "accessToken";
	private final String GATEWAY_TOKEN = "gw_token";

	public static class Config {
	}

	public OktaFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return ((exchange, chain) -> {
			try {
				if (exchange.getRequest().getHeaders().get(ACCESSTOKEN) != null) {
					AccessTokenVerifier jwtVerifier = JwtVerifiers.accessTokenVerifierBuilder()
							.setIssuer("https://dev-56986490.okta.com/oauth2/default").setAudience("api://default") // defaults
							.setConnectionTimeout(Duration.ofSeconds(1)) // defaults to 1s
							.setRetryMaxAttempts(2) // defaults to 2
							.setRetryMaxElapsed(Duration.ofSeconds(10)) // defaults to 10s
							.build();
					LocalDateTime now = LocalDateTime.now();
					Jwt jwt = jwtVerifier.decode(exchange.getRequest().getHeaders().getFirst(ACCESSTOKEN));
					
					if (jwt.getExpiresAt().compareTo(now.toInstant(ZoneOffset.UTC)) < 0) {
						log.info("token expired: {}", jwt.getExpiresAt().compareTo(now.toInstant(ZoneOffset.UTC)));
						throw new AuthorizeException("Token expired", HttpStatus.FORBIDDEN);
					}
	
					log.info("Filtering: {} for {}", exchange.getRequest().getMethodValue(),exchange.getRequest().getURI());
					exchange.getRequest().mutate().header(GATEWAY_TOKEN, GwTokenUtil.generateGwToken());
					return chain.filter(exchange);
				}
			} catch (JwtVerificationException e) {
				e.printStackTrace();
			}
			throw new AuthorizeException("Token invalid", HttpStatus.FORBIDDEN);
		});
	}
}
