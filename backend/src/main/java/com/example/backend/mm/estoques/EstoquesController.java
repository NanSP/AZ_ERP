package com.example.backend.mm.estoques;

import com.example.backend.core.empresas.Empresas;
import com.example.backend.core.empresas.EmpresasRepository;
import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mm/estoques")
public class EstoquesController {

    private final EstoquesRepository repository;
    private final ProdutosRepository produtosRepository;
    private final EmpresasRepository empresasRepository;

    public EstoquesController(
            EstoquesRepository repository,
            ProdutosRepository produtosRepository,
            EmpresasRepository empresasRepository
    ) {
        this.repository = repository;
        this.produtosRepository = produtosRepository;
        this.empresasRepository = empresasRepository;
    }

    @GetMapping
    public List<EstoquesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(EstoquesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new EstoquesResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveEstoques(@RequestBody EstoquesRequestDTO data) {
        try {
            Produtos produto = data.produto() != null
                    ? produtosRepository.findById(data.produto())
                    .orElseThrow(() -> new RuntimeException("Produto nao encontrado"))
                    : null;

            Empresas empresa = data.empresa() != null
                    ? empresasRepository.findById(data.empresa())
                    .orElseThrow(() -> new RuntimeException("Empresa nao encontrada"))
                    : null;

            Estoques entity = new Estoques();
            entity.setProduto(produto);
            entity.setEmpresa(empresa);
            entity.setLocalizacao(data.localizacao());
            entity.setLote(data.lote());
            entity.setQuantidade(data.quantidade());
            entity.setQuantidadeMinima(data.quantidadeMinima());
            entity.setQuantidadeMaxima(data.quantidadeMaxima());
            entity.setValorUnitario(data.valorUnitario());
            entity.setDataValidade(data.dataValidade());
            entity.setCreatedAt(data.createdAt());

            Estoques saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new EstoquesResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEstoques(@PathVariable Integer id, @RequestBody EstoquesRequestDTO data) {
        try {
            Estoques entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Estoque nao encontrado"));

            Produtos produto = data.produto() != null
                    ? produtosRepository.findById(data.produto())
                    .orElseThrow(() -> new RuntimeException("Produto nao encontrado"))
                    : null;

            Empresas empresa = data.empresa() != null
                    ? empresasRepository.findById(data.empresa())
                    .orElseThrow(() -> new RuntimeException("Empresa nao encontrada"))
                    : null;

            entity.setProduto(produto);
            entity.setEmpresa(empresa);
            entity.setLocalizacao(data.localizacao());
            entity.setLote(data.lote());
            entity.setQuantidade(data.quantidade());
            entity.setQuantidadeMinima(data.quantidadeMinima());
            entity.setQuantidadeMaxima(data.quantidadeMaxima());
            entity.setValorUnitario(data.valorUnitario());
            entity.setDataValidade(data.dataValidade());
            entity.setCreatedAt(data.createdAt());

            Estoques updated = repository.save(entity);
            return ResponseEntity.ok(new EstoquesResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEstoques(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Estoque deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}