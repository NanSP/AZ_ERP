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
    private final EstoquesRepository estoquesRepository;
    private final UsuariosRepository usuariosRepository;

    public MovimentacoesController(
            MovimentacoesRepository repository,
            EstoquesRepository estoquesRepository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.estoquesRepository = estoquesRepository;
        this.usuariosRepository = usuariosRepository;
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
            Estoques estoque = data.estoque() != null
                    ? estoquesRepository.findById(data.estoque())
                    .orElseThrow(() -> new RuntimeException("Estoque nao encontrado"))
                    : null;

            Usuarios usuario = data.usuario() != null
                    ? usuariosRepository.findById(data.usuario())
                    .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"))
                    : null;

            Movimentacoes entity = new Movimentacoes();
            entity.setEstoque(estoque);
            entity.setTipoMovimento(data.tipoMovimento());
            entity.setQuantidade(data.quantidade());
            entity.setValorUnitario(data.valorUnitario());
            entity.setValorTotal(data.valorTotal());
            entity.setDocumentoReferencia(data.documentoReferencia());
            entity.setMotivo(data.motivo());
            entity.setUsuario(usuario);
            entity.setCreatedAt(data.createdAt());

            Movimentacoes saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new MovimentacoesResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMovimentacoes(@PathVariable Integer id, @RequestBody MovimentacoesRequestDTO data) {
        try {
            Movimentacoes entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Movimentacao nao encontrada"));

            Estoques estoque = data.estoque() != null
                    ? estoquesRepository.findById(data.estoque())
                    .orElseThrow(() -> new RuntimeException("Estoque nao encontrado"))
                    : null;

            Usuarios usuario = data.usuario() != null
                    ? usuariosRepository.findById(data.usuario())
                    .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"))
                    : null;

            entity.setEstoque(estoque);
            entity.setTipoMovimento(data.tipoMovimento());
            entity.setQuantidade(data.quantidade());
            entity.setValorUnitario(data.valorUnitario());
            entity.setValorTotal(data.valorTotal());
            entity.setDocumentoReferencia(data.documentoReferencia());
            entity.setMotivo(data.motivo());
            entity.setUsuario(usuario);
            entity.setCreatedAt(data.createdAt());

            Movimentacoes updated = repository.save(entity);
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