package com.example.backend.mm.compras;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mm/compras")
public class ComprasController {

    private final ComprasRepository repository;
    private final ParceirosRepository parceirosRepository;

    public ComprasController(
            ComprasRepository repository,
            ParceirosRepository parceirosRepository
    ) {
        this.repository = repository;
        this.parceirosRepository = parceirosRepository;
    }

    @GetMapping
    public List<ComprasResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ComprasResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new ComprasResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveCompras(@RequestBody ComprasRequestDTO data) {
        try {
            Parceiros fornecedor = data.fornecedor() != null
                    ? parceirosRepository.findById(data.fornecedor())
                    .orElseThrow(() -> new RuntimeException("Fornecedor nao encontrado"))
                    : null;

            Compras entity = new Compras();
            entity.setFornecedor(fornecedor);
            entity.setDataPedido(data.dataPedido());
            entity.setDataPrevistaEntrega(data.dataPrevistaEntrega());
            entity.setDataEntrega(data.dataEntrega());
            entity.setValorTotal(data.valorTotal());
            entity.setCondicoesPagamento(data.condicoesPagamento());
            entity.setStatus(data.status());
            entity.setObservacoes(data.observacoes());
            entity.setCreatedAt(data.createdAt());

            Compras saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ComprasResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCompras(@PathVariable Integer id, @RequestBody ComprasRequestDTO data) {
        try {
            Compras entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Compra nao encontrada"));

            Parceiros fornecedor = data.fornecedor() != null
                    ? parceirosRepository.findById(data.fornecedor())
                    .orElseThrow(() -> new RuntimeException("Fornecedor nao encontrado"))
                    : null;

            entity.setFornecedor(fornecedor);
            entity.setDataPedido(data.dataPedido());
            entity.setDataPrevistaEntrega(data.dataPrevistaEntrega());
            entity.setDataEntrega(data.dataEntrega());
            entity.setValorTotal(data.valorTotal());
            entity.setCondicoesPagamento(data.condicoesPagamento());
            entity.setStatus(data.status());
            entity.setObservacoes(data.observacoes());
            entity.setCreatedAt(data.createdAt());

            Compras updated = repository.save(entity);
            return ResponseEntity.ok(new ComprasResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCompras(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Compra deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}