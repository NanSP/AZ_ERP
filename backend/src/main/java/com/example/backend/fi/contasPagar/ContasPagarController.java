package com.example.backend.fi.contasPagar;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/fi/contasPagar")
public class ContasPagarController {

    private final ContasPagarRepository repository;
    private final ContasPagarService contasPagarService;

    public ContasPagarController(
            ContasPagarRepository repository,
            ContasPagarService contasPagarService
    ) {
        this.repository = repository;
        this.contasPagarService = contasPagarService;
    }

    @GetMapping
    public List<ContasPagarResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ContasPagarResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ContasPagarResponseDTO getById(@PathVariable Integer id) {
        ContasPagar entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta a pagar nao encontrada"));

        return new ContasPagarResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContasPagarResponseDTO saveContasPagar(@RequestBody ContasPagarRequestDTO data) {
        ContasPagar saved = contasPagarService.criar(data);
        return new ContasPagarResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public ContasPagarResponseDTO updateContasPagar(@PathVariable Integer id, @RequestBody ContasPagarRequestDTO data) {
        ContasPagar updated = contasPagarService.atualizar(id, data);
        return new ContasPagarResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteContasPagar(@PathVariable Integer id) {
        contasPagarService.excluir(id);
        return "Contas a Pagar deleted";
    }
}
