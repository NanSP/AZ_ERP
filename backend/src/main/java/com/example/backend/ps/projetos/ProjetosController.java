package com.example.backend.ps.projetos;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ps/projetos")
public class ProjetosController {

    private final ProjetosRepository repository;
    private final ParceirosRepository parceirosRepository;
    private final UsuariosRepository usuariosRepository;

    public ProjetosController(
            ProjetosRepository repository,
            ParceirosRepository parceirosRepository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.parceirosRepository = parceirosRepository;
        this.usuariosRepository = usuariosRepository;
    }

    @GetMapping
    public List<ProjetosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ProjetosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new ProjetosResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveProjetos(@RequestBody ProjetosRequestDTO data) {
        try {
            Parceiros cliente = data.cliente() != null
                    ? parceirosRepository.findById(data.cliente())
                    .orElseThrow(() -> new RuntimeException("Cliente nao encontrado"))
                    : null;

            Usuarios gerente = data.gerente() != null
                    ? usuariosRepository.findById(data.gerente())
                    .orElseThrow(() -> new RuntimeException("Gerente nao encontrado"))
                    : null;

            Projetos entity = new Projetos();
            entity.setCodigo(data.codigo());
            entity.setNome(data.nome());
            entity.setDescricao(data.descricao());
            entity.setCliente(cliente);
            entity.setGerente(gerente);
            entity.setDataInicio(data.dataInicio());
            entity.setDataFim(data.dataFim());
            entity.setDataPrevistaInicio(data.dataPrevistaInicio());
            entity.setDataPrevistaFim(data.dataPrevistaFim());
            entity.setOrcamentoTotal(data.orcamentoTotal());
            entity.setOrcamentoGasto(data.orcamentoGasto());
            entity.setStatus(data.status());
            entity.setPrioridade(data.prioridade());
            entity.setCreatedAt(data.createdAt());

            Projetos saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ProjetosResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProjetos(@PathVariable Integer id, @RequestBody ProjetosRequestDTO data) {
        try {
            Projetos entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Projeto nao encontrado"));

            Parceiros cliente = data.cliente() != null
                    ? parceirosRepository.findById(data.cliente())
                    .orElseThrow(() -> new RuntimeException("Cliente nao encontrado"))
                    : null;

            Usuarios gerente = data.gerente() != null
                    ? usuariosRepository.findById(data.gerente())
                    .orElseThrow(() -> new RuntimeException("Gerente nao encontrado"))
                    : null;

            entity.setCodigo(data.codigo());
            entity.setNome(data.nome());
            entity.setDescricao(data.descricao());
            entity.setCliente(cliente);
            entity.setGerente(gerente);
            entity.setDataInicio(data.dataInicio());
            entity.setDataFim(data.dataFim());
            entity.setDataPrevistaInicio(data.dataPrevistaInicio());
            entity.setDataPrevistaFim(data.dataPrevistaFim());
            entity.setOrcamentoTotal(data.orcamentoTotal());
            entity.setOrcamentoGasto(data.orcamentoGasto());
            entity.setStatus(data.status());
            entity.setPrioridade(data.prioridade());
            entity.setCreatedAt(data.createdAt());

            Projetos updated = repository.save(entity);
            return ResponseEntity.ok(new ProjetosResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProjetos(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Projeto deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}