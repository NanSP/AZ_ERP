package com.example.backend.fi.movimentacoesBancarias;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fi/movimentacoesBancarias")
public class MovimentacoesBancariasController {

    private final MovimentacoesBancariasRepository repository;

    public MovimentacoesBancariasController(MovimentacoesBancariasRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<MovimentacoesBancariasResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(MovimentacoesBancariasResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new MovimentacoesBancariasResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveMovimentacoesBancarias(@RequestBody MovimentacoesBancariasRequestDTO data) {
        MovimentacoesBancarias entity = new MovimentacoesBancarias(data);
        MovimentacoesBancarias saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MovimentacoesBancariasResponseDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMovimentacoesBancarias(@PathVariable Integer id, @RequestBody MovimentacoesBancariasRequestDTO data) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    entity.setContaBancariaId(data.contaBancariaId());
                    entity.setTipoMovimento(data.tipoMovimento());
                    entity.setValor(data.valor());
                    entity.setDataMovimento(data.dataMovimento());
                    entity.setHistorico(data.historico());
                    entity.setDocumentoVinculado(data.documentoVinculado());
                    entity.setConciliado(data.conciliado());
                    entity.setDataConciliacao(data.dataConciliacao());
                    entity.setCreatedAt(data.createdAt());

                    MovimentacoesBancarias updated = repository.save(entity);
                    return ResponseEntity.ok(new MovimentacoesBancariasResponseDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovimentacoesBancarias(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Movimentacao Bancaria deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}