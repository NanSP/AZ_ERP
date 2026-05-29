package com.example.backend.bi.relatorios;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bi/relatorios")
public class RelatoriosController {

    private final RelatoriosRepository repository;
    private final RelatoriosService relatoriosService;

    public RelatoriosController(
            RelatoriosRepository repository,
            RelatoriosService relatoriosService
    ) {
        this.repository = repository;
        this.relatoriosService = relatoriosService;
    }

    @GetMapping
    public List<RelatoriosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(RelatoriosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public RelatoriosResponseDTO getById(@PathVariable Integer id) {
        Relatorios entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Relatorio nao encontrado"));

        return new RelatoriosResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RelatoriosResponseDTO saveRelatorios(@RequestBody RelatoriosRequestDTO data) {
        Relatorios saved = relatoriosService.criar(data);
        return new RelatoriosResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public RelatoriosResponseDTO updateRelatorios(@PathVariable Integer id, @RequestBody RelatoriosRequestDTO data) {
        Relatorios updated = relatoriosService.atualizar(id, data);
        return new RelatoriosResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteRelatorios(@PathVariable Integer id) {
        relatoriosService.excluir(id);
        return "Relatorio deleted";
    }
}