package com.example.backend.pp.bom;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pp/bom")
public class BomController {

    private final BomRepository repository;
    private final ProdutosRepository produtosRepository;

    public BomController(
            BomRepository repository,
            ProdutosRepository produtosRepository
    ) {
        this.repository = repository;
        this.produtosRepository = produtosRepository;
    }

    @GetMapping
    public List<BomResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(BomResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new BomResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveBom(@RequestBody BomRequestDTO data) {
        try {
            Produtos produtoPai = data.produtoPai() != null
                    ? produtosRepository.findById(data.produtoPai())
                    .orElseThrow(() -> new RuntimeException("Produto pai nao encontrado"))
                    : null;

            Produtos componente = data.componente() != null
                    ? produtosRepository.findById(data.componente())
                    .orElseThrow(() -> new RuntimeException("Componente nao encontrado"))
                    : null;

            Bom entity = new Bom();
            entity.setProdutoPai(produtoPai);
            entity.setComponente(componente);
            entity.setQuantidade(data.quantidade());
            entity.setUnidadeMedida(data.unidadeMedida());
            entity.setNivel(data.nivel());
            entity.setTempoPreparacao(data.tempoPreparacao());
            entity.setTempoProducao(data.tempoProducao());
            entity.setRoteiro(data.roteiro());
            entity.setCreatedAt(data.createdAt());

            Bom saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new BomResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBom(@PathVariable Integer id, @RequestBody BomRequestDTO data) {
        try {
            Bom entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("BOM nao encontrado"));

            Produtos produtoPai = data.produtoPai() != null
                    ? produtosRepository.findById(data.produtoPai())
                    .orElseThrow(() -> new RuntimeException("Produto pai nao encontrado"))
                    : null;

            Produtos componente = data.componente() != null
                    ? produtosRepository.findById(data.componente())
                    .orElseThrow(() -> new RuntimeException("Componente nao encontrado"))
                    : null;

            entity.setProdutoPai(produtoPai);
            entity.setComponente(componente);
            entity.setQuantidade(data.quantidade());
            entity.setUnidadeMedida(data.unidadeMedida());
            entity.setNivel(data.nivel());
            entity.setTempoPreparacao(data.tempoPreparacao());
            entity.setTempoProducao(data.tempoProducao());
            entity.setRoteiro(data.roteiro());
            entity.setCreatedAt(data.createdAt());

            Bom updated = repository.save(entity);
            return ResponseEntity.ok(new BomResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBom(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("BOM deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}