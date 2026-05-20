package com.example.backend.core.enderecos;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/core/enderecos")
public class EnderecosController {

    private final EnderecosRepository repository;

    public EnderecosController(EnderecosRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<EnderecosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(EnderecosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new EnderecosResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveEndereco(@RequestBody EnderecosRequestDTO data) {
        Enderecos entity = new Enderecos(data);
        Enderecos saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(new EnderecosResponseDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEndereco(@PathVariable Integer id, @RequestBody EnderecosRequestDTO data) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    entity.setEntidadeTipo(data.entidadeTipo());
                    entity.setEntidadeId(data.entidadeId());
                    entity.setTipoEndereco(data.tipoEndereco());
                    entity.setLogradouro(data.logradouro());
                    entity.setNumero(data.numero());
                    entity.setComplemento(data.complemento());
                    entity.setBairro(data.bairro());
                    entity.setCidade(data.cidade());
                    entity.setUf(data.uf());
                    entity.setCep(data.cep());
                    entity.setPais(data.pais());
                    entity.setPrincipal(data.principal());
                    entity.setCreatedAt(data.createdAt());

                    Enderecos updated = repository.save(entity);
                    return ResponseEntity.ok(new EnderecosResponseDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEndereco(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Endereco deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}