package com.example.backend.sd.faturas;

import com.example.backend.sd.pedidos.Pedidos;
import com.example.backend.sd.pedidos.PedidosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sd/faturas")
public class FaturasController {

    private final FaturasRepository repository;
    private final PedidosRepository pedidosRepository;

    public FaturasController(
            FaturasRepository repository,
            PedidosRepository pedidosRepository
    ) {
        this.repository = repository;
        this.pedidosRepository = pedidosRepository;
    }

    @GetMapping
    public List<FaturasResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(FaturasResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new FaturasResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveFaturas(@RequestBody FaturasRequestDTO data) {
        try {
            Pedidos pedido = data.pedido() != null
                    ? pedidosRepository.findById(data.pedido())
                    .orElseThrow(() -> new RuntimeException("Pedido nao encontrado"))
                    : null;

            Faturas entity = new Faturas();
            entity.setPedido(pedido);
            entity.setNumeroFatura(data.numeroFatura());
            entity.setDataEmissao(data.dataEmissao());
            entity.setValorTotal(data.valorTotal());
            entity.setDataVencimento(data.dataVencimento());
            entity.setStatus(data.status());
            entity.setCreatedAt(data.createdAt());

            Faturas saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new FaturasResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFaturas(@PathVariable Integer id, @RequestBody FaturasRequestDTO data) {
        try {
            Faturas entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Fatura nao encontrada"));

            Pedidos pedido = data.pedido() != null
                    ? pedidosRepository.findById(data.pedido())
                    .orElseThrow(() -> new RuntimeException("Pedido nao encontrado"))
                    : null;

            entity.setPedido(pedido);
            entity.setNumeroFatura(data.numeroFatura());
            entity.setDataEmissao(data.dataEmissao());
            entity.setValorTotal(data.valorTotal());
            entity.setDataVencimento(data.dataVencimento());
            entity.setStatus(data.status());
            entity.setCreatedAt(data.createdAt());

            Faturas updated = repository.save(entity);
            return ResponseEntity.ok(new FaturasResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFaturas(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Fatura deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}