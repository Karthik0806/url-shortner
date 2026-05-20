package com.karthik.urlshortener.security.service;

import com.karthik.urlshortener.common.exception.UserNotFoundException;
import com.karthik.urlshortener.security.entity.CustomUserDetails;
import com.karthik.urlshortener.user.entity.User;
import com.karthik.urlshortener.user.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo
                .findByEmail(email)
                .orElseThrow(()->new UserNotFoundException("User not found"));
        return new CustomUserDetails(user);

    }

}
