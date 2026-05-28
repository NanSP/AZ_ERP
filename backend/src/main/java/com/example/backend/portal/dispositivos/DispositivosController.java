package com.example.backend.portal.dispositivos;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portal/dispositivos")
public class DispositivosController {

    private final DispositivosRepository repository;
    private final DispositivosService dispositivosService;

    public DispositivosController(
            DispositivosRepository repository,
            DispositivosService dispositivosService
    ) {
        this.repository = repository;
        this.dispositivosService = dispositivosService;
    }

    @GetMapping
    public List<DispositivosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(DispositivosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public DispositivosResponseDTO getById(@PathVariable Integer id) {
        Dispositivos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Dispositivo nao encontrado"));

        return new DispositivosResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DispositivosResponseDTO saveDispositivos(@RequestBody DispositivosRequestDTO data) {
        Dispositivos saved = dispositivosService.criar(data);
        return new DispositivosResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public DispositivosResponseDTO updateDispositivos(@PathVariable Integer id, @RequestBody DispositivosRequestDTO data) {
        Dispositivos updated = dispositivosService.atualizar(id, data);
        return new DispositivosResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteDispositivos(@PathVariable Integer id) {
        dispositivosService.excluir(id);
        return "Dispositivo deleted";
    }
}