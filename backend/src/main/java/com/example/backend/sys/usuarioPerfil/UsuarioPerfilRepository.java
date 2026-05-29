package com.example.backend.sys.usuarioPerfil;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioPerfilRepository extends JpaRepository<UsuarioPerfil, Integer> {
    boolean existsByUsuarioId(Integer usuarioId);
    boolean existsByPerfilId(Integer perfilId);
    boolean existsByUsuarioIdAndPerfilId(Integer usuarioId, Integer perfilId);
    boolean existsByUsuarioIdAndPerfilIdAndIdNot(Integer usuarioId, Integer perfilId, Integer id);
    boolean existsByUsuarioIdAndIdNot(Integer usuarioId, Integer id);
}
