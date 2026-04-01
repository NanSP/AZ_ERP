package com.example.backend.master.platform.systemUsers;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/platform/systemUsers")
public class SystemUsersController {

    @Autowired
    private SystemUsersRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<SystemUsersResponseDTO> getAll(){

        List<SystemUsersResponseDTO> systemUsersList = repository.findAll().stream().map(SystemUsersResponseDTO::new).toList();
        return systemUsersList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<SystemUsers> systemUsers = repository.findById(Long.valueOf(id));
        if(systemUsers.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        SystemUsersResponseDTO systemUsersDTO = new SystemUsersResponseDTO(systemUsers.get());
        return  ResponseEntity.ok(systemUsersDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveSystemUsers(@RequestBody SystemUsersRequestDTO data){

        SystemUsers systemUsersData = new SystemUsers(data);
        repository.save(systemUsersData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSystemUsers(@PathVariable(value = "id") Integer id, @RequestBody SystemUsersRequestDTO upData){

        Optional<SystemUsers> systemUsers = repository.findById(Long.valueOf(id));
        if(systemUsers.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        SystemUsers systemUsersModel = systemUsers.get();
        BeanUtils.copyProperties(upData, systemUsersModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(systemUsersModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSystemUsers(@PathVariable(value = "id") Integer id){

        Optional<SystemUsers> systemUsers = repository.findById(Long.valueOf(id));
        if(systemUsers.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(systemUsers.get());
        return  ResponseEntity.status(HttpStatus.OK).body("SystemUsers deleted");
    }
}
