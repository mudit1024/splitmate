package com.splitmate.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

// Security comes entirely from the AuthReflector starter: SplitMate has no login code of its own
@SpringBootTest(properties = "authreflector.client.client-secret=test-secret")
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRequireSignInForTheApi() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldKeepTheHealthCheckPublic() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk());
    }

    // A signed-in AuthReflector user gets a SplitMate user row, keyed by the AuthReflector user id
    @Test
    void shouldReturnTheLocalUserCreatedFromTheAuthReflectorAccount() throws Exception {
        mockMvc.perform(get("/api/v1/users/me").with(jwt().jwt(token -> token
                        .subject("8f14e45f-ceea-467f-a0e6-1b2c3d4e5f60")
                        .claim("email", "asha@example.com")
                        .claim("name", "Asha")
                        .claim("roles", List.of("USER")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("8f14e45f-ceea-467f-a0e6-1b2c3d4e5f60"))
                .andExpect(jsonPath("$.name").value("Asha"))
                .andExpect(jsonPath("$.email").value("asha@example.com"));
    }
}
