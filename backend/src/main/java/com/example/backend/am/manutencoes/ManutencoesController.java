package com.example.backend.am.manutencoes;

import com.example.backend.am.bensPatrimoniais.BensPatrimoniais;
import com.example.backend.am.bensPatrimoniais.BensPatrimoniaisRepository;
import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/am/manutencoes")
public class ManutencoesController {

    private final ManutencoesRepository repository;
    private final BensPatrimoniaisRepository bensPatrimoniaisRepository;
    private final ColaboradoresRepository colaboradoresRepository;

    public ManutencoesController(
            ManutencoesRepository repository,
            BensPatrimoniaisRepository bensPatrimoniaisRepository,
            ColaboradoresRepository colaboradoresRepository
    ) {
        this.repository = repository;
        this.bensPatrimoniaisRepository = bensPatrimoniaisRepository;
        this.colaboradoresRepository = colaboradoresRepository;
    }

    @GetMapping
    public List<ManutencoesResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ManutencoesResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new ManutencoesResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveManutencoes(@RequestBody ManutencoesRequestDTO data) {
        try {
            BensPatrimoniais ativo = data.ativo() != null
                    ? bensPatrimoniaisRepository.findById(data.ativo())
                    .orElseThrow(() -> new RuntimeException("Ativo nao encontrado"))
                    : null;

            Colaboradores tecnico = data.tecnico() != null
                    ? colaboradoresRepository.findById(data.tecnico())
                    .orElseThrow(() -> new RuntimeException("Tecnico nao encontrado"))
                    : null;

            Manutencoes entity = new Manutencoes();
            entity.setAtivo(ativo);
            entity.setTipoManutencao(data.tipoManutencao());
            entity.setDataSolicitacao(data.dataSolicitacao());
            entity.setDataExecucao(data.dataExecucao());
            entity.setDescricao(data.descricao());
            entity.setCustoMaoObra(data.custoMaoObra());
            entity.setCustoMaterial(data.custoMaterial());
            entity.setCustoTotal(data.custoTotal());
            entity.setTecnico(tecnico);
            entity.setCreatedAt(data.createdAt());

            Manutencoes saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ManutencoesResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateManutencoes(@PathVariable Integer id, @RequestBody ManutencoesRequestDTO data) {
        try {
            Manutencoes entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Manutencao nao encontrada"));

            BensPatrimoniais ativo = data.ativo() != null
                    ? bensPatrimoniaisRepository.findById(data.ativo())
                    .orElseThrow(() -> new RuntimeException("Ativo nao encontrado"))
                    : null;

            Colaboradores tecnico = data.tecnico() != null
                    ? colaboradoresRepository.findById(data.tecnico())
                    .orElseThrow(() -> new RuntimeException("Tecnico nao encontrado"))
                    : null;

            entity.setAtivo(ativo);
            entity.setTipoManutencao(data.tipoManutencao());
            entity.setDataSolicitacao(data.dataSolicitacao());
            entity.setDataExecucao(data.dataExecucao());
            entity.setDescricao(data.descricao());
            entity.setCustoMaoObra(data.custoMaoObra());
            entity.setCustoMaterial(data.custoMaterial());
            entity.setCustoTotal(data.custoTotal());
            entity.setTecnico(tecnico);
            entity.setCreatedAt(data.createdAt());

            Manutencoes updated = repository.save(entity);
            return ResponseEntity.ok(new ManutencoesResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteManutencoes(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Manutencao deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}