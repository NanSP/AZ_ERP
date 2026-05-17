package com.example.backend.sd.contratos;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sd/contratos")
public class ContratosController {

    private final ContratosRepository repository;
    private final ParceirosRepository parceirosRepository;

    public ContratosController(
            ContratosRepository repository,
            ParceirosRepository parceirosRepository
    ) {
        this.repository = repository;
        this.parceirosRepository = parceirosRepository;
    }

    @GetMapping
    public List<ContratosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ContratosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new ContratosResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveContratos(@RequestBody ContratosRequestDTO data) {
        try {
            Parceiros cliente = data.cliente() != null
                    ? parceirosRepository.findById(data.cliente())
                    .orElseThrow(() -> new RuntimeException("Cliente nao encontrado"))
                    : null;

            Contratos entity = new Contratos();
            entity.setCliente(cliente);
            entity.setNumeroContrato(data.numeroContrato());
            entity.setObjeto(data.objeto());
            entity.setValorTotal(data.valorTotal());
            entity.setDataInicio(data.dataInicio());
            entity.setDataFim(data.dataFim());
            entity.setStatus(data.status());
            entity.setCreatedAt(data.createdAt());

            Contratos saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ContratosResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateContratos(@PathVariable Integer id, @RequestBody ContratosRequestDTO data) {
        try {
            Contratos entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Contrato nao encontrado"));

            Parceiros cliente = data.cliente() != null
                    ? parceirosRepository.findById(data.cliente())
                    .orElseThrow(() -> new RuntimeException("Cliente nao encontrado"))
                    : null;

            entity.setCliente(cliente);
            entity.setNumeroContrato(data.numeroContrato());
            entity.setObjeto(data.objeto());
            entity.setValorTotal(data.valorTotal());
            entity.setDataInicio(data.dataInicio());
            entity.setDataFim(data.dataFim());
            entity.setStatus(data.status());
            entity.setCreatedAt(data.createdAt());

            Contratos updated = repository.save(entity);
            return ResponseEntity.ok(new ContratosResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContratos(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Contrato deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}