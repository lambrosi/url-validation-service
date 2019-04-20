package com.lucasambrosi.axr.urlvalidationservice.messaging;

import com.lucasambrosi.axr.urlvalidationservice.input.RegexInput;
import com.lucasambrosi.axr.urlvalidationservice.input.ValidationInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class RabbitConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitConsumer.class);

    @RabbitListener(queues = "${INSERTION_QUEUE}", containerFactory = "genericListenerContainerFactory")
    public void insertRegex(@Payload final RegexInput regexInput) {
        LOGGER.info("Received message {}.", regexInput.toString());
    }

    @RabbitListener(queues = "${VALIDATION_QUEUE}", containerFactory = "validationListenerContainerFactory")
    public void validateUrl(@Payload final ValidationInput validationInput) {
        LOGGER.info("Received message {}.", validationInput.toString());
    }

}
