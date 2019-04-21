package com.lucasambrosi.axr.urlvalidationservice.service;

import com.lucasambrosi.axr.urlvalidationservice.entity.ClientWhitelist;
import com.lucasambrosi.axr.urlvalidationservice.entity.GlobalWhitelist;
import com.lucasambrosi.axr.urlvalidationservice.input.RegexInput;
import com.lucasambrosi.axr.urlvalidationservice.repository.ClientWhitelistRepository;
import com.lucasambrosi.axr.urlvalidationservice.repository.GlobalWhitelistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WhitelistService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WhitelistService.class);
    private GlobalWhitelistRepository globalWhitelistRepository;
    private ClientWhitelistRepository clientWhitelistRepository;

    public WhitelistService(GlobalWhitelistRepository globalWhitelistRepository,
                            ClientWhitelistRepository clientWhitelistRepository) {
        this.globalWhitelistRepository = globalWhitelistRepository;
        this.clientWhitelistRepository = clientWhitelistRepository;
    }

    public void insertRegularExpression(final RegexInput input) {
        this.validateInputeRegex(input.getRegex());

        if (StringUtils.isEmpty(input.getClient())) {
            this.insertInGlobalWhitelist(input);
            return;
        }
        this.insertInClientWhitelist(input);
    }

    private void validateInputeRegex(String regex) {
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

    public List<String> getAllRegexForAClient(String clientName) {
        //TODO review how to get full list in a better way
        List<String> globalWhitelists = globalWhitelistRepository.findByActiveTrue().stream()
                .map(GlobalWhitelist::getRegex)
                .collect(Collectors.toList());

        List<String> clientWhitelists = clientWhitelistRepository.findByClientAndActiveTrue(clientName).stream()
                .map(ClientWhitelist::getRegex)
                .collect(Collectors.toList());

        List<String> outputList = new ArrayList<>(globalWhitelists);
        outputList.addAll(clientWhitelists);

        return outputList;

    }
}
