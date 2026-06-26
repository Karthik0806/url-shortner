package com.karthik.urlshortener.config;

import com.karthik.urlshortener.user.entity.User;
import com.karthik.urlshortener.user.enums.Role;

import com.karthik.urlshortener.user.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder implements CommandLineRunner {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {

        boolean adminExists = userRepo.findByEmail(adminEmail).isPresent();
        if (adminExists) {
            log.info("Admin account already exists");
            return;
        }

        User admin = User.builder()
                .username("admin")
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .build();

        userRepo.save(admin);
        log.info("Admin account created successfully");
    }
}