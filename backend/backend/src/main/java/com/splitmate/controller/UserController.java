package com.splitmate.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authreflector.client.AuthReflectorUser;
import com.splitmate.entity.User;
import com.splitmate.service.UserProvisioning;

@RestController
public class UserController {

    public record UserResponse(UUID id, String name, String email) {
    }

    private final UserProvisioning provisioning;

    public UserController(UserProvisioning provisioning) {
        this.provisioning = provisioning;
    }

    // The signed-in user as SplitMate knows them (created on first use)
    @GetMapping("/api/v1/users/me")
    public UserResponse me(AuthReflectorUser account) {
        User user = provisioning.ensure(account);
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}
