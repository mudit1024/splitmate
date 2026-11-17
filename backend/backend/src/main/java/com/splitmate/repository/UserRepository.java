package com.splitmate.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.splitmate.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    // Atomic "create if missing": when several first-sign-in requests race, PostgreSQL lets exactly one insert win
    // and the others do nothing (instead of failing with a duplicate key, as a Java "find, then save" would).
    // No conflict target on purpose: the racing rows clash on BOTH unique indexes (id and email).
    @Modifying
    @Query(value = "insert into users (id, name, email) values (:id, :name, :email) on conflict do nothing",
            nativeQuery = true)
    int insertIfAbsent(@Param("id") UUID id, @Param("name") String name, @Param("email") String email);
}
