package com.karthik.urlshortener.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank
    @Size(min = 4, max = 50,message = "Username must be between 4 and 50 characters")
    private String username;
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be empty")
    private String email;
    @NotBlank
    @Size(min = 8,max = 24,message = "Password must be between 8 and 24")
    private String password;
}
