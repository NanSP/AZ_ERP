package com.example.backend.qm.naoConformidade;


import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/qm/naoConformidade")
public class NaoConformidadeController {

    private final NaoConformidadeRepository repository;
    private final NaoConformidadeService naoConformidadeService;

    public NaoConformidadeController(
            NaoConformidadeRepository repository,
            NaoConformidadeService naoConformidadeService
    ) {
        this.repository = repository;
        this.naoConformidadeService = naoConformidadeService;
    }

    @GetMapping
    public List<NaoConformidadeResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(NaoConformidadeResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public NaoConformidadeResponseDTO getById(@PathVariable Integer id) {
        NaoConformidade entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nao conformidade nao encontrada"));

        return new NaoConformidadeResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NaoConformidadeResponseDTO saveNaoConformidade(@RequestBody NaoConformidadeRequestDTO data) {
        NaoConformidade saved = naoConformidadeService.criar(data);
        return new NaoConformidadeResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public NaoConformidadeResponseDTO updateNaoConformidade(@PathVariable Integer id, @RequestBody NaoConformidadeRequestDTO data) {
        NaoConformidade updated = naoConformidadeService.atualizar(id, data);
        return new NaoConformidadeResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteNaoConformidade(@PathVariable Integer id) {
        naoConformidadeService.excluir(id);
        return "Nao conformidade deleted";
    }
}