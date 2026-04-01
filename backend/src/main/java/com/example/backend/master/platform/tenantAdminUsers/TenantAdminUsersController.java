package com.example.backend.master.platform.tenantAdminUsers;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/platform/tenantAdminUsers")
public class TenantAdminUsersController {

    @Autowired
    private TenantAdminUsersRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<TenantAdminUsersResponseDTO> getAll(){

        List<TenantAdminUsersResponseDTO> tenantAdminUsersList = repository.findAll().stream().map(TenantAdminUsersResponseDTO::new).toList();
        return tenantAdminUsersList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<TenantAdminUsers> tenantAdminUsers = repository.findById(Long.valueOf(id));
        if(tenantAdminUsers.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        TenantAdminUsersResponseDTO tenantAdminUsersDTO = new TenantAdminUsersResponseDTO(tenantAdminUsers.get());
        return  ResponseEntity.ok(tenantAdminUsersDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveTenantAdminUsers(@RequestBody TenantAdminUsersRequestDTO data){

        TenantAdminUsers tenantAdminUsersData = new TenantAdminUsers(data);
        repository.save(tenantAdminUsersData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTenantAdminUsers(@PathVariable(value = "id") Integer id, @RequestBody TenantAdminUsersRequestDTO upData){

        Optional<TenantAdminUsers> tenantAdminUsers = repository.findById(Long.valueOf(id));
        if(tenantAdminUsers.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        TenantAdminUsers tenantAdminUsersModel = tenantAdminUsers.get();
        BeanUtils.copyProperties(upData, tenantAdminUsersModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(tenantAdminUsersModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTenantAdminUsers(@PathVariable(value = "id") Integer id){

        Optional<TenantAdminUsers> tenantAdminUsers = repository.findById(Long.valueOf(id));
        if(tenantAdminUsers.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(tenantAdminUsers.get());
        return  ResponseEntity.status(HttpStatus.OK).body("TenantAdminUsers deleted");
    }
}
