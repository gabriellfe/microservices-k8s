package com.dailycodebuffer.commons.service;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service("redisService")
@ConditionalOnProperty(value = "enable.redis", havingValue = "true", matchIfMissing = false)
public class RedisService {

	private final ObjectMapper mapper;
	private final RedisTemplate<String, Object> template;

	public RedisService(ObjectMapper mapper, RedisTemplate<String, Object> template) {
		this.mapper = mapper;
		this.template = template;
	}

	public synchronized Object getValue(final String key) {

		template.setHashValueSerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		return template.opsForValue().get(key);
	}

	public synchronized Object getValue(final String key, Class clazz) {
		template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
		template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

		Object obj = template.opsForValue().get(key);
		return mapper.convertValue(obj, clazz);
	}

	public void setValue(final String key, final Object value, TimeUnit unit, long timeout, boolean marshal) {
		if (marshal) {
			template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
			template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
		} else {
			template.setHashValueSerializer(new StringRedisSerializer());
			template.setValueSerializer(new StringRedisSerializer());
		}
		template.opsForValue().set(key, value);
		// set a expire for a message
		template.expire(key, timeout, unit);
	}

	public void removeKey(final String key) {
		template.delete(key);
	}
}