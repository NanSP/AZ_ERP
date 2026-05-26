package com.example.backend.ps.projetos;


import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ps/projetos")
public class ProjetosController {

    private final ProjetosRepository repository;
    private final ProjetosService projetosService;

    public ProjetosController(
            ProjetosRepository repository,
            ProjetosService projetosService
    ) {
        this.repository = repository;
        this.projetosService = projetosService;
    }

    @GetMapping
    public List<ProjetosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ProjetosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ProjetosResponseDTO getById(@PathVariable Integer id) {
        Projetos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Projeto nao encontrado"));

        return new ProjetosResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjetosResponseDTO saveProjetos(@RequestBody ProjetosRequestDTO data) {
        Projetos saved = projetosService.criar(data);
        return new ProjetosResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public ProjetosResponseDTO updateProjetos(@PathVariable Integer id, @RequestBody ProjetosRequestDTO data) {
        Projetos updated = projetosService.atualizar(id, data);
        return new ProjetosResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteProjetos(@PathVariable Integer id) {
        projetosService.excluir(id);
        return "Projeto deleted";
    }
}