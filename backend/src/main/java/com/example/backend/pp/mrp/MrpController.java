package com.example.backend.pp.mrp;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/pp/mrp")
public class MrpController {

    private final MrpRepository repository;
    private final MrpService mrpService;

    public MrpController(
            MrpRepository repository,
            MrpService mrpService
    ) {
        this.repository = repository;
        this.mrpService = mrpService;
    }

    @GetMapping
    public List<MrpResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(MrpResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public MrpResponseDTO getById(@PathVariable Integer id) {
        Mrp entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("MRP nao encontrado"));

        return new MrpResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MrpResponseDTO saveMrp(@RequestBody MrpRequestDTO data) {
        Mrp saved = mrpService.criar(data);
        return new MrpResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public MrpResponseDTO updateMrp(@PathVariable Integer id, @RequestBody MrpRequestDTO data) {
        Mrp updated = mrpService.atualizar(id, data);
        return new MrpResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteMrp(@PathVariable Integer id) {
        mrpService.excluir(id);
        return "MRP deleted";
    }
}