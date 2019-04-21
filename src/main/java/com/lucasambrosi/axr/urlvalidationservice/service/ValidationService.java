package com.lucasambrosi.axr.urlvalidationservice.service;

import com.lucasambrosi.axr.urlvalidationservice.input.ValidationInput;
import com.lucasambrosi.axr.urlvalidationservice.output.ValidationOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class ValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationService.class);
    private WhitelistService whitelistService;

    public ValidationService(WhitelistService whitelistService) {
        this.whitelistService = whitelistService;
    }

    public ValidationOutput validate(ValidationInput input) {
        this.validateInputParameters(input);

        Optional<String> matchedRegex = this.getFirstMatchedRegex(input.getClient(), input.getUrl());

        ValidationOutput output = new ValidationOutput();
        output.setMatch(matchedRegex.isPresent());
        output.setRegex(matchedRegex.orElse(null));
        output.setCorrelationId(input.getCorrelationId());
        return output;
    }

    private Optional<String> getFirstMatchedRegex(String clientName, String url) {
        return whitelistService.getAllRegexForAClient(clientName)
                .stream()
                .filter(it -> filterByMatchedRegex(it, url))
                .findFirst();
    }

    private boolean filterByMatchedRegex(String regex, String url) {
        LOGGER.info("Verifying if regex '{}' match with URL '{}'.", regex, url);
        return Pattern.matches(regex, url);
    }

    private void validateInputParameters(final ValidationInput input) {
        LOGGER.info("Validating input parameters.");
        if (StringUtils.isEmpty(input.getClient())) {
            throw new IllegalArgumentException("Client name must not be null.");
        }

        if (StringUtils.isEmpty(input.getUrl())) {
            throw new IllegalArgumentException("URL must not be null.");
        }

        if (StringUtils.isEmpty(input.getCorrelationId())) {
            throw new IllegalArgumentException("CorrelationId must not be null.");
        }
    }
}
