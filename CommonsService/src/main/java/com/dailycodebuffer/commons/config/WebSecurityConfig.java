package com.dailycodebuffer.commons.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(value = "enable.web.security.filter", havingValue = "true", matchIfMissing = false)
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class WebSecurityConfig {

	@Bean
	public SecurityFilterChain securityWebFilterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests(authorizeRequest -> {
			try {
				authorizeRequest.anyRequest().permitAll().and().csrf().disable();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return http.build();
	}
}