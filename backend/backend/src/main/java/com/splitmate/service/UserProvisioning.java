package com.splitmate.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.authreflector.client.AuthReflectorSignInEvent;
import com.authreflector.client.AuthReflectorUser;
import com.splitmate.entity.User;
import com.splitmate.repository.UserRepository;

/**
 * Just-in-time provisioning: SplitMate's user row is created the first time someone signs in through
 * AuthReflector, and name/email are refreshed on every later sign-in.
 */
@Service
public class UserProvisioning {

    private final UserRepository userRepository;

    public UserProvisioning(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @EventListener
    @Transactional
    public void onSignIn(AuthReflectorSignInEvent event) {
        ensure(event.user());
    }

    /** Also used for bearer-token callers (mobile), who never pass through a browser sign-in. */
    @Transactional
    public User ensure(AuthReflectorUser account) {
        User user = userRepository.findById(account.id())
                .orElseGet(() -> new User(account.id(), account.name(), account.email()));
        user.updateProfile(account.name(), account.email());
        return userRepository.save(user);
    }
}
