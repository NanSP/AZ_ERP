package com.example.backend.sd.clientes;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sd/clientes")
public class ClientesController {

    private final ClientesRepository repository;
    private final ClientesService clientesService;

    public ClientesController(
            ClientesRepository repository,
            ClientesService clientesService
    ) {
        this.repository = repository;
        this.clientesService = clientesService;
    }

    @GetMapping
    public List<ClientesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ClientesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ClientesResponseDTO getById(@PathVariable Integer id) {
        Clientes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente nao encontrado"));

        return new ClientesResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientesResponseDTO saveClientes(@RequestBody ClientesRequestDTO data) {
        Clientes saved = clientesService.criar(data);
        return new ClientesResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public ClientesResponseDTO updateClientes(@PathVariable Integer id, @RequestBody ClientesRequestDTO data) {
        Clientes updated = clientesService.atualizar(id, data);
        return new ClientesResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteClientes(@PathVariable Integer id) {
        clientesService.excluir(id);
        return "Cliente deleted";
    }
}