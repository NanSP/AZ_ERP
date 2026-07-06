package com.example.backend.grc.relatoriosImpacto;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grc/relatoriosImpacto")
public class RelatoriosImpactoController {

    private final RelatoriosImpactoRepository repository;
    private final RelatoriosImpactoService service;

    public RelatoriosImpactoController(
            RelatoriosImpactoRepository repository,
            RelatoriosImpactoService service
    ) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping
    public List<RelatoriosImpactoResponseDTO> getAll() {
        return repository.findAll().stream().map(RelatoriosImpactoResponseDTO::new).toList();
    }

    @GetMapping("/{id}")
    public RelatoriosImpactoResponseDTO getById(@PathVariable Integer id) {
        RelatoriosImpacto entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Relatorio de impacto nao encontrado"));
        return new RelatoriosImpactoResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RelatoriosImpactoResponseDTO create(@RequestBody RelatoriosImpactoRequestDTO data) {
        return new RelatoriosImpactoResponseDTO(service.criar(data));
    }

    @PutMapping("/{id}")
    public RelatoriosImpactoResponseDTO update(@PathVariable Integer id, @RequestBody RelatoriosImpactoRequestDTO data) {
        return new RelatoriosImpactoResponseDTO(service.atualizar(id, data));
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        service.excluir(id);
        return "Relatorio de impacto deleted";
    }
}
