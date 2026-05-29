package com.example.backend.core.parceiros;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/core/parceiros")
public class ParceirosController {

    private final ParceirosRepository repository;
    private final ParceirosService parceirosService;

    public ParceirosController(
            ParceirosRepository repository,
            ParceirosService parceirosService
    ) {
        this.repository = repository;
        this.parceirosService = parceirosService;
    }

    @GetMapping
    public List<ParceirosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ParceirosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ParceirosResponseDTO getById(@PathVariable Integer id) {
        Parceiros entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Parceiro nao encontrado"));

        return new ParceirosResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParceirosResponseDTO saveParceiro(@RequestBody ParceirosRequestDTO data) {
        Parceiros saved = parceirosService.criar(data);
        return new ParceirosResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public ParceirosResponseDTO updateParceiro(@PathVariable Integer id, @RequestBody ParceirosRequestDTO data) {
        Parceiros updated = parceirosService.atualizar(id, data);
        return new ParceirosResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteParceiro(@PathVariable Integer id) {
        parceirosService.excluir(id);
        return "Parceiro deleted";
    }
}