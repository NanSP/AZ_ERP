package com.example.backend.sm.ordensServico;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sm/ordensServico")
public class OrdensServicoController {

    private final OrdensServicoRepository repository;
    private final OrdensServicoService ordensServicoService;

    public OrdensServicoController(
            OrdensServicoRepository repository,
            OrdensServicoService ordensServicoService
    ) {
        this.repository = repository;
        this.ordensServicoService = ordensServicoService;
    }

    @GetMapping
    public List<OrdensServicoResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(OrdensServicoResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public OrdensServicoResponseDTO getById(@PathVariable Integer id) {
        OrdensServico entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ordem de servico nao encontrada"));

        return new OrdensServicoResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrdensServicoResponseDTO saveOrdensServico(@RequestBody OrdensServicoRequestDTO data) {
        OrdensServico saved = ordensServicoService.criar(data);
        return new OrdensServicoResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public OrdensServicoResponseDTO updateOrdensServico(@PathVariable Integer id, @RequestBody OrdensServicoRequestDTO data) {
        OrdensServico updated = ordensServicoService.atualizar(id, data);
        return new OrdensServicoResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteOrdensServico(@PathVariable Integer id) {
        ordensServicoService.excluir(id);
        return "Ordem de servico deleted";
    }
}