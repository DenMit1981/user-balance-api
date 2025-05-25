package com.denmit.userbalance.config;

import com.denmit.userbalance.service.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BalanceInitRunner {

    private final BalanceService balanceService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        balanceService.initializeMissingBalancesInCache();
    }
}
