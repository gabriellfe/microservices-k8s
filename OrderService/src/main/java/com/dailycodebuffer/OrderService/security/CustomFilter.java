package com.dailycodebuffer.OrderService.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.auth.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Component
public class CustomFilter extends OncePerRequestFilter {
    

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
    	System.out.println("entrou filter");
    	if (request.getHeader("cognito") != null){
//    		valida
    		System.out.println("filtou cognito");
    		filterChain.doFilter(request, response);
    	}
    	else if (request.getHeader("Authorization") != null){
//    		valida
    		System.out.println("filtou Authorization");
    		filterChain.doFilter(request, response);
    	}else {
    		response.setStatus(401);
		}
//    	throw new RuntimeException();
    }

}