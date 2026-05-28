package com.example.backend.grc.riscos;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grc/riscos")
public class RiscosController {

    private final RiscosRepository repository;
    private final RiscosService riscosService;

    public RiscosController(
            RiscosRepository repository,
            RiscosService riscosService
    ) {
        this.repository = repository;
        this.riscosService = riscosService;
    }

    @GetMapping
    public List<RiscosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(RiscosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public RiscosResponseDTO getById(@PathVariable Integer id) {
        Riscos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Risco nao encontrado"));

        return new RiscosResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RiscosResponseDTO saveRiscos(@RequestBody RiscosRequestDTO data) {
        Riscos saved = riscosService.criar(data);
        return new RiscosResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public RiscosResponseDTO updateRiscos(@PathVariable Integer id, @RequestBody RiscosRequestDTO data) {
        Riscos updated = riscosService.atualizar(id, data);
        return new RiscosResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteRiscos(@PathVariable Integer id) {
        riscosService.excluir(id);
        return "Risco deleted";
    }
}