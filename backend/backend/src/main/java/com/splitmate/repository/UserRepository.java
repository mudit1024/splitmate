package com.splitmate.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.splitmate.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {
}
