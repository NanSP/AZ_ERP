package com.example.backend.mm.compraItens;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import com.example.backend.mm.compras.Compras;
import com.example.backend.mm.compras.ComprasRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mm/compraItens")
public class CompraItensController {

    private final CompraItensRepository repository;
    private final ComprasRepository comprasRepository;
    private final ProdutosRepository produtosRepository;

    public CompraItensController(
            CompraItensRepository repository,
            ComprasRepository comprasRepository,
            ProdutosRepository produtosRepository
    ) {
        this.repository = repository;
        this.comprasRepository = comprasRepository;
        this.produtosRepository = produtosRepository;
    }

    @GetMapping
    public List<CompraItensResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(CompraItensResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new CompraItensResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveCompraItens(@RequestBody CompraItensRequestDTO data) {
        try {
            Compras compras = data.compras() != null
                    ? comprasRepository.findById(data.compras())
                    .orElseThrow(() -> new RuntimeException("Compra nao encontrada"))
                    : null;

            Produtos produtos = data.produtos() != null
                    ? produtosRepository.findById(data.produtos())
                    .orElseThrow(() -> new RuntimeException("Produto nao encontrado"))
                    : null;

            CompraItens entity = new CompraItens();
            entity.setCompras(compras);
            entity.setProdutos(produtos);
            entity.setQuantidade(data.quantidade());
            entity.setValorUnitario(data.valorUnitario());
            entity.setValorTotal(data.valorTotal());
            entity.setQuantidadeRecebida(data.quantidadeRecebida());
            entity.setCreatedAt(data.createdAt());

            CompraItens saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new CompraItensResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCompraItens(@PathVariable Integer id, @RequestBody CompraItensRequestDTO data) {
        try {
            CompraItens entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Item de compra nao encontrado"));

            Compras compras = data.compras() != null
                    ? comprasRepository.findById(data.compras())
                    .orElseThrow(() -> new RuntimeException("Compra nao encontrada"))
                    : null;

            Produtos produtos = data.produtos() != null
                    ? produtosRepository.findById(data.produtos())
                    .orElseThrow(() -> new RuntimeException("Produto nao encontrado"))
                    : null;

            entity.setCompras(compras);
            entity.setProdutos(produtos);
            entity.setQuantidade(data.quantidade());
            entity.setValorUnitario(data.valorUnitario());
            entity.setValorTotal(data.valorTotal());
            entity.setQuantidadeRecebida(data.quantidadeRecebida());
            entity.setCreatedAt(data.createdAt());

            CompraItens updated = repository.save(entity);
            return ResponseEntity.ok(new CompraItensResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCompraItens(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Compra Item deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}