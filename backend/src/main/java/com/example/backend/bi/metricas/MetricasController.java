package com.example.backend.bi.metricas;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bi/metricas")
public class MetricasController {

    private final MetricasRepository repository;
    private final MetricasService metricasService;

    public MetricasController(
            MetricasRepository repository,
            MetricasService metricasService
    ) {
        this.repository = repository;
        this.metricasService = metricasService;
    }

    @GetMapping
    public List<MetricasResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(MetricasResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public MetricasResponseDTO getById(@PathVariable Integer id) {
        Metricas entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Metrica nao encontrada"));

        return new MetricasResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MetricasResponseDTO saveMetricas(@RequestBody MetricasRequestDTO data) {
        Metricas saved = metricasService.criar(data);
        return new MetricasResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public MetricasResponseDTO updateMetricas(@PathVariable Integer id, @RequestBody MetricasRequestDTO data) {
        Metricas updated = metricasService.atualizar(id, data);
        return new MetricasResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteMetricas(@PathVariable Integer id) {
        metricasService.excluir(id);
        return "Metrica deleted";
    }
}