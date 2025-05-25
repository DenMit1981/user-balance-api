package com.denmit.userbalance.service;

import com.denmit.userbalance.exception.AccessDeniedException;
import com.denmit.userbalance.exception.BalanceException;
import com.denmit.userbalance.model.Account;
import com.denmit.userbalance.model.User;
import com.denmit.userbalance.repository.AccountRepository;
import com.denmit.userbalance.repository.UserRepository;
import com.denmit.userbalance.service.impl.BalanceServiceImpl;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private BalanceServiceImpl balanceService;

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void initializeMissingBalancesInCache_shouldSetValueIfNotExists() {
        Account account = new Account();
        User user = new User();
        user.setId(1L);
        account.setUser(user);
        account.setBalance(BigDecimal.valueOf(100));
        Mockito.when(accountRepository.findAll()).thenReturn(List.of(account));
        Mockito.when(redisTemplate.hasKey("initial_balance:1")).thenReturn(false);

        balanceService.initializeMissingBalancesInCache();

        Mockito.verify(valueOperations).set("initial_balance:1", BigDecimal.valueOf(100));
    }

    @Test
    void put_shouldStoreValidBalanceInCache() {
        balanceService.put(1L, BigDecimal.valueOf(123.45));
        Mockito.verify(valueOperations).set("initial_balance:1", BigDecimal.valueOf(123.45));
    }

    @Test
    void put_shouldThrowForInvalidBalance() {
        assertThrows(BalanceException.class, () -> balanceService.put(1L, BigDecimal.ZERO));
        assertThrows(BalanceException.class, () -> balanceService.put(1L, new BigDecimal("-100")));
    }

    @Test
    void put_shouldStoreInRedisForValidBalance() {
        BigDecimal validBalance = new BigDecimal("100.00");
        balanceService.put(1L, validBalance);
        Mockito.verify(valueOperations).set("initial_balance:1", validBalance);
    }

    @Test
    void get_shouldReturnBalanceFromCache() {
        Mockito.when(valueOperations.get("initial_balance:1")).thenReturn(BigDecimal.valueOf(100));
        Optional<BigDecimal> result = balanceService.get(1L);
        assertTrue(result.isPresent());
        assertEquals(BigDecimal.valueOf(100), result.get());
    }

    @Test
    void get_shouldReturnEmptyIfNotFound() {
        Mockito.when(valueOperations.get("initial_balance:1")).thenReturn(null);
        assertTrue(balanceService.get(1L).isEmpty());
    }

    @Test
    void getBalanceForCurrentUser_shouldReturnBalance() {
        User user = new User();
        user.setId(1L);
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(200));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(accountRepository.findByUserId(1L)).thenReturn(Optional.of(account));

        BigDecimal result = balanceService.getBalanceForCurrentUser(1L);
        assertEquals(BigDecimal.valueOf(200), result);
    }

    @Test
    void getBalanceForCurrentUser_shouldThrowIfNotOwner() {
        User user = new User();
        user.setId(2L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(AccessDeniedException.class, () -> balanceService.getBalanceForCurrentUser(1L));
    }

    @Test
    void transferMoney_shouldTransferSuccessfully() {
        Long fromUserId = 1L;
        Long toUserId = 2L;
        BigDecimal amount = BigDecimal.valueOf(100);

        User fromUser = new User();
        fromUser.setId(fromUserId);
        User toUser = new User();
        toUser.setId(toUserId);

        Account fromAccount = new Account();
        fromAccount.setId(1L);
        fromAccount.setUser(fromUser);
        fromAccount.setBalance(BigDecimal.valueOf(200));

        Account toAccount = new Account();
        toAccount.setId(2L);
        toAccount.setUser(toUser);
        toAccount.setBalance(BigDecimal.valueOf(50));

        Mockito.when(accountRepository.findByUserIdForUpdate(fromUserId)).thenReturn(Optional.of(fromAccount));
        Mockito.when(accountRepository.findByUserIdForUpdate(toUserId)).thenReturn(Optional.of(toAccount));

        balanceService.transferMoney(fromUserId, toUserId, amount);

        assertEquals(BigDecimal.valueOf(100), fromAccount.getBalance());
        assertEquals(BigDecimal.valueOf(150), toAccount.getBalance());
    }

    @Test
    void transferMoney_shouldThrowIfSameUser() {
        assertThrows(BalanceException.class, () -> balanceService.transferMoney(1L, 1L, BigDecimal.TEN));
    }

    @Test
    void transferMoney_shouldThrowIfNotEnoughFunds() {
        Account from = new Account();
        from.setBalance(BigDecimal.valueOf(10));
        Account to = new Account();
        to.setBalance(BigDecimal.ZERO);

        Mockito.when(accountRepository.findByUserIdForUpdate(1L)).thenReturn(Optional.of(from));
        Mockito.when(accountRepository.findByUserIdForUpdate(2L)).thenReturn(Optional.of(to));

        assertThrows(BalanceException.class, () -> balanceService.transferMoney(1L, 2L, BigDecimal.valueOf(20)));
    }

    @Test
    void accrueInterest_shouldSkipIfAllReachedMax() throws IllegalAccessException {
        FieldUtils.writeField(balanceService, "allUsersReachedMax", true, true);
        balanceService.accrueInterest();
        Mockito.verify(accountRepository, Mockito.never()).findAll();
    }
}