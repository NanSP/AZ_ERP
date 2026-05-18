package com.example.backend.sys.usuarios;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/usuarios")
public class UsuariosController {

    private final UsuariosRepository repository;

    public UsuariosController(UsuariosRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<UsuariosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(UsuariosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new UsuariosResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveUsuarios(@RequestBody UsuariosRequestDTO data) {
        Usuarios entity = new Usuarios(data);
        Usuarios saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UsuariosResponseDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuarios(@PathVariable Integer id, @RequestBody UsuariosRequestDTO data) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    entity.setNome(data.nome());
                    entity.setEmail(data.email());
                    entity.setLogin(data.login());
                    entity.setSenhaHash(data.senhaHash());
                    entity.setDocumento(data.documento());
                    entity.setTipoUsuario(data.tipoUsuario());
                    entity.setStatus(data.status());
                    entity.setUltimoAcesso(data.ultimoAcesso());
                    entity.setExpiracaoSenha(data.expiracaoSenha());
                    entity.setTentativasLogin(data.tentativasLogin());
                    entity.setCreatedAt(data.createdAt());

                    Usuarios updated = repository.save(entity);
                    return ResponseEntity.ok(new UsuariosResponseDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuarios(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Usuario deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}