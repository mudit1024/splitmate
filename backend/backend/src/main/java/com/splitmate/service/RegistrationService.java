package com.splitmate.service;

import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.splitmate.dto.request.RegistrationRequest;
import com.splitmate.dto.response.RegistrationResponse;
import com.splitmate.entity.User;
import com.splitmate.exception.EmailAlreadyRegisteredException;
import com.splitmate.repository.UserRepository;

@Service
public class RegistrationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public RegistrationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegistrationResponse register(RegistrationRequest request) {

        // Input is already validated by @Valid on the controller
        String normalizedName = request.name().trim();
        String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new EmailAlreadyRegisteredException(normalizedEmail);
        }

        String passwordHash = passwordEncoder.encode(request.password());
        User user = new User(normalizedName, normalizedEmail, passwordHash);

        try {
            // Flush now so a unique-constraint violation surfaces inside this try block
            userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException exception) {
            // Lost a race: a concurrent request registered the same email after our check
            throw new EmailAlreadyRegisteredException(normalizedEmail);
        }

        return new RegistrationResponse(normalizedName, normalizedEmail);
    }
}
