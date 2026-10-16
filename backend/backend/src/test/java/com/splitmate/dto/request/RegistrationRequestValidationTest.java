package com.splitmate.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrationRequestValidationTest {

    private final Validator validator;

    RegistrationRequestValidationTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

        validator = factory.getValidator();
    }

    @Test
    void shouldRejectNullName() {

        // Arrange
        RegistrationRequest request = new RegistrationRequest(
                null,
                "mudit@example.com",
                "Password@123");

        // Act
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
    }

    @Test
    void shouldRejectMissingRequiredFields() {

        // Arrange
        RegistrationRequest request = new RegistrationRequest(
                null,
                null,
                null);

        // Act
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(3, violations.size());
    }

    @Test
    void shouldRejectInvalidEmailFormat() {

        // Arrange
        RegistrationRequest request = new RegistrationRequest(
                "Mudit",
                "not-an-email",
                "Password@123");

        // Act
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
    }

    @Test
    void shouldRejectBlankPassword() {

        // Arrange
        RegistrationRequest request = new RegistrationRequest(
                "Mudit",
                "mudit@example.com",
                "   ");

        // Act
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);

        // Assert
        // assertEquals(1, violations.size());
        assertTrue(
                violations.stream()
                        .anyMatch(violation -> violation.getPropertyPath()
                                .toString()
                                .equals("password")));
    }

    @Test
    void shouldRejectPasswordShorterThanEightCharacters() {

        // Arrange
        RegistrationRequest request = new RegistrationRequest(
                "Mudit",
                "mudit@example.com",
                "Pa@1234");

        // Act
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);

        // Assert
        assertEquals(1, violations.size());
    }

    @Test
    void shouldRejectPasswordWithoutLowercaseCharacter() {

        RegistrationRequest request = new RegistrationRequest(
                "Mudit",
                "mudit@example.com",
                "PASSWORD@123");

        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);

        assertTrue(
                violations.stream()
                        .anyMatch(violation -> violation.getPropertyPath()
                                .toString()
                                .equals("password")));
    }

    @Test
    void shouldRejectPasswordWithoutUppercaseCharacter() {

        RegistrationRequest request = new RegistrationRequest(
                "Mudit",
                "mudit@example.com",
                "password@123");

        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);

        assertTrue(
                violations.stream()
                        .anyMatch(violation -> violation.getPropertyPath()
                                .toString()
                                .equals("password")));
    }

    @Test
    void shouldRejectPasswordWithoutNumericCharacter() {

        RegistrationRequest request = new RegistrationRequest(
                "Mudit",
                "mudit@example.com",
                "Password@");

        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);

        assertTrue(
                violations.stream()
                        .anyMatch(violation -> violation.getPropertyPath()
                                .toString()
                                .equals("password")));
    }

    @Test
    void shouldRejectPasswordWithoutSpecialCharacter() {

        RegistrationRequest request = new RegistrationRequest(
                "Mudit",
                "mudit@example.com",
                "Password123");

        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);

        assertTrue(
                violations.stream()
                        .anyMatch(violation -> violation.getPropertyPath()
                                .toString()
                                .equals("password")));
    }

    @Test
    void shouldAcceptPasswordMeetingAllRequirements() {

        RegistrationRequest request = new RegistrationRequest(
                "Mudit",
                "mudit@example.com",
                "Password@123");

        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldRejectPasswordLongerThanSixtyFourCharacters() {

        // BCrypt only uses the first 72 bytes, so long passwords are capped
        String password = "Password@1" + "a".repeat(55);

        RegistrationRequest request = new RegistrationRequest(
                "Mudit",
                "mudit@example.com",
                password);

        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);

        assertEquals(65, password.length());
        assertEquals(1, violations.size());
    }

    @Test
    void shouldAcceptPasswordOfExactlySixtyFourCharacters() {

        String password = "Password@1" + "a".repeat(54);

        RegistrationRequest request = new RegistrationRequest(
                "Mudit",
                "mudit@example.com",
                password);

        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(request);

        assertEquals(64, password.length());
        assertTrue(violations.isEmpty());
    }
}