package com.example.backend.sys.usuarioPerfil;

import com.example.backend.sys.perfis.Perfis;
import com.example.backend.sys.perfis.PerfisRepository;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/usuarioPerfil")
public class UsuarioPerfilController {

    private final UsuarioPerfilRepository repository;
    private final UsuariosRepository usuariosRepository;
    private final PerfisRepository perfisRepository;

    public UsuarioPerfilController(
            UsuarioPerfilRepository repository,
            UsuariosRepository usuariosRepository,
            PerfisRepository perfisRepository
    ) {
        this.repository = repository;
        this.usuariosRepository = usuariosRepository;
        this.perfisRepository = perfisRepository;
    }

    @GetMapping
    public List<UsuarioPerfilResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(UsuarioPerfilResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new UsuarioPerfilResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveUsuarioPerfil(@RequestBody UsuarioPerfilRequestDTO data) {
        try {
            Usuarios usuario = usuariosRepository.findById(data.usuario())
                    .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));

            Perfis perfil = perfisRepository.findById(data.perfil())
                    .orElseThrow(() -> new RuntimeException("Perfil nao encontrado"));

            UsuarioPerfil entity = new UsuarioPerfil();
            entity.setUsuario(usuario);
            entity.setPerfil(perfil);
            entity.setDataAtribuicao(data.dataAtribuicao());

            UsuarioPerfil saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioPerfilResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuarioPerfil(@PathVariable Integer id, @RequestBody UsuarioPerfilRequestDTO data) {
        try {
            UsuarioPerfil entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Relacao usuario_perfil nao encontrada"));

            Usuarios usuario = usuariosRepository.findById(data.usuario())
                    .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));

            Perfis perfil = perfisRepository.findById(data.perfil())
                    .orElseThrow(() -> new RuntimeException("Perfil nao encontrado"));

            entity.setUsuario(usuario);
            entity.setPerfil(perfil);
            entity.setDataAtribuicao(data.dataAtribuicao());

            UsuarioPerfil updated = repository.save(entity);
            return ResponseEntity.ok(new UsuarioPerfilResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuarioPerfil(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Usuario Perfil deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}