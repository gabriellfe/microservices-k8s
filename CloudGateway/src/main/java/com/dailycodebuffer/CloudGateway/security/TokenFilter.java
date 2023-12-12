package com.dailycodebuffer.CloudGateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.dailycodebuffer.CloudGateway.model.AuthorizeException;
import com.dailycodebuffer.CloudGateway.model.UsuarioRepository;
import com.dailycodebuffer.CloudGateway.utils.GwTokenUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TokenFilter extends AbstractGatewayFilterFactory<TokenFilter.Config> {

	private final String GATEWAY_TOKEN = "gw_token";
	private final String TOKEN = "token";
	private final String USER = "user_login";

	private UsuarioRepository repo;

	public static class Config {
	}

	public TokenFilter(UsuarioRepository repo) {
		super(Config.class);
		this.repo = repo;
	}

	@Override
	public GatewayFilter apply(Config config) {
		return ((exchange, chain) -> {
			if (exchange.getRequest().getHeaders().get(TOKEN) != null) {
				String secret = repo.findByEmail(exchange.getRequest().getHeaders().getFirst(USER)).getSecret();
				JWT.require(Algorithm.HMAC256(secret)).build().verify(exchange.getRequest().getHeaders().getFirst(TOKEN));
				exchange.getRequest().mutate().header(GATEWAY_TOKEN, GwTokenUtil.generateGwToken());
				log.info("Filtering: {} for {}", exchange.getRequest().getMethodValue(),
						exchange.getRequest().getURI());
				return chain.filter(exchange);
			}
			throw new AuthorizeException("Token invalid", HttpStatus.FORBIDDEN);
		});
	}
}
