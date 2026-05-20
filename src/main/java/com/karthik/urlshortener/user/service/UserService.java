package com.karthik.urlshortener.user.service;

import com.karthik.urlshortener.common.exception.UserNotFoundException;
import com.karthik.urlshortener.security.entity.CustomUserDetails;
import com.karthik.urlshortener.user.entity.User;
import com.karthik.urlshortener.user.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    public CustomUserDetails
    getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return (CustomUserDetails)
                authentication.getPrincipal();
    }

    public User getCurrentUserEntity() {

        CustomUserDetails currentUser =
                getAuthenticatedUser();

        return userRepo.findById(
                currentUser.getId()
        ).orElseThrow(() ->
                new UserNotFoundException(
                        "User not found"
                )
        );
    }
}