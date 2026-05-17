package com.example.backend.grc.controles;

import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grc/controles")
public class ControlesController {

    private final ControlesRepository repository;
    private final UsuariosRepository usuariosRepository;

    public ControlesController(
            ControlesRepository repository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.usuariosRepository = usuariosRepository;
    }

    @GetMapping
    public List<ControlesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ControlesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new ControlesResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveControles(@RequestBody ControlesRequestDTO data) {
        try {
            Usuarios responsavel = data.responsavel() != null
                    ? usuariosRepository.findById(data.responsavel())
                    .orElseThrow(() -> new RuntimeException("Responsavel nao encontrado"))
                    : null;

            Controles entity = new Controles();
            entity.setCodigo(data.codigo());
            entity.setDescricao(data.descricao());
            entity.setTipoControle(data.tipoControle());
            entity.setFrequencia(data.frequencia());
            entity.setResponsavel(responsavel);
            entity.setEfetivo(data.efetivo());
            entity.setCreatedAt(data.createdAt());

            Controles saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ControlesResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateControles(@PathVariable Integer id, @RequestBody ControlesRequestDTO data) {
        try {
            Controles entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Controle nao encontrado"));

            Usuarios responsavel = data.responsavel() != null
                    ? usuariosRepository.findById(data.responsavel())
                    .orElseThrow(() -> new RuntimeException("Responsavel nao encontrado"))
                    : null;

            entity.setCodigo(data.codigo());
            entity.setDescricao(data.descricao());
            entity.setTipoControle(data.tipoControle());
            entity.setFrequencia(data.frequencia());
            entity.setResponsavel(responsavel);
            entity.setEfetivo(data.efetivo());
            entity.setCreatedAt(data.createdAt());

            Controles updated = repository.save(entity);
            return ResponseEntity.ok(new ControlesResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteControles(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Controle deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}