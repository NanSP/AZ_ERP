package com.example.backend.bi.historicoMetricas;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bi/historicoMetricas")
public class HistoricoMetricasController {

    private final HistoricoMetricasRepository repository;
    private final HistoricoMetricasService historicoMetricasService;

    public HistoricoMetricasController(
            HistoricoMetricasRepository repository,
            HistoricoMetricasService historicoMetricasService
    ) {
        this.repository = repository;
        this.historicoMetricasService = historicoMetricasService;
    }

    @GetMapping
    public List<HistoricoMetricasResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(HistoricoMetricasResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public HistoricoMetricasResponseDTO getById(@PathVariable Long id) {
        HistoricoMetricas entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Historico de metrica nao encontrado"));

        return new HistoricoMetricasResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HistoricoMetricasResponseDTO saveHistoricoMetricas(@RequestBody HistoricoMetricasRequestDTO data) {
        HistoricoMetricas saved = historicoMetricasService.criar(data);
        return new HistoricoMetricasResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public HistoricoMetricasResponseDTO updateHistoricoMetricas(@PathVariable Long id, @RequestBody HistoricoMetricasRequestDTO data) {
        HistoricoMetricas updated = historicoMetricasService.atualizar(id, data);
        return new HistoricoMetricasResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteHistoricoMetricas(@PathVariable Long id) {
        historicoMetricasService.excluir(id);
        return "Historico de metrica deleted";
    }
}