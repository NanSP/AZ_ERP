package com.example.backend.qm.inspecoes;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/qm/inspecoes")
public class InspecoesController {

    private final InspecoesRepository repository;
    private final ProdutosRepository produtosRepository;
    private final ColaboradoresRepository colaboradoresRepository;

    public InspecoesController(
            InspecoesRepository repository,
            ProdutosRepository produtosRepository,
            ColaboradoresRepository colaboradoresRepository
    ) {
        this.repository = repository;
        this.produtosRepository = produtosRepository;
        this.colaboradoresRepository = colaboradoresRepository;
    }

    @GetMapping
    public List<InspecoesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(InspecoesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new InspecoesResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveInspecoes(@RequestBody InspecoesRequestDTO data) {
        try {
            Produtos produto = data.produto() != null
                    ? produtosRepository.findById(data.produto())
                    .orElseThrow(() -> new RuntimeException("Produto nao encontrado"))
                    : null;

            Colaboradores inspetor = data.inspetor() != null
                    ? colaboradoresRepository.findById(data.inspetor())
                    .orElseThrow(() -> new RuntimeException("Inspetor nao encontrado"))
                    : null;

            Inspecoes entity = new Inspecoes();
            entity.setTipoInspecao(data.tipoInspecao());
            entity.setProduto(produto);
            entity.setLote(data.lote());
            entity.setQuantidadeInspecionada(data.quantidadeInspecionada());
            entity.setQuantidadeAprovada(data.quantidadeAprovada());
            entity.setQuantidadeReprovada(data.quantidadeReprovada());
            entity.setDataInspecao(data.dataInspecao());
            entity.setInspetor(inspetor);
            entity.setResultado(data.resultado());
            entity.setObservacoes(data.observacoes());
            entity.setCreatedAt(data.createdAt());

            Inspecoes saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new InspecoesResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateInspecoes(@PathVariable Integer id, @RequestBody InspecoesRequestDTO data) {
        try {
            Inspecoes entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Inspecao nao encontrada"));

            Produtos produto = data.produto() != null
                    ? produtosRepository.findById(data.produto())
                    .orElseThrow(() -> new RuntimeException("Produto nao encontrado"))
                    : null;

            Colaboradores inspetor = data.inspetor() != null
                    ? colaboradoresRepository.findById(data.inspetor())
                    .orElseThrow(() -> new RuntimeException("Inspetor nao encontrado"))
                    : null;

            entity.setTipoInspecao(data.tipoInspecao());
            entity.setProduto(produto);
            entity.setLote(data.lote());
            entity.setQuantidadeInspecionada(data.quantidadeInspecionada());
            entity.setQuantidadeAprovada(data.quantidadeAprovada());
            entity.setQuantidadeReprovada(data.quantidadeReprovada());
            entity.setDataInspecao(data.dataInspecao());
            entity.setInspetor(inspetor);
            entity.setResultado(data.resultado());
            entity.setObservacoes(data.observacoes());
            entity.setCreatedAt(data.createdAt());

            Inspecoes updated = repository.save(entity);
            return ResponseEntity.ok(new InspecoesResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInspecoes(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Inspecao deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}