package com.example.backend.sm.atendimentos;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("atendimentos")
public class AtendimentosController {

    @Autowired
    private AtendimentosRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<AtendimentosResponseDTO> getAll(){

        List<AtendimentosResponseDTO> atendimentosList = repository.findAll().stream().map(AtendimentosResponseDTO::new).toList();
        return atendimentosList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Atendimentos> atendimentos = repository.findById(id);
        if(atendimentos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        AtendimentosResponseDTO atendimentosDTO = new AtendimentosResponseDTO(atendimentos.get());
        return  ResponseEntity.ok(atendimentosDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveAtendimentos(@RequestBody AtendimentosRequestDTO data){

        Atendimentos atendimentosData = new Atendimentos(data);
        repository.save(atendimentosData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAtendimentos(@PathVariable(value = "id") Integer id, @RequestBody AtendimentosRequestDTO upData){

        Optional<Atendimentos> atendimentos = repository.findById(id);
        if(atendimentos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Atendimentos atendimentosModel = atendimentos.get();
        BeanUtils.copyProperties(upData, atendimentosModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(atendimentosModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAtendimentos(@PathVariable(value = "id") Integer id){

        Optional<Atendimentos> atendimentos = repository.findById(id);
        if(atendimentos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(atendimentos.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Atendimentos deleted");
    }
}
