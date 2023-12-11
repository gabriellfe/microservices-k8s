package com.dailycodebuffer.CloudGateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.dailycodebuffer.CloudGateway.model.AuthorizeException;
import com.dailycodebuffer.CloudGateway.utils.GwTokenUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CognitoFilter extends AbstractGatewayFilterFactory<CognitoFilter.Config> {

	private final String COGNITO_KEY = "eI2THAvlUfCzwaezyagi2HUOQ1H7KccHgUTfLjF6NHuMrfZmHdyowcP4rmqF_SDq";
	private final String COGNITO = "cognito";
	private final String GATEWAY_TOKEN = "gw_token";

	public static class Config {
	}

	public CognitoFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return ((exchange, chain) -> {
			if (exchange.getRequest().getHeaders().get(COGNITO).contains(COGNITO_KEY)) {
				exchange.getRequest().mutate().header(GATEWAY_TOKEN, GwTokenUtil.generateGwToken());
				log.info("Filtering: {} for {}", exchange.getRequest().getMethodValue(), exchange.getRequest().getURI());
				return chain.filter(exchange);
			}
			throw new AuthorizeException("Token invalid", HttpStatus.FORBIDDEN);
		});
	}
}
