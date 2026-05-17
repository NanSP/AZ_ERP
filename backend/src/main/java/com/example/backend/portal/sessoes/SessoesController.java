package com.example.backend.portal.sessoes;

import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portal/sessoes")
public class SessoesController {

    private final SessoesRepository repository;
    private final UsuariosRepository usuariosRepository;

    public SessoesController(
            SessoesRepository repository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.usuariosRepository = usuariosRepository;
    }

    @GetMapping
    public List<SessoesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(SessoesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new SessoesResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveSessoes(@RequestBody SessoesRequestDTO data) {
        try {
            Usuarios usuario = data.usuario() != null
                    ? usuariosRepository.findById(data.usuario())
                    .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"))
                    : null;

            Sessoes entity = new Sessoes();
            entity.setUsuario(usuario);
            entity.setTokenSessao(data.tokenSessao());
            entity.setIpAddress(data.ipAddress());
            entity.setUserAgent(data.userAgent());
            entity.setDataLogin(data.dataLogin());
            entity.setDataLogout(data.dataLogout());
            entity.setExpiracao(data.expiracao());

            Sessoes saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new SessoesResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSessoes(@PathVariable Integer id, @RequestBody SessoesRequestDTO data) {
        try {
            Sessoes entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Sessao nao encontrada"));

            Usuarios usuario = data.usuario() != null
                    ? usuariosRepository.findById(data.usuario())
                    .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"))
                    : null;

            entity.setUsuario(usuario);
            entity.setTokenSessao(data.tokenSessao());
            entity.setIpAddress(data.ipAddress());
            entity.setUserAgent(data.userAgent());
            entity.setDataLogin(data.dataLogin());
            entity.setDataLogout(data.dataLogout());
            entity.setExpiracao(data.expiracao());

            Sessoes updated = repository.save(entity);
            return ResponseEntity.ok(new SessoesResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSessoes(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Sessao deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}