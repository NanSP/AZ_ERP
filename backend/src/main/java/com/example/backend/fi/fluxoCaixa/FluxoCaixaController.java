package com.example.backend.fi.fluxoCaixa;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fi/fluxoCaixa")
public class FluxoCaixaController {

    private final FluxoCaixaRepository repository;

    public FluxoCaixaController(FluxoCaixaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<FluxoCaixaResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(FluxoCaixaResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new FluxoCaixaResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveFluxoCaixa(@RequestBody FluxoCaixaRequestDTO data) {
        FluxoCaixa entity = new FluxoCaixa(data);
        FluxoCaixa saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(new FluxoCaixaResponseDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFluxoCaixa(@PathVariable Integer id, @RequestBody FluxoCaixaRequestDTO data) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    entity.setDataReferencia(data.dataReferencia());
                    entity.setSaldoInicial(data.saldoInicial());
                    entity.setEntradasPrevistas(data.entradasPrevistas());
                    entity.setSaidasPrevistas(data.saidasPrevistas());
                    entity.setEntradasRealizadas(data.entradasRealizadas());
                    entity.setSaidasRealizadas(data.saidasRealizadas());
                    entity.setSaldoFinalPrevisto(data.saldoFinalPrevisto());
                    entity.setSaldoFinalReal(data.saldoFinalReal());
                    entity.setCreatedAt(data.createdAt());

                    FluxoCaixa updated = repository.save(entity);
                    return ResponseEntity.ok(new FluxoCaixaResponseDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFluxoCaixa(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Fluxo de Caixa deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}