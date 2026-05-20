package com.example.backend.core.parceiros;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/core/parceiros")
public class ParceirosController {

    private final ParceirosRepository repository;

    public ParceirosController(ParceirosRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ParceirosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ParceirosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new ParceirosResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveParceiro(@RequestBody ParceirosRequestDTO data) {
        Parceiros entity = new Parceiros(data);
        Parceiros saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ParceirosResponseDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateParceiro(@PathVariable Integer id, @RequestBody ParceirosRequestDTO data) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    entity.setTipoParceiro(data.tipoParceiro());
                    entity.setCodigo(data.codigo());
                    entity.setNome(data.nome());
                    entity.setNomeFantasia(data.nomeFantasia());
                    entity.setDocumento(data.documento());
                    entity.setTipoPessoa(data.tipoPessoa());
                    entity.setSituacao(data.situacao());
                    entity.setLimiteCredito(data.limiteCredito());
                    entity.setDiasPrazo(data.diasPrazo());
                    entity.setObservacoes(data.observacoes());
                    entity.setCreatedAt(data.createdAt());

                    Parceiros updated = repository.save(entity);
                    return ResponseEntity.ok(new ParceirosResponseDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteParceiro(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Parceiro deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}