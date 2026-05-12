package com.example.backend.sys.perfilPermissao;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sys/perfilPermissao")
public class PerfilPermissaoController {

    @Autowired
    private PerfilPermissaoRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<PerfilPermissaoResponseDTO> getAll(){

        List<PerfilPermissaoResponseDTO> perfilPermissaoList = repository.findAll().stream().map(PerfilPermissaoResponseDTO::new).toList();
        return perfilPermissaoList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<PerfilPermissao> perfilPermissao = repository.findById(id);
        if(perfilPermissao.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        PerfilPermissaoResponseDTO perfilPermissaoDTO = new PerfilPermissaoResponseDTO(perfilPermissao.get());
        return  ResponseEntity.ok(perfilPermissaoDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void savePerfilPermissao(@RequestBody PerfilPermissaoRequestDTO data){

        PerfilPermissao perfilPermissaoData = new PerfilPermissao(data);
        repository.save(perfilPermissaoData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePerfilPermissao(@PathVariable(value = "id") Integer id, @RequestBody PerfilPermissaoRequestDTO upData){

        Optional<PerfilPermissao> perfilPermissao = repository.findById(id);
        if(perfilPermissao.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        PerfilPermissao perfilPermissaoModel = perfilPermissao.get();
        BeanUtils.copyProperties(upData, perfilPermissaoModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(perfilPermissaoModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePerfilPermissao(@PathVariable(value = "id") Integer id){

        Optional<PerfilPermissao> perfilPermissao = repository.findById(id);
        if(perfilPermissao.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(perfilPermissao.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Perfil Permissão deleted");
    }
}
