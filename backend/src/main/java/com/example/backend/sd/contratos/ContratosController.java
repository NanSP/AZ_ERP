package com.example.backend.sd.contratos;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sd/contratos")
public class ContratosController {

    private final ContratosRepository repository;
    private final ContratosService contratosService;

    public ContratosController(
            ContratosRepository repository,
            ContratosService contratosService
    ) {
        this.repository = repository;
        this.contratosService = contratosService;
    }

    @GetMapping
    public List<ContratosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ContratosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ContratosResponseDTO getById(@PathVariable Integer id) {
        Contratos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Contrato nao encontrado"));

        return new ContratosResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContratosResponseDTO saveContratos(@RequestBody ContratosRequestDTO data) {
        Contratos saved = contratosService.criar(data);
        return new ContratosResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public ContratosResponseDTO updateContratos(@PathVariable Integer id, @RequestBody ContratosRequestDTO data) {
        Contratos updated = contratosService.atualizar(id, data);
        return new ContratosResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteContratos(@PathVariable Integer id) {
        contratosService.excluir(id);
        return "Contrato deleted";
    }
}