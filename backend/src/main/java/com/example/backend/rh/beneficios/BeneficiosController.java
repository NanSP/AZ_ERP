package com.example.backend.rh.beneficios;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rh/beneficios")
public class BeneficiosController {

    @Autowired
    private BeneficiosRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<BeneficiosResponseDTO> getAll(){

        List<BeneficiosResponseDTO> beneficiosList = repository.findAll().stream().map(BeneficiosResponseDTO::new).toList();
        return beneficiosList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Beneficios> beneficios = repository.findById(id);
        if(beneficios.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        BeneficiosResponseDTO beneficiosDTO = new BeneficiosResponseDTO(beneficios.get());
        return  ResponseEntity.ok(beneficiosDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveBeneficio(@RequestBody BeneficiosRequestDTO data){

        Beneficios beneficioData = new Beneficios(data);
        repository.save(beneficioData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBeneficio(@PathVariable(value = "id") Integer id, @RequestBody BeneficiosRequestDTO upData){

        Optional<Beneficios> beneficios = repository.findById(id);
        if(beneficios.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Beneficios beneficioModel = beneficios.get();
        BeanUtils.copyProperties(upData, beneficioModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(beneficioModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBeneficio(@PathVariable(value = "id") Integer id){

        Optional<Beneficios> beneficios = repository.findById(id);
        if(beneficios.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(beneficios.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Beneficio deleted");
    }
}
