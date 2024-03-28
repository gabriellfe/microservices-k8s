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
@ConditionalOnProperty(value = "enable.rabbit.two", havingValue = "true", matchIfMissing = false)
public class RabbitTwoConfig {

	@Value("${rabbit.two.host}")
	private String host;
	@Value("${rabbit.two.port}")
	private int port;
	@Value("${rabbit.two.username}")
	private String username;
	@Value("${rabbit.two.pass}")
	private String pass;

	// create MessageListenerContainer using default connection factory
	@Primary
	@Bean(name = "MessaListenerContainerRabbitTwoPrimary")
	@ConditionalOnBean(name = "containerFactoryRabbitTwoPrimary")
	MessageListenerContainer messageListenerContainerPrimary(
			@Qualifier(value = "containerFactoryRabbitTwoPrimary") ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
		simpleMessageListenerContainer.setConnectionFactory(connectionFactory);
		return simpleMessageListenerContainer;
	}

	// create custom connection factory
	@Primary
	@Bean(name = "containerFactoryRabbitTwoPrimary")
	@ConditionalOnProperty(value = "enable.rabbit.two.primary", havingValue = "true", matchIfMissing = false)
	ConnectionFactory connectionFactoryPrimary() {
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(host, port);
		cachingConnectionFactory.setUsername(username);
		cachingConnectionFactory.setPassword(pass);
		log.info("Connecting to rabbit host as Primary: [{}]", host);
		return cachingConnectionFactory;
	}

	@Primary
	@Bean(name = "rabbitTwoListenerContainerFactoryPrimary")
	@ConditionalOnBean(name = "containerFactoryRabbitTwoPrimary")
	public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactoryPrimary(
			SimpleRabbitListenerContainerFactoryConfigurer configurer,
			@Qualifier(value = "containerFactoryRabbitTwoPrimary") ConnectionFactory connectionFactory) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		configurer.configure(factory, connectionFactory);
		factory.setDefaultRequeueRejected(false);
		return factory;
	}

	@Primary
	@Bean(name = "rabbitTemplateRabbitTwoPrimary")
	@ConditionalOnBean(name = "containerFactoryRabbitTwoPrimary")
	public RabbitTemplate rabbitTemplatePrimary(
			@Qualifier(value = "containerFactoryRabbitTwoPrimary") ConnectionFactory connectionFactory) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(new Jackson2JsonMessageConverter());
		return template;
	}

	@Bean(name = "MessaListenerContainerRabbitTwo")
	@ConditionalOnMissingBean(name = "containerFactoryRabbitTwoPrimary")
	MessageListenerContainer messageListenerContainer(
			@Qualifier(value = "containerFactoryRabbitTwo") ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
		simpleMessageListenerContainer.setConnectionFactory(connectionFactory);
		return simpleMessageListenerContainer;
	}

	// create custom connection factory
	@Bean(name = "containerFactoryRabbitTwo")
	@ConditionalOnMissingBean(name = "containerFactoryRabbitTwoPrimary")
	ConnectionFactory connectionFactory() {
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(host, port);
		cachingConnectionFactory.setUsername(username);
		cachingConnectionFactory.setPassword(pass);
		log.info("Connecting to rabbit host: [{}]", host);
		return cachingConnectionFactory;
	}

	@Bean(name = "rabbitTwoListenerContainerFactory")
	@ConditionalOnMissingBean(name = "containerFactoryRabbitTwoPrimary")
	public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(
			SimpleRabbitListenerContainerFactoryConfigurer configurer,
			@Qualifier(value = "containerFactoryRabbitTwo") ConnectionFactory connectionFactory) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		configurer.configure(factory, connectionFactory);
		factory.setDefaultRequeueRejected(false);
		return factory;
	}

	@Bean(name = "rabbitTemplateRabbitTwo")
	@ConditionalOnMissingBean(name = "containerFactoryRabbitTwoPrimary")
	public RabbitTemplate rabbitTemplate(
			@Qualifier(value = "containerFactoryRabbitTwo") ConnectionFactory connectionFactory) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(new Jackson2JsonMessageConverter());
		return template;
	}
}
