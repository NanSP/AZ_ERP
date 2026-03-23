package com.example.backend.sd.oportunidades;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("oportunidades")
public class Controller {

    @Autowired
    private OportunidadesRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<OportunidadesResponseDTO> getAll(){

        List<OportunidadesResponseDTO> oportunidadesList = repository.findAll().stream().map(OportunidadesResponseDTO::new).toList();
        return oportunidadesList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Oportunidades> oportunidades = repository.findById(id);
        if(oportunidades.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        OportunidadesResponseDTO oportunidadesDTO = new OportunidadesResponseDTO(oportunidades.get());
        return  ResponseEntity.ok(oportunidadesDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveOportunidades(@RequestBody OportunidadesRequestDTO data){

        Oportunidades oportunidadesData = new Oportunidades(data);
        repository.save(oportunidadesData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOportunidades(@PathVariable(value = "id") Integer id, @RequestBody OportunidadesRequestDTO upData){

        Optional<Oportunidades> oportunidades = repository.findById(id);
        if(oportunidades.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Oportunidades oportunidadesModel = oportunidades.get();
        BeanUtils.copyProperties(upData, oportunidadesModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(oportunidadesModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOportunidades(@PathVariable(value = "id") Integer id){

        Optional<Oportunidades> oportunidades = repository.findById(id);
        if(oportunidades.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(oportunidades.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Oportunidade deleted");
    }
}
