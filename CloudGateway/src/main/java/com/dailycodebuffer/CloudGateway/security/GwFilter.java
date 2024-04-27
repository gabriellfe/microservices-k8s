package com.dailycodebuffer.CloudGateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.dailycodebuffer.CloudGateway.model.AuthorizeException;
import com.dailycodebuffer.CloudGateway.util.GwTokenUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GwFilter extends AbstractGatewayFilterFactory<GwFilter.Config> {

	private final String GATEWAY_TOKEN = "gw_token";

	public static class Config {
	}

	public GwFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return ((exchange, chain) -> {
			if (exchange.getRequest().getHeaders().get(GATEWAY_TOKEN) != null) {
				String jwtToken = exchange.getRequest().getHeaders().getFirst(GATEWAY_TOKEN);
				boolean result = GwTokenUtil.validateGwToken(jwtToken);
				if (result) {
					log.info("Filtering: {} for {}", exchange.getRequest().getMethodValue(), exchange.getRequest().getURI());
					return chain.filter(exchange);
				}
			}
			throw new AuthorizeException("Token invalid", HttpStatus.FORBIDDEN);
		});
	}
}
