package com.example.backend.fiscal.edcRegistros;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fiscal/ecdRegistros")
public class EcdRegistrosController {

    private final EcdRegistrosRepository repository;
    private final EcdRegistrosService ecdRegistrosService;

    public EcdRegistrosController(
            EcdRegistrosRepository repository,
            EcdRegistrosService ecdRegistrosService
    ) {
        this.repository = repository;
        this.ecdRegistrosService = ecdRegistrosService;
    }

    @GetMapping
    public List<EcdRegistrosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(EcdRegistrosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public EcdRegistrosResponseDTO getById(@PathVariable Long id) {
        EcdRegistros entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro ECD nao encontrado"));

        return new EcdRegistrosResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EcdRegistrosResponseDTO saveEcdRegistros(@RequestBody EcdRegistrosRequestDTO data) {
        EcdRegistros saved = ecdRegistrosService.criar(data);
        return new EcdRegistrosResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public EcdRegistrosResponseDTO updateEcdRegistros(@PathVariable Long id, @RequestBody EcdRegistrosRequestDTO data) {
        EcdRegistros updated = ecdRegistrosService.atualizar(id, data);
        return new EcdRegistrosResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteEcdRegistros(@PathVariable Long id) {
        ecdRegistrosService.excluir(id);
        return "ECD registro deleted";
    }
}