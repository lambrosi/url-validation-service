package com.lucasambrosi.axr.urlvalidationservice.service;

import com.lucasambrosi.axr.urlvalidationservice.entity.GlobalWhitelist;
import com.lucasambrosi.axr.urlvalidationservice.input.ValidationInput;
import com.lucasambrosi.axr.urlvalidationservice.output.ValidationOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
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
        List<String> regexForClient = whitelistService.getRegexForClient(clientName);
        Optional<String> matchedRegex = getMatchedRegex(regexForClient, url);

        if (matchedRegex.isPresent()) {
            return matchedRegex;
        }
        return getMatchedRegexFromGlobalWhitelisPageable(url);
    }

    private Optional<String> getMatchedRegex(List<String> regexList, String url) {
        return regexList.stream()
                .distinct()
                .filter(regex -> filterByMatchedRegex(regex, url))
                .findFirst();
    }

    private Optional<String> getMatchedRegexFromGlobalWhitelisPageable(final String url) {
        Optional<String> matchedRegex = Optional.empty();

        Page<GlobalWhitelist> globalWhitelistPage = whitelistService.getAllRegexFromGlobalwhitelistPageable();
        for (int i = 0; i < globalWhitelistPage.getTotalPages(); i++) {
            List<String> regexForClient = globalWhitelistPage
                    .map(GlobalWhitelist::getRegex)
                    .getContent();

            matchedRegex = getMatchedRegex(regexForClient, url);

            if (matchedRegex.isPresent()) {
                return matchedRegex;
            }
            globalWhitelistPage = whitelistService.getAllRegexFromGlobalwhitelistPageable(globalWhitelistPage.nextPageable());
        }
        return matchedRegex;
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
