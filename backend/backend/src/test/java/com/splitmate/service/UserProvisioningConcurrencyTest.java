package com.splitmate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.authreflector.client.AuthReflectorUser;
import com.splitmate.repository.UserRepository;

// A first sign-in often fires several requests at once (sign-in event, /users/me, a second tab).
// They all see "no row yet" and all try to insert it: only one may win, and none may fail.
// Not @Transactional on purpose: each thread must commit for real, like separate HTTP requests.
@SpringBootTest(properties = "authreflector.client.client-secret=test-secret")
class UserProvisioningConcurrencyTest {

    private static final int THREADS = 8;
    private static final int ROUNDS = 10;

    @Autowired
    private UserProvisioning provisioning;

    @Autowired
    private UserRepository userRepository;

    private final List<UUID> createdIds = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        userRepository.deleteAllById(createdIds);
    }

    @Test
    void shouldCreateExactlyOneRowWhenTheFirstSignInRequestsArriveTogether() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        try {
            for (int round = 0; round < ROUNDS; round++) {
                UUID id = UUID.randomUUID();
                createdIds.add(id);
                AuthReflectorUser account = new AuthReflectorUser(id, id + "@example.com", "Asha", List.of("USER"));

                CountDownLatch start = new CountDownLatch(1);
                List<Future<?>> requests = new ArrayList<>();
                for (int i = 0; i < THREADS; i++) {
                    requests.add(pool.submit(() -> {
                        start.await();
                        return provisioning.ensure(account);
                    }));
                }
                start.countDown();

                for (Future<?> request : requests) {
                    request.get(); // rethrows a failed request (e.g. duplicate key) and fails the test
                }
                assertTrue(userRepository.existsById(id));
            }
            assertEquals(ROUNDS, userRepository.findAllById(createdIds).size());
        } finally {
            pool.shutdownNow();
        }
    }
}
