package com.denmit.userbalance.service.impl;

import com.denmit.userbalance.exception.*;
import com.denmit.userbalance.model.Account;
import com.denmit.userbalance.model.User;
import com.denmit.userbalance.repository.AccountRepository;
import com.denmit.userbalance.repository.UserRepository;
import com.denmit.userbalance.service.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceServiceImpl implements BalanceService {

    private static final String KEY_PREFIX = "initial_balance:";
    private static final String USER_NOT_FOUND = "User with %s %s not found";

    private volatile boolean allUsersReachedMax = false;

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Set<Long> usersReachedMax = ConcurrentHashMap.newKeySet();

    @Override
    @Transactional(readOnly = true)
    public void initializeMissingBalancesInCache() {
        List<Account> accounts = accountRepository.findAll();

        for (Account account : accounts) {
            Long userId = account.getUser().getId();
            String key = KEY_PREFIX + userId;

            Boolean exists = redisTemplate.hasKey(key);
            if (Boolean.FALSE.equals(exists)) {
                redisTemplate.opsForValue().set(key, account.getBalance());
                log.info("Initialized Redis balance for user {}: {}", userId, account.getBalance());
            }
        }
    }

    @Override
    public void put(Long userId, BigDecimal balance) {
        if (balance == null || balance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BalanceException("Initial balance must be greater than zero");
        }

        redisTemplate.opsForValue().set(KEY_PREFIX + userId, balance);
    }

    @Override
    @Transactional
    @Scheduled(fixedRate = 30000)
    public void accrueInterest() {
        log.info("Running accrueInterest job...");

        if (allUsersReachedMax) {
            log.info("All users have reached the maximum balance. Interest accrual stopped.");
            return;
        }

        List<Account> accounts = accountRepository.findAll();
        int totalUsers = accounts.size();
        int usersAtMaxNow = 0;

        for (Account account : accounts) {
            Long userId = account.getUser().getId();
            BigDecimal currentBalance = account.getBalance();
            Optional<BigDecimal> initialOpt = get(account.getId());

            if (initialOpt.isEmpty()) {
                log.warn("Initial balance not found in cache for user {}", userId);
                continue;
            }

            BigDecimal maxAllowed = initialOpt.get().multiply(BigDecimal.valueOf(2.07)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal newBalance = currentBalance.multiply(BigDecimal.valueOf(1.10)).setScale(2, RoundingMode.HALF_UP);

            if (newBalance.compareTo(maxAllowed) > 0) {
                newBalance = maxAllowed;
            }

            if (newBalance.compareTo(currentBalance) > 0) {
                account.setBalance(newBalance);
                log.info("Updated balance for user {} to {}", userId, newBalance);
            }

            if (newBalance.compareTo(maxAllowed) == 0) {
                if (usersReachedMax.add(userId)) {
                    log.info("User {} has reached the maximum allowed balance: {}", userId, maxAllowed);
                }
                usersAtMaxNow++;
            }
        }

        if (usersReachedMax.size() == totalUsers && !allUsersReachedMax) {
            allUsersReachedMax = true;
            log.info("All users have reached the maximum balance. Stopping interest accrual.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getBalanceForCurrentUser(Long userId) {
        User currentUser = findById(userId);
        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }
        return accountRepository.findByUserId(userId)
                .map(Account::getBalance)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for user ID: " + userId));
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void transferMoney(Long fromUserId, Long toUserId, BigDecimal amount) {
        if (fromUserId.equals(toUserId)) {
            throw new BalanceException("Cannot transfer to the same user");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BalanceException("Transfer amount must be positive");
        }

        if (amount.scale() > 2) {
            throw new BalanceException("Too many decimal places in amount");
        }

        Account fromAccount = accountRepository.findByUserIdForUpdate(fromUserId)
                .orElseThrow(() -> new AccountNotFoundException("Sender account not found"));

        Account toAccount = accountRepository.findByUserIdForUpdate(toUserId)
                .orElseThrow(() -> new AccountNotFoundException("Receiver account not found"));

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            log.warn("Transfer failed: insufficient funds. User {}, balance {}, attempted transfer {}",
                    fromUserId, fromAccount.getBalance(), amount);
            throw new BalanceException("Insufficient funds");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        log.info("Transferred {} from user {} to user {}", amount, fromUserId, toUserId);
    }

    public Optional<BigDecimal> get(Long userId) {
        Object value = redisTemplate.opsForValue().get(KEY_PREFIX + userId);
        if (value instanceof BigDecimal) {
            return Optional.of((BigDecimal) value);
        }
        return Optional.empty();
    }

    private User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, "id", userId)));
    }
}
