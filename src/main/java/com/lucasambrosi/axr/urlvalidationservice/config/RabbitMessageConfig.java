package com.lucasambrosi.axr.urlvalidationservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMessageConfig {

    private static final String RESPONSE_QUEUE = "response.queue";
    @Value("${INSERTION_QUEUE}")
    private String insertionQueue;
    @Value("${VALIDATION_QUEUE}")
    private String validationQueue;

    @Value("${RESPONSE_EXCHANGE}")
    private String responseExchange;
    @Value("${RESPONSE_ROUTING_KEY}")
    private String responseRoutingKey;

    @Bean
    public Queue responseQueue() {
        return this.createDurableQueue(RESPONSE_QUEUE);
    }

    @Bean
    public Queue insertionQueue() {
        return this.createDurableQueue(insertionQueue);
    }

    @Bean
    public Queue validationQueue() {
        return this.createDurableQueue(validationQueue);
    }

    private Queue createDurableQueue(String queueName) {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    public TopicExchange responseExchange() {
        return (TopicExchange) ExchangeBuilder.topicExchange(responseExchange)
                .durable(Boolean.TRUE)
                .build();
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(responseQueue())
                .to(responseExchange())
                .with(responseRoutingKey);
    }
}
