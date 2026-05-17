package com.example.backend.sd.clientes;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sd/clientes")
public class ClientesController {

    private final ClientesRepository repository;
    private final ParceirosRepository parceirosRepository;

    public ClientesController(
            ClientesRepository repository,
            ParceirosRepository parceirosRepository
    ) {
        this.repository = repository;
        this.parceirosRepository = parceirosRepository;
    }

    @GetMapping
    public List<ClientesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ClientesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new ClientesResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveClientes(@RequestBody ClientesRequestDTO data) {
        try {
            Parceiros parceiro = data.parceiro() != null
                    ? parceirosRepository.findById(data.parceiro())
                    .orElseThrow(() -> new RuntimeException("Parceiro nao encontrado"))
                    : null;

            Clientes entity = new Clientes();
            entity.setParceiro(parceiro);
            entity.setClassificacao(data.classificacao());
            entity.setOrigem(data.origem());
            entity.setWebsite(data.website());
            entity.setFaturamentoAnual(data.faturamentoAnual());
            entity.setNumeroFuncionarios(data.numeroFuncionarios());
            entity.setCreatedAt(data.createdAt());

            Clientes saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ClientesResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateClientes(@PathVariable Integer id, @RequestBody ClientesRequestDTO data) {
        try {
            Clientes entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cliente nao encontrado"));

            Parceiros parceiro = data.parceiro() != null
                    ? parceirosRepository.findById(data.parceiro())
                    .orElseThrow(() -> new RuntimeException("Parceiro nao encontrado"))
                    : null;

            entity.setParceiro(parceiro);
            entity.setClassificacao(data.classificacao());
            entity.setOrigem(data.origem());
            entity.setWebsite(data.website());
            entity.setFaturamentoAnual(data.faturamentoAnual());
            entity.setNumeroFuncionarios(data.numeroFuncionarios());
            entity.setCreatedAt(data.createdAt());

            Clientes updated = repository.save(entity);
            return ResponseEntity.ok(new ClientesResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClientes(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Cliente deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}