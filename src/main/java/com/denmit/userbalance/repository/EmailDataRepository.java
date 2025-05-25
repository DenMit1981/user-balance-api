package com.denmit.userbalance.repository;

import com.denmit.userbalance.model.EmailData;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailDataRepository extends JpaRepository<EmailData, Long> {

    @Cacheable(value = "emails", key = "#email")
    boolean existsByEmail(String email);
}
