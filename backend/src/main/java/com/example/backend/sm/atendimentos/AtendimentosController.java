package com.example.backend.sm.atendimentos;

import com.example.backend.rh.colaboradores.Colaboradores;
import com.example.backend.rh.colaboradores.ColaboradoresRepository;
import com.example.backend.sm.ordensServico.OrdensServico;
import com.example.backend.sm.ordensServico.OrdensServicoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sm/atendimentos")
public class AtendimentosController {

    private final AtendimentosRepository repository;
    private final OrdensServicoRepository ordensServicoRepository;
    private final ColaboradoresRepository colaboradoresRepository;

    public AtendimentosController(
            AtendimentosRepository repository,
            OrdensServicoRepository ordensServicoRepository,
            ColaboradoresRepository colaboradoresRepository
    ) {
        this.repository = repository;
        this.ordensServicoRepository = ordensServicoRepository;
        this.colaboradoresRepository = colaboradoresRepository;
    }

    @GetMapping
    public List<AtendimentosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(AtendimentosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new AtendimentosResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveAtendimentos(@RequestBody AtendimentosRequestDTO data) {
        try {
            OrdensServico os = data.os() != null
                    ? ordensServicoRepository.findById(data.os())
                    .orElseThrow(() -> new RuntimeException("Ordem de servico nao encontrada"))
                    : null;

            Colaboradores tecnico = data.tecnico() != null
                    ? colaboradoresRepository.findById(data.tecnico())
                    .orElseThrow(() -> new RuntimeException("Tecnico nao encontrado"))
                    : null;

            Atendimentos entity = new Atendimentos();
            entity.setOs(os);
            entity.setTecnico(tecnico);
            entity.setDataHora(data.dataHora());
            entity.setDescricao(data.descricao());
            entity.setHorasGastas(data.horasGastas());
            entity.setMateriaisUtilizados(data.materiaisUtilizados());
            entity.setCreatedAt(data.createdAt());

            Atendimentos saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new AtendimentosResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAtendimentos(@PathVariable Integer id, @RequestBody AtendimentosRequestDTO data) {
        try {
            Atendimentos entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Atendimento nao encontrado"));

            OrdensServico os = data.os() != null
                    ? ordensServicoRepository.findById(data.os())
                    .orElseThrow(() -> new RuntimeException("Ordem de servico nao encontrada"))
                    : null;

            Colaboradores tecnico = data.tecnico() != null
                    ? colaboradoresRepository.findById(data.tecnico())
                    .orElseThrow(() -> new RuntimeException("Tecnico nao encontrado"))
                    : null;

            entity.setOs(os);
            entity.setTecnico(tecnico);
            entity.setDataHora(data.dataHora());
            entity.setDescricao(data.descricao());
            entity.setHorasGastas(data.horasGastas());
            entity.setMateriaisUtilizados(data.materiaisUtilizados());
            entity.setCreatedAt(data.createdAt());

            Atendimentos updated = repository.save(entity);
            return ResponseEntity.ok(new AtendimentosResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAtendimentos(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Atendimento deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}