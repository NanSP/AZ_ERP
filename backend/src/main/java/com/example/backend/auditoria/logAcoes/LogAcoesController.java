package com.example.backend.auditoria.logAcoes;

import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auditoria/logAcoes")
public class LogAcoesController {

    private final LogAcoesRepository repository;
    private final UsuariosRepository usuariosRepository;

    public LogAcoesController(
            LogAcoesRepository repository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.usuariosRepository = usuariosRepository;
    }

    @GetMapping
    public List<LogAcoesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(LogAcoesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new LogAcoesResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveLogAcoes(@RequestBody LogAcoesRequestDTO data) {
        try {
            Usuarios usuario = data.usuario() != null
                    ? usuariosRepository.findById(data.usuario())
                    .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"))
                    : null;

            LogAcoes entity = new LogAcoes();
            entity.setUsuario(usuario);
            entity.setModulo(data.modulo());
            entity.setAcao(data.acao());
            entity.setTabela(data.tabela());
            entity.setRegistroId(data.registroId());
            entity.setDadosAntigos(data.dadosAntigos());
            entity.setDadosNovos(data.dadosNovos());
            entity.setIpAddress(data.ipAddress());
            entity.setUserAgent(data.userAgent());
            entity.setCreatedAt(data.createdAt());

            LogAcoes saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new LogAcoesResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLogAcoes(@PathVariable Long id, @RequestBody LogAcoesRequestDTO data) {
        try {
            LogAcoes entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Log de acao nao encontrado"));

            Usuarios usuario = data.usuario() != null
                    ? usuariosRepository.findById(data.usuario())
                    .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"))
                    : null;

            entity.setUsuario(usuario);
            entity.setModulo(data.modulo());
            entity.setAcao(data.acao());
            entity.setTabela(data.tabela());
            entity.setRegistroId(data.registroId());
            entity.setDadosAntigos(data.dadosAntigos());
            entity.setDadosNovos(data.dadosNovos());
            entity.setIpAddress(data.ipAddress());
            entity.setUserAgent(data.userAgent());
            entity.setCreatedAt(data.createdAt());

            LogAcoes updated = repository.save(entity);
            return ResponseEntity.ok(new LogAcoesResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLogAcoes(@PathVariable Long id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Log de acao deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}