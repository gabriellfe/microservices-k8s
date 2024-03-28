package com.dailycodebuffer.commons.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {
	
	private String host;
	private int port;
	private String pass;
}
