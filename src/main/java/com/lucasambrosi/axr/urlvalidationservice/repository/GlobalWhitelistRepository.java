package com.lucasambrosi.axr.urlvalidationservice.repository;

import com.lucasambrosi.axr.urlvalidationservice.entity.GlobalWhitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GlobalWhitelistRepository extends JpaRepository<GlobalWhitelist, Long> {

    List<GlobalWhitelist> findByActiveTrue();
}
