package com.example.backend.master.platform.systemUsers;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemUsersRepository extends JpaRepository<SystemUsers, Long> {
    Optional<SystemUsers> findByLogin(String login);
}
