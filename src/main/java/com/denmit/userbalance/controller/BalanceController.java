package com.denmit.userbalance.controller;

import com.denmit.userbalance.config.security.provider.UserProvider;
import com.denmit.userbalance.service.BalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/balance")
@Tag(name = "Balance Controller")
@Slf4j
public class BalanceController {

    private final BalanceService balanceService;
    private final UserProvider userProvider;

    @GetMapping
    @Operation(summary = "Get balance for current user")
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal getBalanceForCurrentUser() {
        return balanceService.getBalanceForCurrentUser(userProvider.getUserId());
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer money to another user")
    @ResponseStatus(HttpStatus.OK)
    public void transferMoney(@RequestParam Long recipientUserId,
                              @RequestParam BigDecimal amount) {
        Long senderUserId = userProvider.getUserId();
        balanceService.transferMoney(senderUserId, recipientUserId, amount);
    }

    @PostMapping("/accrue")
    @Operation(summary = "Manually trigger balance accrual")
    @ResponseStatus(HttpStatus.OK)
    public void accrueAllBalances() {
        balanceService.accrueInterest();
    }
}
