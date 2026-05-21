package com.example.backend.mm.movimentacoes;

import com.example.backend.mm.estoques.Estoques;
import com.example.backend.mm.estoques.EstoquesRepository;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mm/movimentacoes")
public class MovimentacoesController {

    private final MovimentacoesRepository repository;
    private final MovimentacoesService movimentacoesService;

    public MovimentacoesController(
            MovimentacoesRepository repository,
            MovimentacoesService movimentacoesService
    ) {
        this.repository = repository;
        this.movimentacoesService = movimentacoesService;
    }

    @GetMapping
    public List<MovimentacoesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(MovimentacoesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new MovimentacoesResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveMovimentacoes(@RequestBody MovimentacoesRequestDTO data) {
        try {
            Movimentacoes saved = movimentacoesService.criar(data);
            return ResponseEntity.status(HttpStatus.CREATED).body(new MovimentacoesResponseDTO(saved));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMovimentacoes(@PathVariable Integer id, @RequestBody MovimentacoesRequestDTO data) {
        try {
            Movimentacoes updated = movimentacoesService.atualizar(id, data);
            return ResponseEntity.ok(new MovimentacoesResponseDTO(updated));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovimentacoes(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Movimentacao deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}