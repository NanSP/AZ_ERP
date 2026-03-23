package com.example.backend.ps.projetos;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ps/projetos")
public class ProjetosController {

    @Autowired
    private ProjetosRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<ProjetosResponseDTO> getAll(){

        List<ProjetosResponseDTO> projetosList = repository.findAll().stream().map(ProjetosResponseDTO::new).toList();
        return projetosList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Projetos> projetos = repository.findById(id);
        if(projetos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        ProjetosResponseDTO projetosDTO = new ProjetosResponseDTO(projetos.get());
        return  ResponseEntity.ok(projetosDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveProjetos(@RequestBody ProjetosRequestDTO data){

        Projetos projetosData = new Projetos(data);
        repository.save(projetosData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProjetos(@PathVariable(value = "id") Integer id, @RequestBody ProjetosRequestDTO upData){

        Optional<Projetos> projetos = repository.findById(id);
        if(projetos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Projetos projetosModel = projetos.get();
        BeanUtils.copyProperties(upData, projetosModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(projetosModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProjetos(@PathVariable(value = "id") Integer id){

        Optional<Projetos> projetos = repository.findById(id);
        if(projetos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(projetos.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Projetos deleted");
    }
}
