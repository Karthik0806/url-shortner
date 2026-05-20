package com.karthik.urlshortener.admin;

import jakarta.persistence.PrePersist;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    @GetMapping("/health")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminHealth(){
        return "Admin API Working (under development......)";
    }
}
