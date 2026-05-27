package com.example.backend.rh.controleDePonto;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/rh/controleDePonto")
public class ControleDePontoController {

    private final ControleDePontoRepository repository;
    private final ControleDePontoService controleDePontoService;

    public ControleDePontoController(
            ControleDePontoRepository repository,
            ControleDePontoService controleDePontoService
    ) {
        this.repository = repository;
        this.controleDePontoService = controleDePontoService;
    }

    @GetMapping
    public List<ControleDePontoResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ControleDePontoResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ControleDePontoResponseDTO getById(@PathVariable Integer id) {
        ControleDePonto entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Controle de ponto nao encontrado"));

        return new ControleDePontoResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ControleDePontoResponseDTO saveControleDePonto(@RequestBody ControleDePontoRequestDTO data) {
        ControleDePonto saved = controleDePontoService.criar(data);
        return new ControleDePontoResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public ControleDePontoResponseDTO updateControleDePonto(@PathVariable Integer id, @RequestBody ControleDePontoRequestDTO data) {
        ControleDePonto updated = controleDePontoService.atualizar(id, data);
        return new ControleDePontoResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteControleDePonto(@PathVariable Integer id) {
        controleDePontoService.excluir(id);
        return "Ponto deleted";
    }
}