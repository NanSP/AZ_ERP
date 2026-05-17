package com.example.backend.pp.apontamentos;

import com.example.backend.pp.ordemProducao.OrdemProducao;
import com.example.backend.pp.ordemProducao.OrdemProducaoRepository;
import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pp/apontamentos")
public class ApontamentosController {

    private final ApontamentosRepository repository;
    private final OrdemProducaoRepository ordemProducaoRepository;
    private final ColaboradoresRepository colaboradoresRepository;

    public ApontamentosController(
            ApontamentosRepository repository,
            OrdemProducaoRepository ordemProducaoRepository,
            ColaboradoresRepository colaboradoresRepository
    ) {
        this.repository = repository;
        this.ordemProducaoRepository = ordemProducaoRepository;
        this.colaboradoresRepository = colaboradoresRepository;
    }

    @GetMapping
    public List<ApontamentosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ApontamentosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new ApontamentosResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveApontamentos(@RequestBody ApontamentosRequestDTO data) {
        try {
            OrdemProducao op = data.op() != null
                    ? ordemProducaoRepository.findById(data.op())
                    .orElseThrow(() -> new RuntimeException("Ordem de producao nao encontrada"))
                    : null;

            Colaboradores operador = data.operador() != null
                    ? colaboradoresRepository.findById(data.operador())
                    .orElseThrow(() -> new RuntimeException("Operador nao encontrado"))
                    : null;

            Apontamentos entity = new Apontamentos();
            entity.setOp(op);
            entity.setMaquinaId(data.maquinaId());
            entity.setOperador(operador);
            entity.setDataHoraInicio(data.dataHoraInicio());
            entity.setDataHoraFim(data.dataHoraFim());
            entity.setQuantidadeProduzida(data.quantidadeProduzida());
            entity.setQuantidadeRefugo(data.quantidadeRefugo());
            entity.setTempoParado(data.tempoParado());
            entity.setObservacoes(data.observacoes());
            entity.setCreatedAt(data.createdAt());

            Apontamentos saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApontamentosResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateApontamentos(@PathVariable Integer id, @RequestBody ApontamentosRequestDTO data) {
        try {
            Apontamentos entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Apontamento nao encontrado"));

            OrdemProducao op = data.op() != null
                    ? ordemProducaoRepository.findById(data.op())
                    .orElseThrow(() -> new RuntimeException("Ordem de producao nao encontrada"))
                    : null;

            Colaboradores operador = data.operador() != null
                    ? colaboradoresRepository.findById(data.operador())
                    .orElseThrow(() -> new RuntimeException("Operador nao encontrado"))
                    : null;

            entity.setOp(op);
            entity.setMaquinaId(data.maquinaId());
            entity.setOperador(operador);
            entity.setDataHoraInicio(data.dataHoraInicio());
            entity.setDataHoraFim(data.dataHoraFim());
            entity.setQuantidadeProduzida(data.quantidadeProduzida());
            entity.setQuantidadeRefugo(data.quantidadeRefugo());
            entity.setTempoParado(data.tempoParado());
            entity.setObservacoes(data.observacoes());
            entity.setCreatedAt(data.createdAt());

            Apontamentos updated = repository.save(entity);
            return ResponseEntity.ok(new ApontamentosResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteApontamentos(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Apontamento deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}