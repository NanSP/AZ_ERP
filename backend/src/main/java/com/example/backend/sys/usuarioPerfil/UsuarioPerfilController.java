package com.example.backend.sys.usuarioPerfil;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/usuarioPerfil")
public class UsuarioPerfilController {

    private final UsuarioPerfilRepository repository;
    private final UsuarioPerfilService usuarioPerfilService;

    public UsuarioPerfilController(
            UsuarioPerfilRepository repository,
            UsuarioPerfilService usuarioPerfilService
    ) {
        this.repository = repository;
        this.usuarioPerfilService = usuarioPerfilService;
    }

    @GetMapping
    public List<UsuarioPerfilResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(UsuarioPerfilResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public UsuarioPerfilResponseDTO getById(@PathVariable Integer id) {
        UsuarioPerfil entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vinculo usuario-perfil nao encontrado"));

        return new UsuarioPerfilResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioPerfilResponseDTO saveUsuarioPerfil(@RequestBody UsuarioPerfilRequestDTO data) {
        UsuarioPerfil saved = usuarioPerfilService.criar(data);
        return new UsuarioPerfilResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public UsuarioPerfilResponseDTO updateUsuarioPerfil(@PathVariable Integer id, @RequestBody UsuarioPerfilRequestDTO data) {
        UsuarioPerfil updated = usuarioPerfilService.atualizar(id, data);
        return new UsuarioPerfilResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteUsuarioPerfil(@PathVariable Integer id) {
        usuarioPerfilService.excluir(id);
        return "UsuarioPerfil deleted";
    }
}