package com.dailycodebuffer.CloudGateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "s3")
public class S3Properties {
	
	private String host;
	private int port;
	private String pass;
}
