package com.lucasambrosi.axr.urlvalidationservice.repository;

import com.lucasambrosi.axr.urlvalidationservice.entity.GlobalWhitelist;
import com.lucasambrosi.axr.urlvalidationservice.entity.Whitelist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalWhitelistRepository extends JpaRepository<GlobalWhitelist, Long> {

    Page<Whitelist> findByActiveTrue(Pageable pageable);
}
