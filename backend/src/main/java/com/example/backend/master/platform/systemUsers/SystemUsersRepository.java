package com.example.backend.master.platform.systemUsers;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemUsersRepository extends JpaRepository<SystemUsers, Long> {
    Optional<SystemUsers> findByLogin(String login);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    boolean existsByLoginIgnoreCase(String login);
    boolean existsByLoginIgnoreCaseAndIdNot(String login, Long id);
}
