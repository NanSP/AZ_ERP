package com.example.backend.qm.inspecoes;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/qm/inspecoes")
public class InspecoesController {

    @Autowired
    private InspecoesRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<InspecoesResponseDTO> getAll(){

        List<InspecoesResponseDTO> inspecoesList = repository.findAll().stream().map(InspecoesResponseDTO::new).toList();
        return inspecoesList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Inspecoes> inspecoes = repository.findById(id);
        if(inspecoes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        InspecoesResponseDTO inspecoesDTO = new InspecoesResponseDTO(inspecoes.get());
        return  ResponseEntity.ok(inspecoesDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveInspecoes(@RequestBody InspecoesRequestDTO data){

        Inspecoes inspecoesData = new Inspecoes(data);
        repository.save(inspecoesData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInspecoes(@PathVariable(value = "id") Integer id, @RequestBody InspecoesRequestDTO upData){

        Optional<Inspecoes> inspecoes = repository.findById(id);
        if(inspecoes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Inspecoes inspecoesModel = inspecoes.get();
        BeanUtils.copyProperties(upData, inspecoesModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(inspecoesModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInspecoes(@PathVariable(value = "id") Integer id){

        Optional<Inspecoes> inspecoes = repository.findById(id);
        if(inspecoes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(inspecoes.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Inspecoes deleted");
    }
}
