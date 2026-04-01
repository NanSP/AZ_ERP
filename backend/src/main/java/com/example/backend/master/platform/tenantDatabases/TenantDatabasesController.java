package com.example.backend.master.platform.tenantDatabases;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/platform/tenantDatabases")
public class TenantDatabasesController {

    @Autowired
    private TenantDatabasesRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<TenantDatabasesResponseDTO> getAll(){

        List<TenantDatabasesResponseDTO> tenantDatabasesList = repository.findAll().stream().map(TenantDatabasesResponseDTO::new).toList();
        return tenantDatabasesList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Long id){

        Optional<TenantDatabases> tenantDatabases = repository.findById(id);
        if(tenantDatabases.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        TenantDatabasesResponseDTO tenantDatabasesDTO = new TenantDatabasesResponseDTO(tenantDatabases.get());
        return  ResponseEntity.ok(tenantDatabasesDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveTenantDatabases(@RequestBody TenantDatabasesRequestDTO data){

        TenantDatabases tenantDatabasesData = new TenantDatabases(data);
        repository.save(tenantDatabasesData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTenantDatabases(@PathVariable(value = "id") Long id, @RequestBody TenantDatabasesRequestDTO upData){

        Optional<TenantDatabases> tenantDatabases = repository.findById(id);
        if(tenantDatabases.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        TenantDatabases tenantDatabasesModel = tenantDatabases.get();
        BeanUtils.copyProperties(upData, tenantDatabasesModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(tenantDatabasesModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTenantDatabases(@PathVariable(value = "id") Long id){

        Optional<TenantDatabases> tenantDatabases = repository.findById(id);
        if(tenantDatabases.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(tenantDatabases.get());
        return  ResponseEntity.status(HttpStatus.OK).body("TenantDatabases deleted");
    }
}
