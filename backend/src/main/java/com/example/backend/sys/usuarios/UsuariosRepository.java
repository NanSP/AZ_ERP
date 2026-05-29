package com.example.backend.sys.usuarios;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuariosRepository extends JpaRepository<Usuarios, Integer> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Integer id);
    boolean existsByLogin(String login);
    boolean existsByLoginAndIdNot(String login, Integer id);
}
