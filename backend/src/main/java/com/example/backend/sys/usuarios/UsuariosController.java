package com.example.backend.sys.usuarios;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/usuarios")
public class UsuariosController {

    private final UsuariosRepository repository;
    private final UsuariosService usuariosService;

    public UsuariosController(
            UsuariosRepository repository,
            UsuariosService usuariosService
    ) {
        this.repository = repository;
        this.usuariosService = usuariosService;
    }

    @GetMapping
    public List<UsuariosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(UsuariosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public UsuariosResponseDTO getById(@PathVariable Integer id) {
        Usuarios entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));

        return new UsuariosResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuariosResponseDTO saveUsuarios(@RequestBody UsuariosRequestDTO data) {
        Usuarios saved = usuariosService.criar(data);
        return new UsuariosResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public UsuariosResponseDTO updateUsuarios(@PathVariable Integer id, @RequestBody UsuariosRequestDTO data) {
        Usuarios updated = usuariosService.atualizar(id, data);
        return new UsuariosResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteUsuarios(@PathVariable Integer id) {
        usuariosService.excluir(id);
        return "Usuario deleted";
    }
}