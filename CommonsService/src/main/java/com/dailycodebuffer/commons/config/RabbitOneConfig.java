package com.dailycodebuffer.commons.config;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnProperty(value = "enable.rabbit.one", havingValue = "true", matchIfMissing = false)
public class RabbitOneConfig {

	@Value("${rabbit.one.host}")
	private String host;
	@Value("${rabbit.one.port}")
	private int port;
	@Value("${rabbit.one.username}")
	private String username;
	@Value("${rabbit.one.pass}")
	private String pass;

	// create MessageListenerContainer using default connection factory
	@Primary
	@Bean(name = "MessaListenerContainerRabbitOnePrimary")
	@ConditionalOnBean(name = "containerFactoryRabbitOnePrimary")
	MessageListenerContainer messageListenerContainerPrimary(
			@Qualifier(value = "containerFactoryRabbitOnePrimary") ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
		simpleMessageListenerContainer.setConnectionFactory(connectionFactory);
		return simpleMessageListenerContainer;
	}

	// create custom connection factory
	@Primary
	@Bean(name = "containerFactoryRabbitOnePrimary")
	@ConditionalOnProperty(value = "enable.rabbit.one.primary", havingValue = "true", matchIfMissing = false)
	ConnectionFactory connectionFactoryPrimary() {
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(host, port);
		cachingConnectionFactory.setUsername(username);
		cachingConnectionFactory.setPassword(pass);
		log.info("Connecting to rabbit host as Primary: [{}]", host);
		return cachingConnectionFactory;
	}

	@Primary
	@Bean(name = "rabbitOneListenerContainerFactoryPrimary")
	@ConditionalOnBean(name = "containerFactoryRabbitOnePrimary")
	public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactoryPrimary(
			SimpleRabbitListenerContainerFactoryConfigurer configurer,
			@Qualifier(value = "containerFactoryRabbitOnePrimary") ConnectionFactory connectionFactory) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		configurer.configure(factory, connectionFactory);
		factory.setDefaultRequeueRejected(false);
		return factory;
	}

	@Primary
	@Bean(name = "rabbitTemplateRabbitOnePrimary")
	@ConditionalOnBean(name = "containerFactoryRabbitOnePrimary")
	public RabbitTemplate rabbitTemplatePrimary(
			@Qualifier(value = "containerFactoryRabbitOnePrimary") ConnectionFactory connectionFactory) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(new Jackson2JsonMessageConverter());
		return template;
	}

	@Bean(name = "MessaListenerContainerRabbitOne")
	@ConditionalOnMissingBean(name = "containerFactoryRabbitOnePrimary")
	MessageListenerContainer messageListenerContainer(
			@Qualifier(value = "containerFactoryRabbitOne") ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
		simpleMessageListenerContainer.setConnectionFactory(connectionFactory);
		return simpleMessageListenerContainer;
	}

	// create custom connection factory
	@Bean(name = "containerFactoryRabbitOne")
	@ConditionalOnMissingBean(name = "containerFactoryRabbitOnePrimary")
	ConnectionFactory connectionFactory() {
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(host, port);
		cachingConnectionFactory.setUsername(username);
		cachingConnectionFactory.setPassword(pass);
		log.info("Connecting to rabbit host: [{}]", host);
		return cachingConnectionFactory;
	}

	@Bean(name = "rabbitOneListenerContainerFactory")
	@ConditionalOnMissingBean(name = "containerFactoryRabbitOnePrimary")
	public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(
			SimpleRabbitListenerContainerFactoryConfigurer configurer,
			@Qualifier(value = "containerFactoryRabbitOne") ConnectionFactory connectionFactory) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		configurer.configure(factory, connectionFactory);
		factory.setDefaultRequeueRejected(false);
		return factory;
	}

	@Bean(name = "rabbitTemplateRabbitOne")
	@ConditionalOnMissingBean(name = "containerFactoryRabbitOnePrimary")
	public RabbitTemplate rabbitTemplate(
			@Qualifier(value = "containerFactoryRabbitOne") ConnectionFactory connectionFactory) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(new Jackson2JsonMessageConverter());
		return template;
	}
}
