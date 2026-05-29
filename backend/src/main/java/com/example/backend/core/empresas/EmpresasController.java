package com.example.backend.core.empresas;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/core/empresas")
public class EmpresasController {

    private final EmpresasRepository repository;
    private final EmpresasService empresasService;

    public EmpresasController(
            EmpresasRepository repository,
            EmpresasService empresasService
    ) {
        this.repository = repository;
        this.empresasService = empresasService;
    }

    @GetMapping
    public List<EmpresasResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(EmpresasResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public EmpresasResponseDTO getById(@PathVariable Integer id) {
        Empresas entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa nao encontrada"));

        return new EmpresasResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmpresasResponseDTO saveEmpresa(@RequestBody EmpresasRequestDTO data) {
        Empresas saved = empresasService.criar(data);
        return new EmpresasResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public EmpresasResponseDTO updateEmpresa(@PathVariable Integer id, @RequestBody EmpresasRequestDTO data) {
        Empresas updated = empresasService.atualizar(id, data);
        return new EmpresasResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteEmpresa(@PathVariable Integer id) {
        empresasService.excluir(id);
        return "Empresa deleted";
    }
}