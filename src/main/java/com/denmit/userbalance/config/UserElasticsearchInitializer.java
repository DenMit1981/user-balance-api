package com.denmit.userbalance.config;

import com.denmit.userbalance.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserElasticsearchInitializer {

    private final UserService userService;

    @PostConstruct
    public void init() {
        userService.reindexAll();
    }
}


