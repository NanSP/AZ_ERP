package com.example.backend.sd.pedidos;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sd/pedidos")
public class PedidosController {

    private final PedidosRepository repository;
    private final ParceirosRepository parceirosRepository;

    public PedidosController(
            PedidosRepository repository,
            ParceirosRepository parceirosRepository
    ) {
        this.repository = repository;
        this.parceirosRepository = parceirosRepository;
    }

    @GetMapping
    public List<PedidosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(PedidosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new PedidosResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> savePedidos(@RequestBody PedidosRequestDTO data) {
        try {
            Parceiros cliente = data.cliente() != null
                    ? parceirosRepository.findById(data.cliente())
                    .orElseThrow(() -> new RuntimeException("Cliente nao encontrado"))
                    : null;

            Pedidos entity = new Pedidos();
            entity.setCliente(cliente);
            entity.setNumeroPedido(data.numeroPedido());
            entity.setDataPedido(data.dataPedido());
            entity.setDataEntrega(data.dataEntrega());
            entity.setValorTotal(data.valorTotal());
            entity.setDescontoTotal(data.descontoTotal());
            entity.setCondicoesPagamento(data.condicoesPagamento());
            entity.setStatus(data.status());
            entity.setObservacoes(data.observacoes());
            entity.setCreatedAt(data.createdAt());

            Pedidos saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new PedidosResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePedidos(@PathVariable Integer id, @RequestBody PedidosRequestDTO data) {
        try {
            Pedidos entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pedido nao encontrado"));

            Parceiros cliente = data.cliente() != null
                    ? parceirosRepository.findById(data.cliente())
                    .orElseThrow(() -> new RuntimeException("Cliente nao encontrado"))
                    : null;

            entity.setCliente(cliente);
            entity.setNumeroPedido(data.numeroPedido());
            entity.setDataPedido(data.dataPedido());
            entity.setDataEntrega(data.dataEntrega());
            entity.setValorTotal(data.valorTotal());
            entity.setDescontoTotal(data.descontoTotal());
            entity.setCondicoesPagamento(data.condicoesPagamento());
            entity.setStatus(data.status());
            entity.setObservacoes(data.observacoes());
            entity.setCreatedAt(data.createdAt());

            Pedidos updated = repository.save(entity);
            return ResponseEntity.ok(new PedidosResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePedidos(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Pedido deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}