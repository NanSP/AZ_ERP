package ps.tarefas;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("tarefas")
public class Controller {

    @Autowired
    private TarefasRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<TarefasResponseDTO> getAll(){

        List<TarefasResponseDTO> tarefasList = repository.findAll().stream().map(TarefasResponseDTO::new).toList();
        return tarefasList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Tarefas> tarefas = repository.findById(id);
        if(tarefas.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        TarefasResponseDTO tarefasDTO = new TarefasResponseDTO(tarefas.get());
        return  ResponseEntity.ok(tarefasDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveTarefas(@RequestBody TarefasRequestDTO data){

        Tarefas tarefasData = new Tarefas(data);
        repository.save(tarefasData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTarefas(@PathVariable(value = "id") Integer id, @RequestBody TarefasRequestDTO upData){

        Optional<Tarefas> tarefas = repository.findById(id);
        if(tarefas.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Tarefas tarefasModel = tarefas.get();
        BeanUtils.copyProperties(upData, tarefasModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(tarefasModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTarefas(@PathVariable(value = "id") Integer id){

        Optional<Tarefas> tarefas = repository.findById(id);
        if(tarefas.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(tarefas.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Tarefas deleted");
    }
}
