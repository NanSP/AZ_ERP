package com.example.backend.grc.riscos;

import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grc/riscos")
public class RiscosController {

    private final RiscosRepository repository;
    private final UsuariosRepository usuariosRepository;

    public RiscosController(
            RiscosRepository repository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.usuariosRepository = usuariosRepository;
    }

    @GetMapping
    public List<RiscosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(RiscosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new RiscosResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveRiscos(@RequestBody RiscosRequestDTO data) {
        try {
            Usuarios responsavel = data.responsavel() != null
                    ? usuariosRepository.findById(data.responsavel())
                    .orElseThrow(() -> new RuntimeException("Responsavel nao encontrado"))
                    : null;

            Riscos entity = new Riscos();
            entity.setCodigo(data.codigo());
            entity.setTitulo(data.titulo());
            entity.setDescricao(data.descricao());
            entity.setCategoria(data.categoria());
            entity.setProbabilidade(data.probabilidade());
            entity.setImpacto(data.impacto());
            entity.setNivelRisco(data.nivelRisco());
            entity.setResponsavel(responsavel);
            entity.setPlanoMitigacao(data.planoMitigacao());
            entity.setCreatedAt(data.createdAt());

            Riscos saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new RiscosResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRiscos(@PathVariable Integer id, @RequestBody RiscosRequestDTO data) {
        try {
            Riscos entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Risco nao encontrado"));

            Usuarios responsavel = data.responsavel() != null
                    ? usuariosRepository.findById(data.responsavel())
                    .orElseThrow(() -> new RuntimeException("Responsavel nao encontrado"))
                    : null;

            entity.setCodigo(data.codigo());
            entity.setTitulo(data.titulo());
            entity.setDescricao(data.descricao());
            entity.setCategoria(data.categoria());
            entity.setProbabilidade(data.probabilidade());
            entity.setImpacto(data.impacto());
            entity.setNivelRisco(data.nivelRisco());
            entity.setResponsavel(responsavel);
            entity.setPlanoMitigacao(data.planoMitigacao());
            entity.setCreatedAt(data.createdAt());

            Riscos updated = repository.save(entity);
            return ResponseEntity.ok(new RiscosResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRiscos(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Risco deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}