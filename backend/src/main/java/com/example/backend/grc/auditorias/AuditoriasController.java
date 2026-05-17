package com.example.backend.grc.auditorias;

import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grc/auditorias")
public class AuditoriasController {

    private final AuditoriasRepository repository;
    private final UsuariosRepository usuariosRepository;

    public AuditoriasController(
            AuditoriasRepository repository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.usuariosRepository = usuariosRepository;
    }

    @GetMapping
    public List<AuditoriasResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(AuditoriasResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new AuditoriasResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveAuditorias(@RequestBody AuditoriasRequestDTO data) {
        try {
            Usuarios responsavel = data.responsavel() != null
                    ? usuariosRepository.findById(data.responsavel())
                    .orElseThrow(() -> new RuntimeException("Responsavel nao encontrado"))
                    : null;

            Auditorias entity = new Auditorias();
            entity.setTitulo(data.titulo());
            entity.setTipoAuditoria(data.tipoAuditoria());
            entity.setEscopo(data.escopo());
            entity.setDataInicio(data.dataInicio());
            entity.setDataFim(data.dataFim());
            entity.setResponsavel(responsavel);
            entity.setStatus(data.status());
            entity.setCreatedAt(data.createdAt());

            Auditorias saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new AuditoriasResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAuditorias(@PathVariable Integer id, @RequestBody AuditoriasRequestDTO data) {
        try {
            Auditorias entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Auditoria nao encontrada"));

            Usuarios responsavel = data.responsavel() != null
                    ? usuariosRepository.findById(data.responsavel())
                    .orElseThrow(() -> new RuntimeException("Responsavel nao encontrado"))
                    : null;

            entity.setTitulo(data.titulo());
            entity.setTipoAuditoria(data.tipoAuditoria());
            entity.setEscopo(data.escopo());
            entity.setDataInicio(data.dataInicio());
            entity.setDataFim(data.dataFim());
            entity.setResponsavel(responsavel);
            entity.setStatus(data.status());
            entity.setCreatedAt(data.createdAt());

            Auditorias updated = repository.save(entity);
            return ResponseEntity.ok(new AuditoriasResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAuditorias(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Auditoria deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}