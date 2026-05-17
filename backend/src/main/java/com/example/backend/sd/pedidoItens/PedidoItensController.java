package com.example.backend.sd.pedidoItens;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import com.example.backend.sd.pedidos.Pedidos;
import com.example.backend.sd.pedidos.PedidosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sd/pedidoItens")
public class PedidoItensController {

    private final PedidoItensRepository repository;
    private final PedidosRepository pedidosRepository;
    private final ProdutosRepository produtosRepository;

    public PedidoItensController(
            PedidoItensRepository repository,
            PedidosRepository pedidosRepository,
            ProdutosRepository produtosRepository
    ) {
        this.repository = repository;
        this.pedidosRepository = pedidosRepository;
        this.produtosRepository = produtosRepository;
    }

    @GetMapping
    public List<PedidoItensResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(PedidoItensResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new PedidoItensResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> savePedidoItens(@RequestBody PedidoItensRequestDTO data) {
        try {
            Pedidos pedido = data.pedido() != null
                    ? pedidosRepository.findById(data.pedido())
                    .orElseThrow(() -> new RuntimeException("Pedido nao encontrado"))
                    : null;

            Produtos produto = data.produto() != null
                    ? produtosRepository.findById(data.produto())
                    .orElseThrow(() -> new RuntimeException("Produto nao encontrado"))
                    : null;

            PedidoItens entity = new PedidoItens();
            entity.setPedido(pedido);
            entity.setProduto(produto);
            entity.setQuantidade(data.quantidade());
            entity.setValorUnitario(data.valorUnitario());
            entity.setValorTotal(data.valorTotal());
            entity.setDesconto(data.desconto());
            entity.setCreatedAt(data.createdAt());

            PedidoItens saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new PedidoItensResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePedidoItens(@PathVariable Integer id, @RequestBody PedidoItensRequestDTO data) {
        try {
            PedidoItens entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Item do pedido nao encontrado"));

            Pedidos pedido = data.pedido() != null
                    ? pedidosRepository.findById(data.pedido())
                    .orElseThrow(() -> new RuntimeException("Pedido nao encontrado"))
                    : null;

            Produtos produto = data.produto() != null
                    ? produtosRepository.findById(data.produto())
                    .orElseThrow(() -> new RuntimeException("Produto nao encontrado"))
                    : null;

            entity.setPedido(pedido);
            entity.setProduto(produto);
            entity.setQuantidade(data.quantidade());
            entity.setValorUnitario(data.valorUnitario());
            entity.setValorTotal(data.valorTotal());
            entity.setDesconto(data.desconto());
            entity.setCreatedAt(data.createdAt());

            PedidoItens updated = repository.save(entity);
            return ResponseEntity.ok(new PedidoItensResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePedidoItens(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Pedido Item deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}