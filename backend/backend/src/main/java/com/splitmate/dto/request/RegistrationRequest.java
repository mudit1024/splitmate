package com.splitmate.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
        @NotBlank(message = "Name must not be blank")
        String name,

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password must not be blank")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
        // Lookaheads: at least one lowercase, uppercase, digit and special (non-alphanumeric, non-space) character
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s]).*$",
                message = "Password must contain at least one lowercase letter, one uppercase letter, one number and one special character")
        String password
) {
}