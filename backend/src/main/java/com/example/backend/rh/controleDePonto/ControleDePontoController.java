package com.example.backend.rh.controleDePonto;

import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rh/controleDePonto")
public class ControleDePontoController {

    private final ControleDePontoRepository repository;
    private final ColaboradoresRepository colaboradoresRepository;

    public ControleDePontoController(
            ControleDePontoRepository repository,
            ColaboradoresRepository colaboradoresRepository
    ) {
        this.repository = repository;
        this.colaboradoresRepository = colaboradoresRepository;
    }

    @GetMapping
    public List<ControleDePontoResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ControleDePontoResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new ControleDePontoResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveControleDePonto(@RequestBody ControleDePontoRequestDTO data) {
        try {
            Colaboradores colaborador = data.colaborador() != null
                    ? colaboradoresRepository.findById(data.colaborador())
                    .orElseThrow(() -> new RuntimeException("Colaborador nao encontrado"))
                    : null;

            ControleDePonto entity = new ControleDePonto();
            entity.setColaborador(colaborador);
            entity.setData(data.data());
            entity.setHoraEntrada(data.horaEntrada());
            entity.setHoraSaidaAlmoco(data.horaSaidaAlmoco());
            entity.setHoraRetornoAlmoco(data.horaRetornoAlmoco());
            entity.setHoraSaida(data.horaSaida());
            entity.setHorasTrabalhadas(data.horasTrabalhadas());
            entity.setHorasExtras(data.horasExtras());
            entity.setAtrasos(data.atrasos());
            entity.setCreatedAt(data.createdAt());

            ControleDePonto saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ControleDePontoResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateControleDePonto(@PathVariable Integer id, @RequestBody ControleDePontoRequestDTO data) {
        try {
            ControleDePonto entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Controle de ponto nao encontrado"));

            Colaboradores colaborador = data.colaborador() != null
                    ? colaboradoresRepository.findById(data.colaborador())
                    .orElseThrow(() -> new RuntimeException("Colaborador nao encontrado"))
                    : null;

            entity.setColaborador(colaborador);
            entity.setData(data.data());
            entity.setHoraEntrada(data.horaEntrada());
            entity.setHoraSaidaAlmoco(data.horaSaidaAlmoco());
            entity.setHoraRetornoAlmoco(data.horaRetornoAlmoco());
            entity.setHoraSaida(data.horaSaida());
            entity.setHorasTrabalhadas(data.horasTrabalhadas());
            entity.setHorasExtras(data.horasExtras());
            entity.setAtrasos(data.atrasos());
            entity.setCreatedAt(data.createdAt());

            ControleDePonto updated = repository.save(entity);
            return ResponseEntity.ok(new ControleDePontoResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteControleDePonto(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Ponto deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}