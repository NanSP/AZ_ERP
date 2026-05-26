package com.example.backend.sd.pedidos;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sd/pedidos")
public class PedidosController {

    private final PedidosRepository repository;
    private final PedidosService pedidosService;

    public PedidosController(
            PedidosRepository repository,
            PedidosService pedidosService
    ) {
        this.repository = repository;
        this.pedidosService = pedidosService;
    }

    @GetMapping
    public List<PedidosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(PedidosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public PedidosResponseDTO getById(@PathVariable Integer id) {
        Pedidos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido nao encontrado"));

        return new PedidosResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidosResponseDTO savePedidos(@RequestBody PedidosRequestDTO data) {
        Pedidos saved = pedidosService.criar(data);
        return new PedidosResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public PedidosResponseDTO updatePedidos(@PathVariable Integer id, @RequestBody PedidosRequestDTO data) {
        Pedidos updated = pedidosService.atualizar(id, data);
        return new PedidosResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deletePedidos(@PathVariable Integer id) {
        pedidosService.excluir(id);
        return "Pedido deleted";
    }
}