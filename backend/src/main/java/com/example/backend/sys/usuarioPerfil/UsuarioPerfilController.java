package com.example.backend.sys.usuarioPerfil;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sys/usuarioPerfil")
public class UsuarioPerfilController {

    @Autowired
    private UsuarioPerfilRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<UsuarioPerfilResponseDTO> getAll(){

        List<UsuarioPerfilResponseDTO> usuarioPerfilList = repository.findAll().stream().map(UsuarioPerfilResponseDTO::new).toList();
        return usuarioPerfilList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<UsuarioPerfil> usuarioPerfil = repository.findById(id);
        if(usuarioPerfil.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        UsuarioPerfilResponseDTO usuarioPerfilDTO = new UsuarioPerfilResponseDTO(usuarioPerfil.get());
        return  ResponseEntity.ok(usuarioPerfilDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveUsuarioPerfil(@RequestBody UsuarioPerfilRequestDTO data){

        UsuarioPerfil usuarioPerfilData = new UsuarioPerfil(data);
        repository.save(usuarioPerfilData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuarioPerfil(@PathVariable(value = "id") Integer id, @RequestBody UsuarioPerfilRequestDTO upData){

        Optional<UsuarioPerfil> usuarioPerfil = repository.findById(id);
        if(usuarioPerfil.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        UsuarioPerfil usuarioPerfilModel = usuarioPerfil.get();
        BeanUtils.copyProperties(upData, usuarioPerfilModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(usuarioPerfilModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuarioPerfil(@PathVariable(value = "id") Integer id){

        Optional<UsuarioPerfil> usuarioPerfil = repository.findById(id);
        if(usuarioPerfil.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(usuarioPerfil.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Usuario Perfil deleted");
    }
}
