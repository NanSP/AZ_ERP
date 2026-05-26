package com.example.backend.sd.pedidoItens;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sd/pedidoItens")
public class PedidoItensController {

    private final PedidoItensRepository repository;
    private final PedidoItensService pedidoItensService;

    public PedidoItensController(
            PedidoItensRepository repository,
            PedidoItensService pedidoItensService
    ) {
        this.repository = repository;
        this.pedidoItensService = pedidoItensService;
    }

    @GetMapping
    public List<PedidoItensResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(PedidoItensResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public PedidoItensResponseDTO getById(@PathVariable Integer id) {
        PedidoItens entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item do pedido nao encontrado"));

        return new PedidoItensResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoItensResponseDTO savePedidoItens(@RequestBody PedidoItensRequestDTO data) {
        PedidoItens saved = pedidoItensService.criar(data);
        return new PedidoItensResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public PedidoItensResponseDTO updatePedidoItens(@PathVariable Integer id, @RequestBody PedidoItensRequestDTO data) {
        PedidoItens updated = pedidoItensService.atualizar(id, data);
        return new PedidoItensResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deletePedidoItens(@PathVariable Integer id) {
        pedidoItensService.excluir(id);
        return "Pedido Item deleted";
    }
}