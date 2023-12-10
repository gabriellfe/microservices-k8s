package com.dailycodebuffer.CloudGateway.security;

import java.util.Calendar;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class CognitoFilter extends AbstractGatewayFilterFactory<CognitoFilter.Config>{


    public static class Config {
    }
	
	public CognitoFilter (){
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return ((exchange, chain)->{
			System.out.println("Bypass Filter: "+ Calendar.getInstance().getTime().toString());
			return chain.filter(exchange);
		});
	}
}
