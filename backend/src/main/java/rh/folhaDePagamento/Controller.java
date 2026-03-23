package rh.folhaDePagamento;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("rh/folha_pagamento")
public class Controller {

    @Autowired
    private FolhaDePagamentoRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<FolhaDePagamentoResponseDTO> getAll(){

        List<FolhaDePagamentoResponseDTO> folhaDePagamentoList = repository.findAll().stream().map(FolhaDePagamentoResponseDTO::new).toList();
        return folhaDePagamentoList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<FolhaDePagamento> folhaDePagamento = repository.findById(id);
        if(folhaDePagamento.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        FolhaDePagamentoResponseDTO folhaDePagamentoDTO = new FolhaDePagamentoResponseDTO(folhaDePagamento.get());
        return  ResponseEntity.ok(folhaDePagamentoDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveFolhaDePagamento(@RequestBody FolhaDePagamentoRequestDTO data){

        FolhaDePagamento folhaDePagamentoData = new FolhaDePagamento(data);
        repository.save(folhaDePagamentoData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFolhaDePagamento(@PathVariable(value = "id") Integer id, @RequestBody FolhaDePagamentoRequestDTO upData){

        Optional<FolhaDePagamento> folhaDePagamento = repository.findById(id);
        if(folhaDePagamento.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        FolhaDePagamento folhaDePagamentoModel = folhaDePagamento.get();
        BeanUtils.copyProperties(upData, folhaDePagamentoModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(folhaDePagamentoModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFolhaDePagamento(@PathVariable(value = "id") Integer id){

        Optional<FolhaDePagamento> folhaDePagamento = repository.findById(id);
        if(folhaDePagamento.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(folhaDePagamento.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Pagamento deleted");
    }
}
