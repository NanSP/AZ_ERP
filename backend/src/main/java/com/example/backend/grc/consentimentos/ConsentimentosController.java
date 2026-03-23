package com.example.backend.grc.consentimentos;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/grc/consentimentos")
public class ConsentimentosController {

    @Autowired
    private ConsentimentosRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<ConsentimentosResponseDTO> getAll(){

        List<ConsentimentosResponseDTO> consentimentosList = repository.findAll().stream().map(ConsentimentosResponseDTO::new).toList();
        return consentimentosList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Consentimentos> consentimentos = repository.findById(id);
        if(consentimentos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        ConsentimentosResponseDTO consentimentosDTO = new ConsentimentosResponseDTO(consentimentos.get());
        return  ResponseEntity.ok(consentimentosDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveCompras(@RequestBody ConsentimentosRequestDTO data){

        Consentimentos consentimentosData = new Consentimentos(data);
        repository.save(consentimentosData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateConsentimentos(@PathVariable(value = "id") Integer id, @RequestBody ConsentimentosRequestDTO upData){

        Optional<Consentimentos> consentimentos = repository.findById(id);
        if(consentimentos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Consentimentos consentimentosModel = consentimentos.get();
        BeanUtils.copyProperties(upData, consentimentosModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(consentimentosModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteConsentimentos(@PathVariable(value = "id") Integer id){

        Optional<Consentimentos> consentimentos = repository.findById(id);
        if(consentimentos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(consentimentos.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Consentimentos deleted");
    }
}
