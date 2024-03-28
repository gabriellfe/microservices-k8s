package com.dailycodebuffer.commons.utils;

import java.util.Collections;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GwRestClientUtil {
	
	private RestTemplate restTemplate;
	private ObjectMapper mapper;

	public GwRestClientUtil(RestTemplate restTemplate, ObjectMapper mapper) {
		super();
		this.restTemplate = restTemplate;
		this.mapper = mapper;
	}

	
	public static GwRestClientUtil getInstance() {
		return new GwRestClientUtil(new RestTemplate(), new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false));
	}
	
	public HttpHeaders getHeaders(){
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("gw_token", GwTokenUtil.generateGwToken());
		return headers;
	}
	
	public <T> T jsonPost(String url, Object object, Class<?> clazz) throws Exception{
		return this.doRequest(url, object, HttpMethod.POST, clazz);
	}
	
	public <T> T jsonPut(String url, Object object, Class<?> clazz) throws Exception{
		return this.doRequest(url, object, HttpMethod.PUT, clazz);
	}
	
	public <T> T jsonPut(String url, Class<?> clazz) throws Exception{
		return this.doRequest(url, HttpMethod.PUT, clazz);
	}

	public <T> T jsonGet(String url, Object object, Class<?> clazz) throws Exception{
		return this.doRequest(url, object, HttpMethod.GET, clazz);
	}
	
	public <T> T jsonGet(String url, Class<?> clazz) throws Exception{
		return this.doRequest(url, HttpMethod.GET, clazz);
	}
	
	public <T> T jsonPatch(String url, Object object, Class<?> clazz) throws Exception{
		return this.doRequest(url, object, HttpMethod.PATCH, clazz);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T doRequest(String url, Object object, HttpMethod method, Class<?> clazz) throws Exception{
		String body = this.mapper.writeValueAsString(object);
		HttpEntity<String> entity = new HttpEntity<>(body, getHeaders());
		return (T) restTemplate.exchange(url, method, entity, clazz);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T doRequest(String url, HttpMethod method, Class<?> clazz) throws Exception{
		HttpEntity<String> entity = new HttpEntity<>(getHeaders());
		return (T) restTemplate.exchange(url, method, entity, clazz);
	}

}
