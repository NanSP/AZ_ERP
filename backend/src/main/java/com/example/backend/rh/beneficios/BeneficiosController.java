package com.example.backend.rh.beneficios;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/rh/beneficios")
public class BeneficiosController {

    private final BeneficiosRepository repository;
    private final BeneficiosService beneficiosService;

    public BeneficiosController(
            BeneficiosRepository repository,
            BeneficiosService beneficiosService
    ) {
        this.repository = repository;
        this.beneficiosService = beneficiosService;
    }

    @GetMapping
    public List<BeneficiosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(BeneficiosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public BeneficiosResponseDTO getById(@PathVariable Integer id) {
        Beneficios entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Beneficio nao encontrado"));

        return new BeneficiosResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeneficiosResponseDTO saveBeneficio(@RequestBody BeneficiosRequestDTO data) {
        Beneficios saved = beneficiosService.criar(data);
        return new BeneficiosResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public BeneficiosResponseDTO updateBeneficio(@PathVariable Integer id, @RequestBody BeneficiosRequestDTO data) {
        Beneficios updated = beneficiosService.atualizar(id, data);
        return new BeneficiosResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteBeneficio(@PathVariable Integer id) {
        beneficiosService.excluir(id);
        return "Beneficio deleted";
    }
}