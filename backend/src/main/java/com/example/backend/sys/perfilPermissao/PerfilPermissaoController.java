package com.example.backend.sys.perfilPermissao;

import com.example.backend.sys.perfis.Perfis;
import com.example.backend.sys.perfis.PerfisRepository;
import com.example.backend.sys.permissoes.Permissoes;
import com.example.backend.sys.permissoes.PermissoesRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/perfilPermissao")
public class PerfilPermissaoController {

    private final PerfilPermissaoRepository repository;
    private final PerfisRepository perfisRepository;
    private final PermissoesRepository permissoesRepository;

    public PerfilPermissaoController(
            PerfilPermissaoRepository repository,
            PerfisRepository perfisRepository,
            PermissoesRepository permissoesRepository
    ) {
        this.repository = repository;
        this.perfisRepository = perfisRepository;
        this.permissoesRepository = permissoesRepository;
    }

    @GetMapping
    public List<PerfilPermissaoResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(PerfilPermissaoResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new PerfilPermissaoResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> savePerfilPermissao(@RequestBody PerfilPermissaoRequestDTO data) {
        try {
            Perfis perfil = perfisRepository.findById(data.perfil())
                    .orElseThrow(() -> new RuntimeException("Perfil nao encontrado"));

            Permissoes permissao = permissoesRepository.findById(data.permissao())
                    .orElseThrow(() -> new RuntimeException("Permissao nao encontrada"));

            PerfilPermissao entity = new PerfilPermissao();
            entity.setPerfil(perfil);
            entity.setPermissao(permissao);

            PerfilPermissao saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new PerfilPermissaoResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePerfilPermissao(@PathVariable Integer id, @RequestBody PerfilPermissaoRequestDTO data) {
        try {
            PerfilPermissao entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Relacao perfil_permissao nao encontrada"));

            Perfis perfil = perfisRepository.findById(data.perfil())
                    .orElseThrow(() -> new RuntimeException("Perfil nao encontrado"));

            Permissoes permissao = permissoesRepository.findById(data.permissao())
                    .orElseThrow(() -> new RuntimeException("Permissao nao encontrada"));

            entity.setPerfil(perfil);
            entity.setPermissao(permissao);

            PerfilPermissao updated = repository.save(entity);
            return ResponseEntity.ok(new PerfilPermissaoResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePerfilPermissao(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Perfil Permissao deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}
