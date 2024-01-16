package com.dailycodebuffer.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.dailycodebuffer.utils.GwTokenUtil;

@Component
public class TransactionalSecurityFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		if (req.getRequestURI().contains("/livenessProbe")) {
			chain.doFilter(request, response);
			return;
		}
		boolean isGwToken = GwTokenUtil.validateGwToken(req);
		if (isGwToken) {
			chain.doFilter(request, response);
			return;
		}

		boolean isAuthorization = this.validateAuthorization(req);
		if (isAuthorization) {
			chain.doFilter(request, response);
			return;
		}

		HttpServletResponse res = (HttpServletResponse) response;
		res.setStatus(401);

	}

	private boolean validateAuthorization(HttpServletRequest req) {
		// validate Authorization
		return false;
	}

}