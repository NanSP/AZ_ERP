package com.example.backend.rh.beneficios;

import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rh/beneficios")
public class BeneficiosController {

    private final BeneficiosRepository repository;
    private final ColaboradoresRepository colaboradoresRepository;

    public BeneficiosController(
            BeneficiosRepository repository,
            ColaboradoresRepository colaboradoresRepository
    ) {
        this.repository = repository;
        this.colaboradoresRepository = colaboradoresRepository;
    }

    @GetMapping
    public List<BeneficiosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(BeneficiosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new BeneficiosResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveBeneficio(@RequestBody BeneficiosRequestDTO data) {
        try {
            Colaboradores colaborador = data.colaborador() != null
                    ? colaboradoresRepository.findById(data.colaborador())
                    .orElseThrow(() -> new RuntimeException("Colaborador nao encontrado"))
                    : null;

            Beneficios entity = new Beneficios();
            entity.setColaborador(colaborador);
            entity.setTipoBeneficio(data.tipoBeneficio());
            entity.setValor(data.valor());
            entity.setDataInicio(data.dataInicio());
            entity.setDataFim(data.dataFim());
            entity.setAtivo(data.ativo());

            Beneficios saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new BeneficiosResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBeneficio(@PathVariable Integer id, @RequestBody BeneficiosRequestDTO data) {
        try {
            Beneficios entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Beneficio nao encontrado"));

            Colaboradores colaborador = data.colaborador() != null
                    ? colaboradoresRepository.findById(data.colaborador())
                    .orElseThrow(() -> new RuntimeException("Colaborador nao encontrado"))
                    : null;

            entity.setColaborador(colaborador);
            entity.setTipoBeneficio(data.tipoBeneficio());
            entity.setValor(data.valor());
            entity.setDataInicio(data.dataInicio());
            entity.setDataFim(data.dataFim());
            entity.setAtivo(data.ativo());

            Beneficios updated = repository.save(entity);
            return ResponseEntity.ok(new BeneficiosResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBeneficio(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Beneficio deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}