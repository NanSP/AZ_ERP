package com.example.backend.pp.mrp;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pp/mrp")
public class MrpController {

    private final MrpRepository repository;
    private final ProdutosRepository produtosRepository;

    public MrpController(
            MrpRepository repository,
            ProdutosRepository produtosRepository
    ) {
        this.repository = repository;
        this.produtosRepository = produtosRepository;
    }

    @GetMapping
    public List<MrpResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(MrpResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new MrpResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveMrp(@RequestBody MrpRequestDTO data) {
        try {
            Produtos produto = data.produto() != null
                    ? produtosRepository.findById(data.produto())
                    .orElseThrow(() -> new RuntimeException("Produto nao encontrado"))
                    : null;

            Mrp entity = new Mrp();
            entity.setProduto(produto);
            entity.setPeriodo(data.periodo());
            entity.setDemandaPrevista(data.demandaPrevista());
            entity.setEstoqueAtual(data.estoqueAtual());
            entity.setEstoqueSeguranca(data.estoqueSeguranca());
            entity.setNecessidadeCompra(data.necessidadeCompra());
            entity.setNecessidadeProducao(data.necessidadeProducao());
            entity.setDataNecessidade(data.dataNecessidade());
            entity.setCreatedAt(data.createdAt());

            Mrp saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new MrpResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMrp(@PathVariable Integer id, @RequestBody MrpRequestDTO data) {
        try {
            Mrp entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("MRP nao encontrado"));

            Produtos produto = data.produto() != null
                    ? produtosRepository.findById(data.produto())
                    .orElseThrow(() -> new RuntimeException("Produto nao encontrado"))
                    : null;

            entity.setProduto(produto);
            entity.setPeriodo(data.periodo());
            entity.setDemandaPrevista(data.demandaPrevista());
            entity.setEstoqueAtual(data.estoqueAtual());
            entity.setEstoqueSeguranca(data.estoqueSeguranca());
            entity.setNecessidadeCompra(data.necessidadeCompra());
            entity.setNecessidadeProducao(data.necessidadeProducao());
            entity.setDataNecessidade(data.dataNecessidade());
            entity.setCreatedAt(data.createdAt());

            Mrp updated = repository.save(entity);
            return ResponseEntity.ok(new MrpResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMrp(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("MRP deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}