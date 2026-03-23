package com.example.backend.grc.riscos;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("com/example/backend/grc/riscos")
public class Controller {

    @Autowired
    private RiscosRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<RiscosResponseDTO> getAll(){

        List<RiscosResponseDTO> riscosList = repository.findAll().stream().map(RiscosResponseDTO::new).toList();
        return riscosList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Riscos> riscos = repository.findById(id);
        if(riscos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        RiscosResponseDTO riscosDTO = new RiscosResponseDTO(riscos.get());
        return  ResponseEntity.ok(riscosDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveCompras(@RequestBody RiscosRequestDTO data){

        Riscos riscosData = new Riscos(data);
        repository.save(riscosData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRiscos(@PathVariable(value = "id") Integer id, @RequestBody RiscosRequestDTO upData){

        Optional<Riscos> riscos = repository.findById(id);
        if(riscos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Riscos riscosModel = riscos.get();
        BeanUtils.copyProperties(upData, riscosModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(riscosModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRiscos(@PathVariable(value = "id") Integer id){

        Optional<Riscos> riscos = repository.findById(id);
        if(riscos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(riscos.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Riscos deleted");
    }
}
