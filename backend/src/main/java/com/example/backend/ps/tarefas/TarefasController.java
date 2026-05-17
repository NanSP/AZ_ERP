package com.example.backend.ps.tarefas;

import com.example.backend.ps.projetos.Projetos;
import com.example.backend.ps.projetos.ProjetosRepository;
import com.example.backend.sys.usuarios.Usuarios;
import com.example.backend.sys.usuarios.UsuariosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ps/tarefas")
public class TarefasController {

    private final TarefasRepository repository;
    private final ProjetosRepository projetosRepository;
    private final UsuariosRepository usuariosRepository;

    public TarefasController(
            TarefasRepository repository,
            ProjetosRepository projetosRepository,
            UsuariosRepository usuariosRepository
    ) {
        this.repository = repository;
        this.projetosRepository = projetosRepository;
        this.usuariosRepository = usuariosRepository;
    }

    @GetMapping
    public List<TarefasResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(TarefasResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new TarefasResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveTarefas(@RequestBody TarefasRequestDTO data) {
        try {
            Projetos projeto = data.projeto() != null
                    ? projetosRepository.findById(data.projeto())
                    .orElseThrow(() -> new RuntimeException("Projeto nao encontrado"))
                    : null;

            Tarefas tarefaPai = data.tarefaPai() != null
                    ? repository.findById(data.tarefaPai())
                    .orElseThrow(() -> new RuntimeException("Tarefa pai nao encontrada"))
                    : null;

            Usuarios responsavel = data.responsavel() != null
                    ? usuariosRepository.findById(data.responsavel())
                    .orElseThrow(() -> new RuntimeException("Responsavel nao encontrado"))
                    : null;

            Tarefas entity = new Tarefas();
            entity.setProjeto(projeto);
            entity.setTarefaPai(tarefaPai);
            entity.setTitulo(data.titulo());
            entity.setDescricao(data.descricao());
            entity.setResponsavel(responsavel);
            entity.setDataInicio(data.dataInicio());
            entity.setDataFim(data.dataFim());
            entity.setHorasEstimadas(data.horasEstimadas());
            entity.setHorasRealizadas(data.horasRealizadas());
            entity.setPercentualConcluido(data.percentualConcluido());
            entity.setStatus(data.status());
            entity.setPrioridade(data.prioridade());
            entity.setCreatedAt(data.createdAt());

            Tarefas saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new TarefasResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTarefas(@PathVariable Integer id, @RequestBody TarefasRequestDTO data) {
        try {
            Tarefas entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Tarefa nao encontrada"));

            Projetos projeto = data.projeto() != null
                    ? projetosRepository.findById(data.projeto())
                    .orElseThrow(() -> new RuntimeException("Projeto nao encontrado"))
                    : null;

            Tarefas tarefaPai = data.tarefaPai() != null
                    ? repository.findById(data.tarefaPai())
                    .orElseThrow(() -> new RuntimeException("Tarefa pai nao encontrada"))
                    : null;

            Usuarios responsavel = data.responsavel() != null
                    ? usuariosRepository.findById(data.responsavel())
                    .orElseThrow(() -> new RuntimeException("Responsavel nao encontrado"))
                    : null;

            entity.setProjeto(projeto);
            entity.setTarefaPai(tarefaPai);
            entity.setTitulo(data.titulo());
            entity.setDescricao(data.descricao());
            entity.setResponsavel(responsavel);
            entity.setDataInicio(data.dataInicio());
            entity.setDataFim(data.dataFim());
            entity.setHorasEstimadas(data.horasEstimadas());
            entity.setHorasRealizadas(data.horasRealizadas());
            entity.setPercentualConcluido(data.percentualConcluido());
            entity.setStatus(data.status());
            entity.setPrioridade(data.prioridade());
            entity.setCreatedAt(data.createdAt());

            Tarefas updated = repository.save(entity);
            return ResponseEntity.ok(new TarefasResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTarefas(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Tarefa deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}