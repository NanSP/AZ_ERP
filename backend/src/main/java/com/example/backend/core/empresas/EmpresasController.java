package com.example.backend.core.empresas;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/core/empresas")
public class EmpresasController {

    private final EmpresasRepository repository;

    public EmpresasController(EmpresasRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<EmpresasResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(EmpresasResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new EmpresasResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveEmpresa(@RequestBody EmpresasRequestDTO data) {
        Empresas entity = new Empresas(data);
        Empresas saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(new EmpresasResponseDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmpresa(@PathVariable Integer id, @RequestBody EmpresasRequestDTO data) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    entity.setCodigo(data.codigo());
                    entity.setRazaoSocial(data.razaoSocial());
                    entity.setNomeFantasia(data.nomeFantasia());
                    entity.setCnpj(data.cnpj());
                    entity.setInscricaoEstadual(data.inscricaoEstadual());
                    entity.setInscricaoMunicipal(data.inscricaoMunicipal());
                    entity.setRegimeTributario(data.regimeTributario());
                    entity.setDataFundacao(data.dataFundacao());
                    entity.setSituacao(data.situacao());
                    entity.setCreatedAt(data.createdAt());

                    Empresas updated = repository.save(entity);
                    return ResponseEntity.ok(new EmpresasResponseDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmpresa(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Empresa deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}