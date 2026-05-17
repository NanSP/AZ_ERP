package com.example.backend.bi.historicoMetricas;

import com.example.backend.bi.metricas.Metricas;
import com.example.backend.bi.metricas.MetricasRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bi/historicoMetricas")
public class HistoricoMetricasController {

    private final HistoricoMetricasRepository repository;
    private final MetricasRepository metricasRepository;

    public HistoricoMetricasController(
            HistoricoMetricasRepository repository,
            MetricasRepository metricasRepository
    ) {
        this.repository = repository;
        this.metricasRepository = metricasRepository;
    }

    @GetMapping
    public List<HistoricoMetricasResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(HistoricoMetricasResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new HistoricoMetricasResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveHistoricoMetricas(@RequestBody HistoricoMetricasRequestDTO data) {
        try {
            Metricas metrica = data.metrica() != null
                    ? metricasRepository.findById(data.metrica())
                    .orElseThrow(() -> new RuntimeException("Metrica nao encontrada"))
                    : null;

            HistoricoMetricas entity = new HistoricoMetricas();
            entity.setMetrica(metrica);
            entity.setPeriodo(data.periodo());
            entity.setValorApurado(data.valorApurado());
            entity.setCreatedAt(data.createdAt());

            HistoricoMetricas saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new HistoricoMetricasResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateHistoricoMetricas(@PathVariable Long id, @RequestBody HistoricoMetricasRequestDTO data) {
        try {
            HistoricoMetricas entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Historico de metrica nao encontrado"));

            Metricas metrica = data.metrica() != null
                    ? metricasRepository.findById(data.metrica())
                    .orElseThrow(() -> new RuntimeException("Metrica nao encontrada"))
                    : null;

            entity.setMetrica(metrica);
            entity.setPeriodo(data.periodo());
            entity.setValorApurado(data.valorApurado());
            entity.setCreatedAt(data.createdAt());

            HistoricoMetricas updated = repository.save(entity);
            return ResponseEntity.ok(new HistoricoMetricasResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHistoricoMetricas(@PathVariable Long id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Historico de metrica deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}