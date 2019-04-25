package com.lucasambrosi.axr.urlvalidationservice.repository;

import com.lucasambrosi.axr.urlvalidationservice.entity.ClientWhitelist;
import com.lucasambrosi.axr.urlvalidationservice.entity.Whitelist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientWhitelistRepository extends JpaRepository<ClientWhitelist, Long> {

    Page<Whitelist> findByClientAndActiveTrue(String client, Pageable pageable);
}
