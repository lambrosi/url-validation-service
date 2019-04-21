package com.lucasambrosi.axr.urlvalidationservice.repository;

import com.lucasambrosi.axr.urlvalidationservice.entity.ClientWhitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientWhitelistRepository extends JpaRepository<ClientWhitelist, Long> {

    List<ClientWhitelist> findByClientAndActiveTrue(String client);
}
