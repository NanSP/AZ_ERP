package com.example.backend.pp.ordemProducao;

import com.example.backend.core.produtos.Produtos;
import com.example.backend.core.produtos.ProdutosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pp/ordemProducao")
public class OrdemProducaoController {

    private final OrdemProducaoRepository repository;
    private final ProdutosRepository produtosRepository;

    public OrdemProducaoController(
            OrdemProducaoRepository repository,
            ProdutosRepository produtosRepository
    ) {
        this.repository = repository;
        this.produtosRepository = produtosRepository;
    }

    @GetMapping
    public List<OrdemProducaoResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(OrdemProducaoResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new OrdemProducaoResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveOrdemProducao(@RequestBody OrdemProducaoRequestDTO data) {
        try {
            Produtos produto = data.produto() != null
                    ? produtosRepository.findById(data.produto())
                    .orElseThrow(() -> new RuntimeException("Produto nao encontrado"))
                    : null;

            OrdemProducao entity = new OrdemProducao();
            entity.setNumeroOp(data.numeroOp());
            entity.setProduto(produto);
            entity.setQuantidadePlanejada(data.quantidadePlanejada());
            entity.setQuantidadeProduzida(data.quantidadeProduzida());
            entity.setDataEmissao(data.dataEmissao());
            entity.setDataInicio(data.dataInicio());
            entity.setDataFim(data.dataFim());
            entity.setDataPrevista(data.dataPrevista());
            entity.setStatus(data.status());
            entity.setPrioridade(data.prioridade());
            entity.setObservacoes(data.observacoes());
            entity.setCreatedAt(data.createdAt());

            OrdemProducao saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new OrdemProducaoResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrdemProducao(@PathVariable Integer id, @RequestBody OrdemProducaoRequestDTO data) {
        try {
            OrdemProducao entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Ordem de producao nao encontrada"));

            Produtos produto = data.produto() != null
                    ? produtosRepository.findById(data.produto())
                    .orElseThrow(() -> new RuntimeException("Produto nao encontrado"))
                    : null;

            entity.setNumeroOp(data.numeroOp());
            entity.setProduto(produto);
            entity.setQuantidadePlanejada(data.quantidadePlanejada());
            entity.setQuantidadeProduzida(data.quantidadeProduzida());
            entity.setDataEmissao(data.dataEmissao());
            entity.setDataInicio(data.dataInicio());
            entity.setDataFim(data.dataFim());
            entity.setDataPrevista(data.dataPrevista());
            entity.setStatus(data.status());
            entity.setPrioridade(data.prioridade());
            entity.setObservacoes(data.observacoes());
            entity.setCreatedAt(data.createdAt());

            OrdemProducao updated = repository.save(entity);
            return ResponseEntity.ok(new OrdemProducaoResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrdemProducao(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Ordem Producao deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}