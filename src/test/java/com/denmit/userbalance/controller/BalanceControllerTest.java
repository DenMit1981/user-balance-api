package com.denmit.userbalance.controller;

import com.denmit.userbalance.config.security.provider.UserProvider;
import com.denmit.userbalance.exception.BalanceException;
import com.denmit.userbalance.model.CustomUserDetails;
import com.denmit.userbalance.model.User;
import com.denmit.userbalance.service.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class BalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BalanceService balanceService;

    @MockBean
    private UserProvider userProvider;

    @BeforeEach
    void setupSecurityContext() {
        User user = new User();
        user.setId(1L);
        user.setPassword("testpassword");
        CustomUserDetails customUserDetails = new CustomUserDetails(user, "testuser");

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void testGetBalanceForCurrentUser() throws Exception {
        when(userProvider.getUserId()).thenReturn(1L);
        when(balanceService.getBalanceForCurrentUser(1L)).thenReturn(BigDecimal.valueOf(123.45));

        mockMvc.perform(get("/api/v1/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string("123.45"));
    }

    @Test
    void testTransferMoney() throws Exception {
        Long recipientUserId = 2L;
        BigDecimal amount = new BigDecimal("50.00");

        mockMvc.perform(post("/api/v1/balance/transfer")
                        .param("recipientUserId", recipientUserId.toString())
                        .param("amount", amount.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void testAccrueAllBalances() throws Exception {
        mockMvc.perform(post("/api/v1/balance/accrue"))
                .andExpect(status().isOk());
    }

    @Test
    void testTransferMoneyFailsWhenSameUser() throws Exception {
        String errorMessage = "Cannot transfer to the same user";
        Long userId = 1L;
        Mockito.when(userProvider.getUserId()).thenReturn(userId);

        Mockito.doThrow(new BalanceException(errorMessage))
                .when(balanceService).transferMoney(eq(userId), eq(userId), any());

        mockMvc.perform(post("/api/v1/balance/transfer")
                        .param("recipientUserId", "1")
                        .param("amount", "100.00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.info").value(errorMessage));
    }

    @Test
    void testTransferMoneyFailsWhenAmountNegative() throws Exception {
        String errorMessage = "Transfer amount must be positive";
        Long userId = 1L;
        Long recipientId = 2L;
        Mockito.when(userProvider.getUserId()).thenReturn(userId);

        Mockito.doThrow(new BalanceException(errorMessage))
                .when(balanceService).transferMoney(eq(userId), eq(recipientId), any());

        mockMvc.perform(post("/api/v1/balance/transfer")
                        .param("recipientUserId", "2")
                        .param("amount", "-10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.info").value(errorMessage));
    }

    @Test
    void testTransferMoneyFailsWhenNotEnoughBalance() throws Exception {
        String errorMessage = "Insufficient funds";
        Long userId = 1L;
        Long recipientId = 2L;
        Mockito.when(userProvider.getUserId()).thenReturn(userId);

        Mockito.doThrow(new BalanceException(errorMessage))
                .when(balanceService).transferMoney(eq(userId), eq(recipientId), any());

        mockMvc.perform(post("/api/v1/balance/transfer")
                        .param("recipientUserId", "2")
                        .param("amount", "999999.99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.info").value(errorMessage));
    }

    @Test
    void testTransferFailsWithTooManyDecimals() throws Exception {
        String errorMessage = "Too many decimal places in amount";
        Long userId = 1L;
        Long recipientId = 2L;
        Mockito.when(userProvider.getUserId()).thenReturn(userId);

        Mockito.doThrow(new BalanceException(errorMessage))
                .when(balanceService).transferMoney(eq(userId), eq(recipientId), any());

        mockMvc.perform(post("/api/v1/balance/transfer")
                        .param("recipientUserId", "2")
                        .param("amount", "10.123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.info").value(errorMessage));
    }
}
