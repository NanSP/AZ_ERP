package com.example.backend.bi.relatorios;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bi/relatorios")
public class RelatoriosController {

    private final RelatoriosRepository repository;

    public RelatoriosController(RelatoriosRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<RelatoriosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(RelatoriosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new RelatoriosResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveRelatorios(@RequestBody RelatoriosRequestDTO data) {
        Relatorios entity = new Relatorios(data);
        Relatorios saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(new RelatoriosResponseDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRelatorios(@PathVariable Integer id, @RequestBody RelatoriosRequestDTO data) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    entity.setNome(data.nome());
                    entity.setDescricao(data.descricao());
                    entity.setTipoRelatorio(data.tipoRelatorio());
                    entity.setQuerySql(data.querySql());
                    entity.setParametros(data.parametros());
                    entity.setCreatedAt(data.createdAt());

                    Relatorios updated = repository.save(entity);
                    return ResponseEntity.ok(new RelatoriosResponseDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRelatorios(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Relatorio deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}