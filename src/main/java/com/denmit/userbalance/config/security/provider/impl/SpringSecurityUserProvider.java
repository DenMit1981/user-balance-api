package com.denmit.userbalance.config.security.provider.impl;

import com.denmit.userbalance.config.security.provider.UserProvider;
import com.denmit.userbalance.model.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SpringSecurityUserProvider implements UserProvider {

    @Override
    public Long getUserId() {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return customUserDetails.getId();
    }
}
