package com.lucasambrosi.axr.urlvalidationservice.service;

import com.lucasambrosi.axr.urlvalidationservice.entity.Whitelist;
import com.lucasambrosi.axr.urlvalidationservice.input.ValidationInput;
import com.lucasambrosi.axr.urlvalidationservice.output.ValidationOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
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

    private Optional<String> getFirstMatchedRegex(final String clientName, final String url) {
        Function<WhitelistService, Page<Whitelist>> lambdaForSearch = this.getLambdaForSearch(clientName);
        Optional<String> matchedRegex = this.getMatchedRegex(lambdaForSearch, clientName, url);

        if (matchedRegex.isPresent()) {
            return matchedRegex;
        }

        lambdaForSearch = this.getLambdaForSearch(null);
        return this.getMatchedRegex(lambdaForSearch, url);
    }

    private Function<WhitelistService, Page<Whitelist>> getLambdaForSearch(final String clientName) {
        return service -> service.getAllRegex(clientName);
    }

    private Optional<String> getMatchedRegex(Function<WhitelistService, Page<Whitelist>> function, final String url) {
        return this.getMatchedRegex(function, null, url);
    }

    private Optional<String> getMatchedRegex(Function<WhitelistService, Page<Whitelist>> searchFunction,
                                             final String clientName,
                                             final String url) {
        Page<Whitelist> whitelistPage = searchFunction.apply(whitelistService);

        List<String> regexForClient = whitelistPage
                .map(Whitelist::getRegex)
                .getContent();

        Optional<String> matchedRegex = getFirstPositiveMatch(regexForClient, url);

        if (matchedRegex.isPresent() || !whitelistPage.hasNext()) {
            return matchedRegex;
        }

        Function<WhitelistService, Page<Whitelist>> pageableSearchFunction = service -> service.getAllRegexPageable(clientName, whitelistPage.nextPageable());
        return this.getMatchedRegex(pageableSearchFunction, clientName, url);
    }

    private Optional<String> getFirstPositiveMatch(List<String> regexList, final String url) {
        return regexList.stream()
                .distinct()
                .filter(regex -> verifyThatRegexMatches(regex, url))
                .findFirst();
    }

    private boolean verifyThatRegexMatches(final String regex, final String url) {
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
