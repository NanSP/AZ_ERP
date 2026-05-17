package com.example.backend.am.bensPatrimoniais;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/am/bensPatrimoniais")
public class BensPatrimoniaisController {

    private final BensPatrimoniaisRepository repository;
    private final ParceirosRepository parceirosRepository;
    private final ColaboradoresRepository colaboradoresRepository;

    public BensPatrimoniaisController(
            BensPatrimoniaisRepository repository,
            ParceirosRepository parceirosRepository,
            ColaboradoresRepository colaboradoresRepository
    ) {
        this.repository = repository;
        this.parceirosRepository = parceirosRepository;
        this.colaboradoresRepository = colaboradoresRepository;
    }

    @GetMapping
    public List<BensPatrimoniaisResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(BensPatrimoniaisResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new BensPatrimoniaisResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveBensPatrimoniais(@RequestBody BensPatrimoniaisRequestDTO data) {
        try {
            Parceiros fornecedor = data.fornecedor() != null
                    ? parceirosRepository.findById(data.fornecedor())
                    .orElseThrow(() -> new RuntimeException("Fornecedor nao encontrado"))
                    : null;

            Colaboradores responsavel = data.responsavel() != null
                    ? colaboradoresRepository.findById(data.responsavel())
                    .orElseThrow(() -> new RuntimeException("Responsavel nao encontrado"))
                    : null;

            BensPatrimoniais entity = new BensPatrimoniais();
            entity.setCodigoPatrimonio(data.codigoPatrimonio());
            entity.setNome(data.nome());
            entity.setDescricao(data.descricao());
            entity.setTipoAtivo(data.tipoAtivo());
            entity.setLocalizacao(data.localizacao());
            entity.setDataAquisicao(data.dataAquisicao());
            entity.setValorAquisicao(data.valorAquisicao());
            entity.setValorAtual(data.valorAtual());
            entity.setVidaUtilAnos(data.vidaUtilAnos());
            entity.setTaxaDepreciacao(data.taxaDepreciacao());
            entity.setDataDepreciacao(data.dataDepreciacao());
            entity.setFornecedor(fornecedor);
            entity.setResponsavel(responsavel);
            entity.setStatus(data.status());
            entity.setCreatedAt(data.createdAt());

            BensPatrimoniais saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new BensPatrimoniaisResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBensPatrimoniais(@PathVariable Integer id, @RequestBody BensPatrimoniaisRequestDTO data) {
        try {
            BensPatrimoniais entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Bem patrimonial nao encontrado"));

            Parceiros fornecedor = data.fornecedor() != null
                    ? parceirosRepository.findById(data.fornecedor())
                    .orElseThrow(() -> new RuntimeException("Fornecedor nao encontrado"))
                    : null;

            Colaboradores responsavel = data.responsavel() != null
                    ? colaboradoresRepository.findById(data.responsavel())
                    .orElseThrow(() -> new RuntimeException("Responsavel nao encontrado"))
                    : null;

            entity.setCodigoPatrimonio(data.codigoPatrimonio());
            entity.setNome(data.nome());
            entity.setDescricao(data.descricao());
            entity.setTipoAtivo(data.tipoAtivo());
            entity.setLocalizacao(data.localizacao());
            entity.setDataAquisicao(data.dataAquisicao());
            entity.setValorAquisicao(data.valorAquisicao());
            entity.setValorAtual(data.valorAtual());
            entity.setVidaUtilAnos(data.vidaUtilAnos());
            entity.setTaxaDepreciacao(data.taxaDepreciacao());
            entity.setDataDepreciacao(data.dataDepreciacao());
            entity.setFornecedor(fornecedor);
            entity.setResponsavel(responsavel);
            entity.setStatus(data.status());
            entity.setCreatedAt(data.createdAt());

            BensPatrimoniais updated = repository.save(entity);
            return ResponseEntity.ok(new BensPatrimoniaisResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBensPatrimoniais(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Bem patrimonial deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}