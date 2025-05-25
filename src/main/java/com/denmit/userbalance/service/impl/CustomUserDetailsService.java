package com.denmit.userbalance.service.impl;

import com.denmit.userbalance.model.CustomUserDetails;
import com.denmit.userbalance.model.User;
import com.denmit.userbalance.exception.UserNotFoundException;
import com.denmit.userbalance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private static final String USER_NOT_FOUND = "User with email or phone '%s' not found";

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) {
        User user = getByEmailOrPhone(login);

        return new CustomUserDetails(user, login);
    }

    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        return new CustomUserDetails(user);

    }

    private User getByEmailOrPhone(String login) {
        return userRepository.findByEmail(login)
                .or(() -> userRepository.findByPhone(login))
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, login)));
    }
}
