package com.example.backend.am.manutencoes;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("manutencoes")
public class Controller {

    @Autowired
    private ManutencoesRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<ManutencoesResponseDTO> getAll(){

        List<ManutencoesResponseDTO> manutencoesList = repository.findAll().stream().map(ManutencoesResponseDTO::new).toList();
        return manutencoesList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Manutencoes> manutencoes = repository.findById(id);
        if(manutencoes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        ManutencoesResponseDTO manutencoesDTO = new ManutencoesResponseDTO(manutencoes.get());
        return  ResponseEntity.ok(manutencoesDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveCompras(@RequestBody ManutencoesRequestDTO data){

        Manutencoes manutencoesData = new Manutencoes(data);
        repository.save(manutencoesData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateManutencoes(@PathVariable(value = "id") Integer id, @RequestBody ManutencoesRequestDTO upData){

        Optional<Manutencoes> manutencoes = repository.findById(id);
        if(manutencoes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Manutencoes manutencoesModel = manutencoes.get();
        BeanUtils.copyProperties(upData, manutencoesModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(manutencoesModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteManutencoes(@PathVariable(value = "id") Integer id){

        Optional<Manutencoes> manutencoes = repository.findById(id);
        if(manutencoes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(manutencoes.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Manutencoes deleted");
    }
}
