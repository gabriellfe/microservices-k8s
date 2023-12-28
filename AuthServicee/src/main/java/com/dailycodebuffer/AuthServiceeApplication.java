package com.dailycodebuffer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class AuthServiceeApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceeApplication.class, args);
	}

}
