package com.example.backend.rh.dependentes;

import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rh/dependentes")
public class DependentesController {

    private final DependentesRepository repository;
    private final ColaboradoresRepository colaboradoresRepository;

    public DependentesController(
            DependentesRepository repository,
            ColaboradoresRepository colaboradoresRepository
    ) {
        this.repository = repository;
        this.colaboradoresRepository = colaboradoresRepository;
    }

    @GetMapping
    public List<DependentesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(DependentesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new DependentesResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveDependente(@RequestBody DependentesRequestDTO data) {
        try {
            Colaboradores colaborador = data.colaborador() != null
                    ? colaboradoresRepository.findById(data.colaborador())
                    .orElseThrow(() -> new RuntimeException("Colaborador nao encontrado"))
                    : null;

            Dependentes entity = new Dependentes();
            entity.setColaborador(colaborador);
            entity.setNome(data.nome());
            entity.setDataNascimento(data.dataNascimento());
            entity.setParentesco(data.parentesco());
            entity.setCpf(data.cpf());
            entity.setCreatedAt(data.createdAt());

            Dependentes saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new DependentesResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDependente(@PathVariable Integer id, @RequestBody DependentesRequestDTO data) {
        try {
            Dependentes entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Dependente nao encontrado"));

            Colaboradores colaborador = data.colaborador() != null
                    ? colaboradoresRepository.findById(data.colaborador())
                    .orElseThrow(() -> new RuntimeException("Colaborador nao encontrado"))
                    : null;

            entity.setColaborador(colaborador);
            entity.setNome(data.nome());
            entity.setDataNascimento(data.dataNascimento());
            entity.setParentesco(data.parentesco());
            entity.setCpf(data.cpf());
            entity.setCreatedAt(data.createdAt());

            Dependentes updated = repository.save(entity);
            return ResponseEntity.ok(new DependentesResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDependente(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Dependente deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}