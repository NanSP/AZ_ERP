package com.example.backend.sys.permissoes;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("permissoes")
public class Controller {

    @Autowired
    private PermissoesRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<PermissoesResponseDTO> getAll(){

        List<PermissoesResponseDTO> permissoesList = repository.findAll().stream().map(PermissoesResponseDTO::new).toList();
        return permissoesList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Permissoes> permissoes = repository.findById(id);
        if(permissoes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        PermissoesResponseDTO permissoesDTO = new PermissoesResponseDTO(permissoes.get());
        return  ResponseEntity.ok(permissoesDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void savePermissoes(@RequestBody PermissoesRequestDTO data){

        Permissoes permissoesData = new Permissoes(data);
        repository.save(permissoesData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePermissoes(@PathVariable(value = "id") Integer id, @RequestBody PermissoesRequestDTO upData){

        Optional<Permissoes> permissoes = repository.findById(id);
        if(permissoes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Permissoes permissoesModel = permissoes.get();
        BeanUtils.copyProperties(upData, permissoesModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(permissoesModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePermissoes(@PathVariable(value = "id") Integer id){

        Optional<Permissoes> permissoes = repository.findById(id);
        if(permissoes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(permissoes.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Permissão deleted");
    }
}
