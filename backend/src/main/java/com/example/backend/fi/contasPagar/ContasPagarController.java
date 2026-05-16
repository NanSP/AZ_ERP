package com.example.backend.fi.contasPagar;

import com.example.backend.core.empresas.Empresas;
import com.example.backend.core.empresas.EmpresasRepository;
import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.fi.centrosCusto.CentrosCusto;
import com.example.backend.fi.centrosCusto.CentrosCustoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fi/contasPagar")
public class ContasPagarController {

    private final ContasPagarRepository repository;
    private final EmpresasRepository empresasRepository;
    private final ParceirosRepository parceirosRepository;
    private final CentrosCustoRepository centrosCustoRepository;

    public ContasPagarController(
            ContasPagarRepository repository,
            EmpresasRepository empresasRepository,
            ParceirosRepository parceirosRepository,
            CentrosCustoRepository centrosCustoRepository
    ) {
        this.repository = repository;
        this.empresasRepository = empresasRepository;
        this.parceirosRepository = parceirosRepository;
        this.centrosCustoRepository = centrosCustoRepository;
    }

    @GetMapping
    public List<ContasPagarResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ContasPagarResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new ContasPagarResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveContasPagar(@RequestBody ContasPagarRequestDTO data) {
        try {
            Empresas empresa = data.empresa() != null
                    ? empresasRepository.findById(data.empresa())
                    .orElseThrow(() -> new RuntimeException("Empresa nao encontrada"))
                    : null;

            Parceiros fornecedor = data.fornecedor() != null
                    ? parceirosRepository.findById(data.fornecedor())
                    .orElseThrow(() -> new RuntimeException("Fornecedor nao encontrado"))
                    : null;

            CentrosCusto centroCusto = data.centroCusto() != null
                    ? centrosCustoRepository.findById(data.centroCusto())
                    .orElseThrow(() -> new RuntimeException("Centro de custo nao encontrado"))
                    : null;

            ContasPagar entity = new ContasPagar();
            entity.setEmpresa(empresa);
            entity.setFornecedor(fornecedor);
            entity.setCentroCusto(centroCusto);
            entity.setNumeroDocumento(data.numeroDocumento());
            entity.setDescricao(data.descricao());
            entity.setValorOriginal(data.valorOriginal());
            entity.setValorPago(data.valorPago());
            entity.setDataEmissao(data.dataEmissao());
            entity.setDataVencimento(data.dataVencimento());
            entity.setDataPagamento(data.dataPagamento());
            entity.setStatus(data.status());
            entity.setFormaPagamento(data.formaPagamento());
            entity.setCreatedAt(data.createdAt());

            ContasPagar saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ContasPagarResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateContasPagar(@PathVariable Integer id, @RequestBody ContasPagarRequestDTO data) {
        try {
            ContasPagar entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Conta a pagar nao encontrada"));

            Empresas empresa = data.empresa() != null
                    ? empresasRepository.findById(data.empresa())
                    .orElseThrow(() -> new RuntimeException("Empresa nao encontrada"))
                    : null;

            Parceiros fornecedor = data.fornecedor() != null
                    ? parceirosRepository.findById(data.fornecedor())
                    .orElseThrow(() -> new RuntimeException("Fornecedor nao encontrado"))
                    : null;

            CentrosCusto centroCusto = data.centroCusto() != null
                    ? centrosCustoRepository.findById(data.centroCusto())
                    .orElseThrow(() -> new RuntimeException("Centro de custo nao encontrado"))
                    : null;

            entity.setEmpresa(empresa);
            entity.setFornecedor(fornecedor);
            entity.setCentroCusto(centroCusto);
            entity.setNumeroDocumento(data.numeroDocumento());
            entity.setDescricao(data.descricao());
            entity.setValorOriginal(data.valorOriginal());
            entity.setValorPago(data.valorPago());
            entity.setDataEmissao(data.dataEmissao());
            entity.setDataVencimento(data.dataVencimento());
            entity.setDataPagamento(data.dataPagamento());
            entity.setStatus(data.status());
            entity.setFormaPagamento(data.formaPagamento());
            entity.setCreatedAt(data.createdAt());

            ContasPagar updated = repository.save(entity);
            return ResponseEntity.ok(new ContasPagarResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContasPagar(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Contas a Pagar deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}
