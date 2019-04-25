package com.lucasambrosi.axr.urlvalidationservice.service;

import com.lucasambrosi.axr.urlvalidationservice.entity.ClientWhitelist;
import com.lucasambrosi.axr.urlvalidationservice.entity.GlobalWhitelist;
import com.lucasambrosi.axr.urlvalidationservice.entity.Whitelist;
import com.lucasambrosi.axr.urlvalidationservice.input.RegexInput;
import com.lucasambrosi.axr.urlvalidationservice.repository.ClientWhitelistRepository;
import com.lucasambrosi.axr.urlvalidationservice.repository.GlobalWhitelistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class WhitelistService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WhitelistService.class);
    private static final int INITIAL_PAGE_NUMBER = 0;
    private GlobalWhitelistRepository globalWhitelistRepository;
    private ClientWhitelistRepository clientWhitelistRepository;

    @Value("${application.query.pagination.pageSize}")
    private int defaultPageSize;

    public WhitelistService(GlobalWhitelistRepository globalWhitelistRepository,
                            ClientWhitelistRepository clientWhitelistRepository) {
        this.globalWhitelistRepository = globalWhitelistRepository;
        this.clientWhitelistRepository = clientWhitelistRepository;
    }

    public Long insertRegularExpression(final RegexInput input) {
        this.validateInputRegex(input.getRegex());

        if (StringUtils.isEmpty(input.getClient())) {
            return this.insertInGlobalWhitelist(input);
        }
        return this.insertInClientWhitelist(input);
    }

    private void validateInputRegex(final String regex) {
        LOGGER.info("Validating input regular expression {}.", regex);
        if (StringUtils.isEmpty(regex)) {
            throw new IllegalArgumentException("Regular expression must not be null.");
        }
    }

    private Long insertInGlobalWhitelist(final RegexInput input) {
        LOGGER.info("Inserting regex '{}' in global whitelist.", input.getRegex());
        GlobalWhitelist globalWhitelist = new GlobalWhitelist(input.getRegex());
        return globalWhitelistRepository.save(globalWhitelist).getId();
    }

    private Long insertInClientWhitelist(final RegexInput input) {
        LOGGER.info("Inserting regex '{}' to client '{}' in client whitelist.",
                input.getRegex(), input.getClient());

        ClientWhitelist clientWhitelist = new ClientWhitelist();
        clientWhitelist.setRegex(input.getRegex());
        clientWhitelist.setClient(input.getClient());
        return clientWhitelistRepository.save(clientWhitelist).getId();
    }

    public Page<Whitelist> getAllRegex(final String clientName) {
        final PageRequest pageRequest = PageRequest.of(INITIAL_PAGE_NUMBER, defaultPageSize);

        if (StringUtils.isEmpty(clientName)) {
            return getAllGlobalRegexPageable(pageRequest);
        }
        return getAllRegexPageable(clientName, pageRequest);
    }

    public Page<Whitelist> getAllRegexPageable(final String clientName, final Pageable pageable) {
        if (StringUtils.isEmpty(clientName)) {
            return getAllGlobalRegexPageable(pageable);
        }
        LOGGER.info("Retrieving regex for the client '{}' pageable. Page {}, size {}.",
                clientName, pageable.getPageNumber(), pageable.getPageSize());
        return clientWhitelistRepository.findByClientAndActiveTrue(clientName, pageable);
    }

    private Page<Whitelist> getAllGlobalRegexPageable(final Pageable pageable) {
        LOGGER.info("Retrieving regex from Global Whitelist, page {}, size {}.",
                pageable.getPageNumber(), pageable.getPageSize());
        return globalWhitelistRepository.findByActiveTrue(pageable);
    }
}
