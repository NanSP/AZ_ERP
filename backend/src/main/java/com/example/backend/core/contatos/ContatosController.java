package com.example.backend.core.contatos;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/core/contatos")
public class ContatosController {

    private final ContatosRepository repository;
    private final ContatosService contatosService;

    public ContatosController(
            ContatosRepository repository,
            ContatosService contatosService
    ) {
        this.repository = repository;
        this.contatosService = contatosService;
    }

    @GetMapping
    public List<ContatosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ContatosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ContatosResponseDTO getById(@PathVariable Integer id) {
        Contatos entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Contato nao encontrado"));

        return new ContatosResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContatosResponseDTO saveContato(@RequestBody ContatosRequestDTO data) {
        Contatos saved = contatosService.criar(data);
        return new ContatosResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public ContatosResponseDTO updateContato(@PathVariable Integer id, @RequestBody ContatosRequestDTO data) {
        Contatos updated = contatosService.atualizar(id, data);
        return new ContatosResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deleteContato(@PathVariable Integer id) {
        contatosService.excluir(id);
        return "Contato deleted";
    }
}