package com.denmit.userbalance.repository;

import com.denmit.userbalance.model.PhoneData;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneDataRepository extends JpaRepository<PhoneData, Long> {

    @Cacheable(value = "phones", key = "#phone")
    boolean existsByPhone(String phone);
}
