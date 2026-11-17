package com.splitmate.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * SplitMate's own record of a person (for groups, expenses, balances).
 * The account itself (password, Google sign-in, ...) lives in AuthReflector; the id here IS the
 * AuthReflector user id (token "sub"), and name/email are refreshed on every sign-in.
 */
// "user" is a reserved word in PostgreSQL
@Entity
@Table(name = "users")
public class User {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    protected User() {
        // Required by JPA
    }

    public User(UUID id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public void updateProfile(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
