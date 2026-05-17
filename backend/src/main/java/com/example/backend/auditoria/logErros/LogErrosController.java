package com.example.backend.auditoria.logErros;

import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auditoria/logErros")
public class LogErrosController {

    private final LogErrosRepository repository;
    private final UsuariosRepository usuariosRepository;

    public LogErrosController(
            LogErrosRepository repository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.usuariosRepository = usuariosRepository;
    }

    @GetMapping
    public List<LogErrosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(LogErrosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new LogErrosResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveLogErros(@RequestBody LogErrosRequestDTO data) {
        try {
            Usuarios usuario = data.usuario() != null
                    ? usuariosRepository.findById(data.usuario())
                    .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"))
                    : null;

            LogErros entity = new LogErros();
            entity.setErroCodigo(data.erroCodigo());
            entity.setErroMensagem(data.erroMensagem());
            entity.setModulo(data.modulo());
            entity.setUsuario(usuario);
            entity.setUrl(data.url());
            entity.setParametros(data.parametros());
            entity.setIpAddress(data.ipAddress());
            entity.setCreatedAt(data.createdAt());

            LogErros saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new LogErrosResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLogErros(@PathVariable Long id, @RequestBody LogErrosRequestDTO data) {
        try {
            LogErros entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Log de erro nao encontrado"));

            Usuarios usuario = data.usuario() != null
                    ? usuariosRepository.findById(data.usuario())
                    .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"))
                    : null;

            entity.setErroCodigo(data.erroCodigo());
            entity.setErroMensagem(data.erroMensagem());
            entity.setModulo(data.modulo());
            entity.setUsuario(usuario);
            entity.setUrl(data.url());
            entity.setParametros(data.parametros());
            entity.setIpAddress(data.ipAddress());
            entity.setCreatedAt(data.createdAt());

            LogErros updated = repository.save(entity);
            return ResponseEntity.ok(new LogErrosResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLogErros(@PathVariable Long id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Log de erro deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}