package com.lucasambrosi.axr.urlvalidationservice.messaging;

import com.lucasambrosi.axr.urlvalidationservice.output.ValidationOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitProducer.class);
    private RabbitTemplate rabbitTemplate;

    @Value("${RESPONSE_EXCHANGE}")
    private String responseExchange;
    @Value("${RESPONSE_ROUTING_KEY}")
    private String responseRoutingKey;

    public RabbitProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendResponseValidationMessage(final ValidationOutput validationOutput) {
        LOGGER.info("Sending message '{}' to exchange '{}' with routing key '{}'.",
                validationOutput.toString(), responseExchange, responseRoutingKey);
        rabbitTemplate.convertAndSend(responseExchange, responseRoutingKey, validationOutput);
    }
}
