package com.kashika.finance_manager.config;

import com.kashika.finance_manager.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.Collections;

import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.kashika.finance_manager.model.User applicationUser = 
                userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(applicationUser.getUsername()) 
                .password(applicationUser.getPassword()) // already hashed
                .authorities(Collections.emptyList()) // no roles
                .build();
    }
}
