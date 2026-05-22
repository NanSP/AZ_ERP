package com.example.backend.fi.contasReceber;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fi/contasReceber")
public class ContasReceberController {

    private final ContasReceberRepository repository;
    private final ContasReceberService contasReceberService;

    public ContasReceberController(
            ContasReceberRepository repository,
            ContasReceberService contasReceberService
    ) {
        this.repository = repository;
        this.contasReceberService = contasReceberService;
    }

    @GetMapping
    public List<ContasReceberResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ContasReceberResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ContasReceberResponseDTO getById(@PathVariable Integer id) {
        ContasReceber entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta a receber nao encontrada"));

        return new ContasReceberResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContasReceberResponseDTO saveContasReceber(@RequestBody ContasReceberRequestDTO data) {
        ContasReceber saved = contasReceberService.criar(data);
        return new ContasReceberResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public ContasReceberResponseDTO updateContasReceber(@PathVariable Integer id, @RequestBody ContasReceberRequestDTO data) {
        ContasReceber updated = contasReceberService.atualizar(id, data);
        return new ContasReceberResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteContasReceber(@PathVariable Integer id) {
        contasReceberService.excluir(id);
        return "Contas a Receber deleted";
    }
}