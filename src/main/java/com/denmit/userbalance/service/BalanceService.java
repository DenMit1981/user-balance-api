package com.denmit.userbalance.service;

import java.math.BigDecimal;

public interface BalanceService {

    void initializeMissingBalancesInCache();

    void put(Long userId, BigDecimal balance);

    void accrueInterest();

    BigDecimal getBalanceForCurrentUser(Long userId);

    void transferMoney(Long fromUserId, Long toUserId, BigDecimal amount);
}
