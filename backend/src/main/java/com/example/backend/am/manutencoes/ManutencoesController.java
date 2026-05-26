package com.example.backend.am.manutencoes;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/am/manutencoes")
public class ManutencoesController {

    private final ManutencoesRepository repository;
    private final ManutencoesService manutencoesService;

    public ManutencoesController(
            ManutencoesRepository repository,
            ManutencoesService manutencoesService
    ) {
        this.repository = repository;
        this.manutencoesService = manutencoesService;
    }

    @GetMapping
    public List<ManutencoesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ManutencoesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ManutencoesResponseDTO getById(@PathVariable Integer id) {
        Manutencoes entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Manutencao nao encontrada"));

        return new ManutencoesResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ManutencoesResponseDTO saveManutencoes(@RequestBody ManutencoesRequestDTO data) {
        Manutencoes saved = manutencoesService.criar(data);
        return new ManutencoesResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public ManutencoesResponseDTO updateManutencoes(@PathVariable Integer id, @RequestBody ManutencoesRequestDTO data) {
        Manutencoes updated = manutencoesService.atualizar(id, data);
        return new ManutencoesResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteManutencoes(@PathVariable Integer id) {
        manutencoesService.excluir(id);
        return "Manutencao deleted";
    }
}