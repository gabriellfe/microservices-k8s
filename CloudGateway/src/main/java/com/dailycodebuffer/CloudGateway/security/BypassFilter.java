package com.dailycodebuffer.CloudGateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import com.dailycodebuffer.commons.utils.GwTokenUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BypassFilter extends AbstractGatewayFilterFactory<BypassFilter.Config> {

	private final String GATEWAY_TOKEN = "gw_token";

	public static class Config {
	}

	public BypassFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return ((exchange, chain) -> {
			exchange.getRequest().mutate().header(GATEWAY_TOKEN, GwTokenUtil.generateGwToken());
			log.info("Filtering: {} for {}", exchange.getRequest().getMethodValue(), exchange.getRequest().getURI());
			return chain.filter(exchange);
		});
	}
}
