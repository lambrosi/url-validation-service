package com.lucasambrosi.axr.urlvalidationservice.service;

import com.lucasambrosi.axr.urlvalidationservice.entity.ClientWhitelist;
import com.lucasambrosi.axr.urlvalidationservice.entity.GlobalWhitelist;
import com.lucasambrosi.axr.urlvalidationservice.input.RegexInput;
import com.lucasambrosi.axr.urlvalidationservice.repository.ClientWhitelistRepository;
import com.lucasambrosi.axr.urlvalidationservice.repository.GlobalWhitelistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WhitelistService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WhitelistService.class);
    private static final int DEFAULT_PAGE_NUMBER = 0;
    private static final int DEFAULT_PAGE_SIZE = 5;
    private GlobalWhitelistRepository globalWhitelistRepository;
    private ClientWhitelistRepository clientWhitelistRepository;

    public WhitelistService(GlobalWhitelistRepository globalWhitelistRepository,
                            ClientWhitelistRepository clientWhitelistRepository) {
        this.globalWhitelistRepository = globalWhitelistRepository;
        this.clientWhitelistRepository = clientWhitelistRepository;
    }

    public void insertRegularExpression(final RegexInput input) {
        this.validateInputRegex(input.getRegex());

        if (StringUtils.isEmpty(input.getClient())) {
            this.insertInGlobalWhitelist(input);
            return;
        }
        this.insertInClientWhitelist(input);
    }

    private void validateInputRegex(String regex) {
        LOGGER.info("Validating input regular expression.");
        if (StringUtils.isEmpty(regex)) {
            throw new IllegalArgumentException("Regular expression must not be null.");
        }
    }

    private void insertInGlobalWhitelist(final RegexInput input) {
        LOGGER.info("Inserting regex '{}' in global whitelist.", input.getRegex());
        GlobalWhitelist globalWhitelist = new GlobalWhitelist(input.getRegex());
        globalWhitelistRepository.save(globalWhitelist);
    }

    private void insertInClientWhitelist(final RegexInput input) {
        LOGGER.info("Inserting regex '{}' to client '{}' in client whitelist.",
                input.getRegex(), input.getClient());

        ClientWhitelist clientWhitelist = new ClientWhitelist();
        clientWhitelist.setRegex(input.getRegex());
        clientWhitelist.setClient(input.getClient());
        clientWhitelistRepository.save(clientWhitelist);
    }

    public List<String> getRegexForClient(String clientName) {
        LOGGER.info("Retrieving all regex for the client '{}'.", clientName);
        return clientWhitelistRepository.findByClientAndActiveTrue(clientName)
                .stream()
                .map(ClientWhitelist::getRegex)
                .distinct()
                .collect(Collectors.toList());
    }

    public Page<GlobalWhitelist> getAllRegexFromGlobalwhitelistPageable() {
        LOGGER.info("Retrieving regex from globalwhitelist pageable, page {}, size {}.",
                DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE);
        return globalWhitelistRepository.findByActiveTrue(PageRequest.of(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE));
    }

    public Page<GlobalWhitelist> getAllRegexFromGlobalwhitelistPageable(Pageable pageable) {
        LOGGER.info("Retrieving regex from globalwhitelist, page {}, size {}.",
                pageable.getPageNumber(), pageable.getPageSize());
        return globalWhitelistRepository.findByActiveTrue(pageable);
    }
}
