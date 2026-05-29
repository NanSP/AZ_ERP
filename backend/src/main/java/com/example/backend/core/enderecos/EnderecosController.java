package com.example.backend.core.enderecos;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/core/enderecos")
public class EnderecosController {

    private final EnderecosRepository repository;
    private final EnderecosService enderecosService;

    public EnderecosController(
            EnderecosRepository repository,
            EnderecosService enderecosService
    ) {
        this.repository = repository;
        this.enderecosService = enderecosService;
    }

    @GetMapping
    public List<EnderecosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(EnderecosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public EnderecosResponseDTO getById(@PathVariable Integer id) {
        Enderecos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Endereco nao encontrado"));

        return new EnderecosResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EnderecosResponseDTO saveEndereco(@RequestBody EnderecosRequestDTO data) {
        Enderecos saved = enderecosService.criar(data);
        return new EnderecosResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public EnderecosResponseDTO updateEndereco(@PathVariable Integer id, @RequestBody EnderecosRequestDTO data) {
        Enderecos updated = enderecosService.atualizar(id, data);
        return new EnderecosResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteEndereco(@PathVariable Integer id) {
        enderecosService.excluir(id);
        return "Endereco deleted";
    }
}