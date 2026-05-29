package com.example.backend.sys.perfilPermissao;

import com.example.backend.shared.exception.RecursoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/perfilPermissao")
public class PerfilPermissaoController {

    private final PerfilPermissaoRepository repository;
    private final PerfilPermissaoService perfilPermissaoService;

    public PerfilPermissaoController(
            PerfilPermissaoRepository repository,
            PerfilPermissaoService perfilPermissaoService
    ) {
        this.repository = repository;
        this.perfilPermissaoService = perfilPermissaoService;
    }

    @GetMapping
    public List<PerfilPermissaoResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(PerfilPermissaoResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public PerfilPermissaoResponseDTO getById(@PathVariable Integer id) {
        PerfilPermissao entity = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vinculo perfil-permissao nao encontrado"));

        return new PerfilPermissaoResponseDTO(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PerfilPermissaoResponseDTO savePerfilPermissao(@RequestBody PerfilPermissaoRequestDTO data) {
        PerfilPermissao saved = perfilPermissaoService.criar(data);
        return new PerfilPermissaoResponseDTO(saved);
    }

    @PutMapping("/{id}")
    public PerfilPermissaoResponseDTO updatePerfilPermissao(@PathVariable Integer id, @RequestBody PerfilPermissaoRequestDTO data) {
        PerfilPermissao updated = perfilPermissaoService.atualizar(id, data);
        return new PerfilPermissaoResponseDTO(updated);
    }

    @DeleteMapping("/{id}")
    public String deletePerfilPermissao(@PathVariable Integer id) {
        perfilPermissaoService.excluir(id);
        return "PerfilPermissao deleted";
    }
}