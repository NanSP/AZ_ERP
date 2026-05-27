package com.example.backend.mm.inventarios;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mm/inventarios")
public class InventariosController {

    private final InventariosRepository repository;
    private final InventariosService inventariosService;

    public InventariosController(
            InventariosRepository repository,
            InventariosService inventariosService
    ) {
        this.repository = repository;
        this.inventariosService = inventariosService;
    }

    @GetMapping
    public List<InventariosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(InventariosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public InventariosResponseDTO getById(@PathVariable Integer id) {
        Inventarios entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Inventario nao encontrado"));

        return new InventariosResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventariosResponseDTO saveInventarios(@RequestBody InventariosRequestDTO data) {
        Inventarios saved = inventariosService.criar(data);
        return new InventariosResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public InventariosResponseDTO updateInventarios(@PathVariable Integer id, @RequestBody InventariosRequestDTO data) {
        Inventarios updated = inventariosService.atualizar(id, data);
        return new InventariosResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteInventarios(@PathVariable Integer id) {
        inventariosService.excluir(id);
        return "Inventario deleted";
    }
}