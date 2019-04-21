package com.lucasambrosi.axr.urlvalidationservice.messaging;

import com.lucasambrosi.axr.urlvalidationservice.input.RegexInput;
import com.lucasambrosi.axr.urlvalidationservice.input.ValidationInput;
import com.lucasambrosi.axr.urlvalidationservice.output.ValidationOutput;
import com.lucasambrosi.axr.urlvalidationservice.service.ValidationService;
import com.lucasambrosi.axr.urlvalidationservice.service.WhitelistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class RabbitConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitConsumer.class);
    private RabbitProducer rabbitProducer;
    private WhitelistService whitelistService;
    private ValidationService validationService;

    public RabbitConsumer(RabbitProducer rabbitProducer,
                          WhitelistService whitelistService,
                          ValidationService validationService) {
        this.rabbitProducer = rabbitProducer;
        this.whitelistService = whitelistService;
        this.validationService = validationService;
    }

    @RabbitListener(queues = "${INSERTION_QUEUE}", containerFactory = "genericListenerContainerFactory")
    public void insertRegex(@Payload final RegexInput regexInput) {
        LOGGER.info("Received message {}.", regexInput.toString());
        try {
            whitelistService.insertRegularExpression(regexInput);
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Error on insert regular expression. Body: " + regexInput.toString(), ex);
        }
    }

    @RabbitListener(queues = "${VALIDATION_QUEUE}", containerFactory = "validationListenerContainerFactory")
    public void validateUrl(@Payload final ValidationInput validationInput) {
        LOGGER.info("Received message {}.", validationInput.toString());
        try {
            ValidationOutput output = validationService.validate(validationInput);
            rabbitProducer.sendResponseValidationMessage(output);
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Error on validate URL. Body: " + validationInput.toString(), ex);
        }
    }
}
