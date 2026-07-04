package com.example.backend.grc.registrosTratamento;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grc/registrosTratamento")
public class RegistrosTratamentoController {

    private final RegistrosTratamentoRepository repository;
    private final RegistrosTratamentoService service;

    public RegistrosTratamentoController(
            RegistrosTratamentoRepository repository,
            RegistrosTratamentoService service
    ) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping
    public List<RegistrosTratamentoResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(RegistrosTratamentoResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public RegistrosTratamentoResponseDTO getById(@PathVariable Integer id) {
        RegistrosTratamento entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro de tratamento nao encontrado"));
        return new RegistrosTratamentoResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrosTratamentoResponseDTO create(@RequestBody RegistrosTratamentoRequestDTO data) {
        return new RegistrosTratamentoResponseDTO(service.criar(data));
    }

    @PutMapping("/{id}")
    public RegistrosTratamentoResponseDTO update(@PathVariable Integer id, @RequestBody RegistrosTratamentoRequestDTO data) {
        return new RegistrosTratamentoResponseDTO(service.atualizar(id, data));
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        service.excluir(id);
        return "Registro de tratamento deleted";
    }
}
