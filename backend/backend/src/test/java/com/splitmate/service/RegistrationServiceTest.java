package com.splitmate.service;

import com.splitmate.dto.request.RegistrationRequest;
import com.splitmate.dto.response.RegistrationResponse;
import com.splitmate.entity.User;
import com.splitmate.exception.EmailAlreadyRegisteredException;
import com.splitmate.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegistrationServiceTest {

    private static final String NAME = "Mudit";
    private static final String EMAIL = "mudit@example.com";
    private static final String PASSWORD = "Password@123";

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private RegistrationService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        service = new RegistrationService(userRepository, passwordEncoder);
    }

    @Test
    void shouldRegisterUserWithValidDetails() {

        // Act
        RegistrationResponse response = service.register(new RegistrationRequest(NAME, EMAIL, PASSWORD));

        // Assert
        assertEquals(NAME, response.name());
        assertEquals(EMAIL, response.email());

        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    void shouldStorePasswordAsHash() {

        // Act
        service.register(new RegistrationRequest(NAME, EMAIL, PASSWORD));

        // Assert
        User savedUser = captureSavedUser();

        assertNotEquals(PASSWORD, savedUser.getPasswordHash());
        assertTrue(passwordEncoder.matches(PASSWORD, savedUser.getPasswordHash()));
    }

    @Test
    void shouldRejectDuplicateEmail() {

        // Arrange
        when(userRepository.existsByEmailIgnoreCase(EMAIL)).thenReturn(true);

        RegistrationRequest request = new RegistrationRequest(NAME, EMAIL, PASSWORD);

        // Act & Assert
        assertThrows(
                EmailAlreadyRegisteredException.class,
                () -> service.register(request));

        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    void shouldTrimNameBeforeRegistration() {

        // Act
        RegistrationResponse response = service.register(new RegistrationRequest("  Mudit  ", EMAIL, PASSWORD));

        // Assert
        assertEquals("Mudit", response.name());
    }

    @Test
    void shouldNormalizeEmailBeforeRegistration() {

        // Act
        RegistrationResponse response = service.register(
                new RegistrationRequest(NAME, "  Mudit@Example.COM ", PASSWORD));

        // Assert
        assertEquals(EMAIL, response.email());
        assertEquals(EMAIL, captureSavedUser().getEmail());
        verify(userRepository).existsByEmailIgnoreCase(EMAIL);
    }

    @Test
    void shouldRejectDuplicateEmailWhenConcurrentRegistrationWinsTheRace() {

        // Arrange: the duplicate check passes, but another request inserts the same email first
        when(userRepository.existsByEmailIgnoreCase(EMAIL)).thenReturn(false);
        when(userRepository.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key value violates unique constraint"));

        RegistrationRequest request = new RegistrationRequest(NAME, EMAIL, PASSWORD);

        // Act & Assert
        assertThrows(
                EmailAlreadyRegisteredException.class,
                () -> service.register(request));
    }

    private User captureSavedUser() {
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(userCaptor.capture());
        return userCaptor.getValue();
    }
}
