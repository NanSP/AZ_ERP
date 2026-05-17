package com.example.backend.grc.consentimentos;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grc/consentimentos")
public class ConsentimentosController {

    private final ConsentimentosRepository repository;

    public ConsentimentosController(ConsentimentosRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ConsentimentosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ConsentimentosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new ConsentimentosResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveConsentimentos(@RequestBody ConsentimentosRequestDTO data) {
        Consentimentos entity = new Consentimentos();
        entity.setTitular(data.titular());
        entity.setTipoTitular(data.tipoTitular());
        entity.setFinalidade(data.finalidade());
        entity.setDataConsentimento(data.dataConsentimento());
        entity.setDataRevogacao(data.dataRevogacao());
        entity.setIpAddress(data.ipAddress());
        entity.setUserAgent(data.userAgent());

        Consentimentos saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ConsentimentosResponseDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateConsentimentos(@PathVariable Integer id, @RequestBody ConsentimentosRequestDTO data) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    entity.setTitular(data.titular());
                    entity.setTipoTitular(data.tipoTitular());
                    entity.setFinalidade(data.finalidade());
                    entity.setDataConsentimento(data.dataConsentimento());
                    entity.setDataRevogacao(data.dataRevogacao());
                    entity.setIpAddress(data.ipAddress());
                    entity.setUserAgent(data.userAgent());

                    Consentimentos updated = repository.save(entity);
                    return ResponseEntity.ok(new ConsentimentosResponseDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteConsentimentos(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Consentimento deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}