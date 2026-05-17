package com.example.backend.qm.naoConformidade;

import com.example.backend.qm.inspecoes.Inspecoes;
import com.example.backend.qm.inspecoes.InspecoesRepository;
import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/qm/naoConformidade")
public class NaoConformidadeController {

    private final NaoConformidadeRepository repository;
    private final InspecoesRepository inspecoesRepository;
    private final ColaboradoresRepository colaboradoresRepository;

    public NaoConformidadeController(
            NaoConformidadeRepository repository,
            InspecoesRepository inspecoesRepository,
            ColaboradoresRepository colaboradoresRepository
    ) {
        this.repository = repository;
        this.inspecoesRepository = inspecoesRepository;
        this.colaboradoresRepository = colaboradoresRepository;
    }

    @GetMapping
    public List<NaoConformidadeResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(NaoConformidadeResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new NaoConformidadeResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveNaoConformidade(@RequestBody NaoConformidadeRequestDTO data) {
        try {
            Inspecoes inspecao = data.inspecao() != null
                    ? inspecoesRepository.findById(data.inspecao())
                    .orElseThrow(() -> new RuntimeException("Inspecao nao encontrada"))
                    : null;

            Colaboradores responsavel = data.responsavel() != null
                    ? colaboradoresRepository.findById(data.responsavel())
                    .orElseThrow(() -> new RuntimeException("Responsavel nao encontrado"))
                    : null;

            NaoConformidade entity = new NaoConformidade();
            entity.setInspecao(inspecao);
            entity.setTipoNaoConformidade(data.tipoNaoConformidade());
            entity.setDescricao(data.descricao());
            entity.setCausaRaiz(data.causaRaiz());
            entity.setAcaoImediata(data.acaoImediata());
            entity.setAcaoCorretiva(data.acaoCorretiva());
            entity.setResponsavel(responsavel);
            entity.setDataIdentificacao(data.dataIdentificacao());
            entity.setDataResolucao(data.dataResolucao());
            entity.setStatus(data.status());
            entity.setCreatedAt(data.createdAt());

            NaoConformidade saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new NaoConformidadeResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNaoConformidade(@PathVariable Integer id, @RequestBody NaoConformidadeRequestDTO data) {
        try {
            NaoConformidade entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Nao conformidade nao encontrada"));

            Inspecoes inspecao = data.inspecao() != null
                    ? inspecoesRepository.findById(data.inspecao())
                    .orElseThrow(() -> new RuntimeException("Inspecao nao encontrada"))
                    : null;

            Colaboradores responsavel = data.responsavel() != null
                    ? colaboradoresRepository.findById(data.responsavel())
                    .orElseThrow(() -> new RuntimeException("Responsavel nao encontrado"))
                    : null;

            entity.setInspecao(inspecao);
            entity.setTipoNaoConformidade(data.tipoNaoConformidade());
            entity.setDescricao(data.descricao());
            entity.setCausaRaiz(data.causaRaiz());
            entity.setAcaoImediata(data.acaoImediata());
            entity.setAcaoCorretiva(data.acaoCorretiva());
            entity.setResponsavel(responsavel);
            entity.setDataIdentificacao(data.dataIdentificacao());
            entity.setDataResolucao(data.dataResolucao());
            entity.setStatus(data.status());
            entity.setCreatedAt(data.createdAt());

            NaoConformidade updated = repository.save(entity);
            return ResponseEntity.ok(new NaoConformidadeResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNaoConformidade(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Nao conformidade deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}