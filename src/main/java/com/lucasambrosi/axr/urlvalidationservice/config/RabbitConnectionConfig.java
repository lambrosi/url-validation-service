package com.lucasambrosi.axr.urlvalidationservice.config;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConnectionConfig {

    @Value("${RABBITMQ_HOST}")
    private String hostname;
    @Value("${RABBITMQ_PORT}")
    private int port;
    @Value("${RABBITMQ_VHOST}")
    private String virtualHost;
    @Value("${RABBITMQ_USERNAME}")
    private String username;
    @Value("${RABBITMQ_PASSWORD}")
    private String password;

    @Value("${NUMBER_OF_VALIDATION_CONSUMERS}")
    private Integer maxValidationConsumers;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(hostname, port);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory genericListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setMessageConverter(jackson2JsonMessageConverter());
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory validationListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = this.genericListenerContainerFactory();
        factory.setMaxConcurrentConsumers(maxValidationConsumers);
        return factory;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
