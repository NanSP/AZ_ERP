package com.example.backend.portal.notificacoes;

import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portal/notificacoes")
public class NotificacoesController {

    private final NotificacoesRepository repository;
    private final UsuariosRepository usuariosRepository;

    public NotificacoesController(
            NotificacoesRepository repository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.usuariosRepository = usuariosRepository;
    }

    @GetMapping
    public List<NotificacoesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(NotificacoesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new NotificacoesResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveNotificacoes(@RequestBody NotificacoesRequestDTO data) {
        try {
            Usuarios usuario = data.usuario() != null
                    ? usuariosRepository.findById(data.usuario())
                    .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"))
                    : null;

            Notificacoes entity = new Notificacoes();
            entity.setUsuario(usuario);
            entity.setTitulo(data.titulo());
            entity.setMensagem(data.mensagem());
            entity.setTipo(data.tipo());
            entity.setLida(data.lida());
            entity.setDataLeitura(data.dataLeitura());
            entity.setCreatedAt(data.createdAt());

            Notificacoes saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new NotificacoesResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNotificacoes(@PathVariable Integer id, @RequestBody NotificacoesRequestDTO data) {
        try {
            Notificacoes entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Notificacao nao encontrada"));

            Usuarios usuario = data.usuario() != null
                    ? usuariosRepository.findById(data.usuario())
                    .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"))
                    : null;

            entity.setUsuario(usuario);
            entity.setTitulo(data.titulo());
            entity.setMensagem(data.mensagem());
            entity.setTipo(data.tipo());
            entity.setLida(data.lida());
            entity.setDataLeitura(data.dataLeitura());
            entity.setCreatedAt(data.createdAt());

            Notificacoes updated = repository.save(entity);
            return ResponseEntity.ok(new NotificacoesResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotificacoes(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Notificacao deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}