package com.example.backend.ps.recursosAlocados;

import com.example.backend.ps.projetos.Projetos;
import com.example.backend.ps.projetos.ProjetosRepository;
import com.example.backend.ps.tarefas.Tarefas;
import com.example.backend.ps.tarefas.TarefasRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ps/recursosAlocados")
public class RecursosAlocadosController {

    private final RecursosAlocadosRepository repository;
    private final ProjetosRepository projetosRepository;
    private final TarefasRepository tarefasRepository;

    public RecursosAlocadosController(
            RecursosAlocadosRepository repository,
            ProjetosRepository projetosRepository,
            TarefasRepository tarefasRepository
    ) {
        this.repository = repository;
        this.projetosRepository = projetosRepository;
        this.tarefasRepository = tarefasRepository;
    }

    @GetMapping
    public List<RecursosAlocadosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(RecursosAlocadosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new RecursosAlocadosResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveRecursosAlocados(@RequestBody RecursosAlocadosRequestDTO data) {
        try {
            Projetos projeto = data.projeto() != null
                    ? projetosRepository.findById(data.projeto())
                    .orElseThrow(() -> new RuntimeException("Projeto nao encontrado"))
                    : null;

            Tarefas tarefa = data.tarefa() != null
                    ? tarefasRepository.findById(data.tarefa())
                    .orElseThrow(() -> new RuntimeException("Tarefa nao encontrada"))
                    : null;

            RecursosAlocados entity = new RecursosAlocados();
            entity.setProjeto(projeto);
            entity.setTarefa(tarefa);
            entity.setTipoRecurso(data.tipoRecurso());
            entity.setRecursoId(data.recursoId());
            entity.setQuantidade(data.quantidade());
            entity.setValorUnitario(data.valorUnitario());
            entity.setValorTotal(data.valorTotal());
            entity.setDataAlocacao(data.dataAlocacao());
            entity.setCreatedAt(data.createdAt());

            RecursosAlocados saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new RecursosAlocadosResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecursosAlocados(@PathVariable Integer id, @RequestBody RecursosAlocadosRequestDTO data) {
        try {
            RecursosAlocados entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Recurso alocado nao encontrado"));

            Projetos projeto = data.projeto() != null
                    ? projetosRepository.findById(data.projeto())
                    .orElseThrow(() -> new RuntimeException("Projeto nao encontrado"))
                    : null;

            Tarefas tarefa = data.tarefa() != null
                    ? tarefasRepository.findById(data.tarefa())
                    .orElseThrow(() -> new RuntimeException("Tarefa nao encontrada"))
                    : null;

            entity.setProjeto(projeto);
            entity.setTarefa(tarefa);
            entity.setTipoRecurso(data.tipoRecurso());
            entity.setRecursoId(data.recursoId());
            entity.setQuantidade(data.quantidade());
            entity.setValorUnitario(data.valorUnitario());
            entity.setValorTotal(data.valorTotal());
            entity.setDataAlocacao(data.dataAlocacao());
            entity.setCreatedAt(data.createdAt());

            RecursosAlocados updated = repository.save(entity);
            return ResponseEntity.ok(new RecursosAlocadosResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecursosAlocados(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Recurso alocado deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}