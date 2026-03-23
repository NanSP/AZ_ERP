package com.example.backend.auditoria.logAcoes;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auditoria/logAcoes")
public class LogAcoesController {

    @Autowired
    private LogAcoesRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<LogAcoesResponseDTO> getAll(){

        List<LogAcoesResponseDTO> logAcoesList = repository.findAll().stream().map(LogAcoesResponseDTO::new).toList();
        return logAcoesList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<LogAcoes> logAcoes = repository.findById(id);
        if(logAcoes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        LogAcoesResponseDTO logAcoesDTO = new LogAcoesResponseDTO(logAcoes.get());
        return  ResponseEntity.ok(logAcoesDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveCompras(@RequestBody LogAcoesRequestDTO data){

        LogAcoes logAcoesData = new LogAcoes(data);
        repository.save(logAcoesData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLogAcoes(@PathVariable(value = "id") Integer id, @RequestBody LogAcoesRequestDTO upData){

        Optional<LogAcoes> logAcoes = repository.findById(id);
        if(logAcoes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        LogAcoes logAcoesModel = logAcoes.get();
        BeanUtils.copyProperties(upData, logAcoesModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(logAcoesModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLogAcoes(@PathVariable(value = "id") Integer id){

        Optional<LogAcoes> logAcoes = repository.findById(id);
        if(logAcoes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(logAcoes.get());
        return  ResponseEntity.status(HttpStatus.OK).body("LogAcoes deleted");
    }
}
