package com.example.backend.ps.recursosAlocados;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("com/example/backend/ps/recursosAlocados")
public class Controller {

    @Autowired
    private RecursosAlocadosRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<RecursosAlocadosResponseDTO> getAll(){

        List<RecursosAlocadosResponseDTO> recursosAlocadosList = repository.findAll().stream().map(RecursosAlocadosResponseDTO::new).toList();
        return recursosAlocadosList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<RecursosAlocados> recursosAlocados = repository.findById(id);
        if(recursosAlocados.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        RecursosAlocadosResponseDTO recursosAlocadosDTO = new RecursosAlocadosResponseDTO(recursosAlocados.get());
        return  ResponseEntity.ok(recursosAlocadosDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveRecursosAlocados(@RequestBody RecursosAlocadosRequestDTO data){

        RecursosAlocados recursosAlocadosData = new RecursosAlocados(data);
        repository.save(recursosAlocadosData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecursosAlocados(@PathVariable(value = "id") Integer id, @RequestBody RecursosAlocadosRequestDTO upData){

        Optional<RecursosAlocados> recursosAlocados = repository.findById(id);
        if(recursosAlocados.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        RecursosAlocados recursosAlocadosModel = recursosAlocados.get();
        BeanUtils.copyProperties(upData, recursosAlocadosModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(recursosAlocadosModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecursosAlocados(@PathVariable(value = "id") Integer id){

        Optional<RecursosAlocados> recursosAlocados = repository.findById(id);
        if(recursosAlocados.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(recursosAlocados.get());
        return  ResponseEntity.status(HttpStatus.OK).body("RecursosAlocados deleted");
    }
}
