package com.splitmate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.authreflector.client.AuthReflectorSignInEvent;
import com.authreflector.client.AuthReflectorUser;
import com.splitmate.entity.User;
import com.splitmate.repository.UserRepository;

// SplitMate keeps its own user rows (for groups, expenses, ...), keyed by the AuthReflector user id
class UserProvisioningTest {

    private static final UUID ID = UUID.fromString("8f14e45f-ceea-467f-a0e6-1b2c3d4e5f60");

    private UserRepository userRepository;
    private UserProvisioning provisioning;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        provisioning = new UserProvisioning(userRepository);
    }

    @Test
    void shouldCreateTheLocalUserOnFirstSignIn() {
        when(userRepository.findById(ID)).thenReturn(Optional.empty());

        provisioning.onSignIn(new AuthReflectorSignInEvent(user("Asha", "asha@example.com")));

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertEquals(ID, saved.getValue().getId());
        assertEquals("Asha", saved.getValue().getName());
        assertEquals("asha@example.com", saved.getValue().getEmail());
    }

    // Name or email changed in AuthReflector: SplitMate's copy follows on the next sign-in
    @Test
    void shouldRefreshNameAndEmailOnLaterSignIns() {
        User existing = new User(ID, "Old name", "old@example.com");
        when(userRepository.findById(ID)).thenReturn(Optional.of(existing));

        User result = provisioning.ensure(user("Asha", "asha@example.com"));

        assertEquals("Asha", result.getName());
        assertEquals("asha@example.com", result.getEmail());
    }

    private static AuthReflectorUser user(String name, String email) {
        return new AuthReflectorUser(ID, email, name, List.of("USER"));
    }
}
