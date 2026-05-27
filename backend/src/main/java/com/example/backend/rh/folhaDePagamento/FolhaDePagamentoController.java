package com.example.backend.rh.folhaDePagamento;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rh/folhaDePagamento")
public class FolhaDePagamentoController {

    private final FolhaDePagamentoRepository repository;
    private final FolhaDePagamentoService folhaDePagamentoService;

    public FolhaDePagamentoController(
            FolhaDePagamentoRepository repository,
            FolhaDePagamentoService folhaDePagamentoService
    ) {
        this.repository = repository;
        this.folhaDePagamentoService = folhaDePagamentoService;
    }

    @GetMapping
    public List<FolhaDePagamentoResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(FolhaDePagamentoResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public FolhaDePagamentoResponseDTO getById(@PathVariable Integer id) {
        FolhaDePagamento entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Folha de pagamento nao encontrada"));

        return new FolhaDePagamentoResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FolhaDePagamentoResponseDTO saveFolhaDePagamento(@RequestBody FolhaDePagamentoRequestDTO data) {
        FolhaDePagamento saved = folhaDePagamentoService.criar(data);
        return new FolhaDePagamentoResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public FolhaDePagamentoResponseDTO updateFolhaDePagamento(@PathVariable Integer id, @RequestBody FolhaDePagamentoRequestDTO data) {
        FolhaDePagamento updated = folhaDePagamentoService.atualizar(id, data);
        return new FolhaDePagamentoResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteFolhaDePagamento(@PathVariable Integer id) {
        folhaDePagamentoService.excluir(id);
        return "Folha de pagamento deleted";
    }
}