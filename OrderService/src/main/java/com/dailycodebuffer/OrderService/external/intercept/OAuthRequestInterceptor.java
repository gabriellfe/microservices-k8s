package com.dailycodebuffer.OrderService.external.intercept;

import org.springframework.context.annotation.Configuration;

import com.dailycodebuffer.commons.utils.GwTokenUtil;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Configuration
public class OAuthRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        template.header("gw_token", GwTokenUtil.generateGwToken());
    }
}
