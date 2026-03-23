package com.example.backend.fiscal.edcRegistros;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("com/example/backend/fiscal/edcRegistros")
public class Controller {

    @Autowired
    private EdcRegistrosRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<EdcRegistrosResponseDTO> getAll(){

        List<EdcRegistrosResponseDTO> edcRegistrosList = repository.findAll().stream().map(EdcRegistrosResponseDTO::new).toList();
        return edcRegistrosList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<EdcRegistros> edcRegistros = repository.findById(id);
        if(edcRegistros.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        EdcRegistrosResponseDTO edcRegistrosDTO = new EdcRegistrosResponseDTO(edcRegistros.get());
        return  ResponseEntity.ok(edcRegistrosDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveCompras(@RequestBody EdcRegistrosRequestDTO data){

        EdcRegistros edcRegistrosData = new EdcRegistros(data);
        repository.save(edcRegistrosData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEdcRegistros(@PathVariable(value = "id") Integer id, @RequestBody EdcRegistrosRequestDTO upData){

        Optional<EdcRegistros> edcRegistros = repository.findById(id);
        if(edcRegistros.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        EdcRegistros edcRegistrosModel = edcRegistros.get();
        BeanUtils.copyProperties(upData, edcRegistrosModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(edcRegistrosModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEdcRegistros(@PathVariable(value = "id") Integer id){

        Optional<EdcRegistros> edcRegistros = repository.findById(id);
        if(edcRegistros.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(edcRegistros.get());
        return  ResponseEntity.status(HttpStatus.OK).body("EdcRegistros deleted");
    }
}
