package com.example.backend.fi.contasPagar;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("com/example/backend/fi/contasPagar")
public class Controller {

    @Autowired
    private ContasPagarRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<ContasPagarResponseDTO> getAll(){

        List<ContasPagarResponseDTO> contasPagarList = repository.findAll().stream().map(ContasPagarResponseDTO::new).toList();
        return contasPagarList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<ContasPagar> contasPagar = repository.findById(id);
        if(contasPagar.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        ContasPagarResponseDTO contasPagarDTO = new ContasPagarResponseDTO(contasPagar.get());
        return  ResponseEntity.ok(contasPagarDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveContasPagar(@RequestBody ContasPagarRequestDTO data){

        ContasPagar contasPagarData = new ContasPagar(data);
        repository.save(contasPagarData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateContasPagar(@PathVariable(value = "id") Integer id, @RequestBody ContasPagarRequestDTO upData){

        Optional<ContasPagar> contasPagar = repository.findById(id);
        if(contasPagar.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        ContasPagar contasPagarModel = contasPagar.get();
        BeanUtils.copyProperties(upData, contasPagarModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(contasPagarModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContasPagar(@PathVariable(value = "id") Integer id){

        Optional<ContasPagar> contasPagar = repository.findById(id);
        if(contasPagar.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(contasPagar.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Contas a Pagar deleted");
    }
}
