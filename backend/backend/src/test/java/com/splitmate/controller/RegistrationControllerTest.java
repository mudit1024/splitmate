package com.splitmate.controller;

import com.splitmate.dto.request.RegistrationRequest;
import com.splitmate.dto.response.RegistrationResponse;
import com.splitmate.exception.EmailAlreadyRegisteredException;
import com.splitmate.service.RegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrationController.class)
class RegistrationControllerTest {

    private static final String REGISTER_URL = "/api/v1/auth/register";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrationService registrationService;

    @Test
    void shouldReturnCreatedWhenRegistrationSucceeds() throws Exception {

        // Arrange
        when(registrationService.register(any(RegistrationRequest.class)))
                .thenReturn(new RegistrationResponse("Mudit", "mudit@example.com"));

        // Act & Assert
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Mudit", "email": "mudit@example.com", "password": "Password@123"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Mudit"))
                .andExpect(jsonPath("$.email").value("mudit@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void shouldReturnBadRequestWithFieldErrorsWhenRequestIsInvalid() throws Exception {

        // Act & Assert
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "   ", "email": "not-an-email", "password": "weak"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value(REGISTER_URL))
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'name')]").exists())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'email')]").exists())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'password')]").exists());

        verify(registrationService, never()).register(any(RegistrationRequest.class));
    }

    @Test
    void shouldReturnBadRequestInStandardFormatWhenJsonIsMalformed() throws Exception {

        // Act & Assert
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Mudit\","))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Malformed JSON request"))
                .andExpect(jsonPath("$.path").value(REGISTER_URL));

        verify(registrationService, never()).register(any(RegistrationRequest.class));
    }

    @Test
    void shouldReturnConflictWhenEmailIsAlreadyRegistered() throws Exception {

        // Arrange
        when(registrationService.register(any(RegistrationRequest.class)))
                .thenThrow(new EmailAlreadyRegisteredException("mudit@example.com"));

        // Act & Assert
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Mudit", "email": "mudit@example.com", "password": "Password@123"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Email is already registered"));
    }
}
