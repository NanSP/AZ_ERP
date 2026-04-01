package com.example.backend.master.platform.tenants;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/platform/tenants")
public class TenantsController {

    @Autowired
    private TenantsRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<TenantsResponseDTO> getAll(){

        List<TenantsResponseDTO> tenantsList = repository.findAll().stream().map(TenantsResponseDTO::new).toList();
        return tenantsList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Long id){

        Optional<Tenants> tenants = repository.findById(id);
        if(tenants.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        TenantsResponseDTO tenantsDTO = new TenantsResponseDTO(tenants.get());
        return  ResponseEntity.ok(tenantsDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveTenants(@RequestBody TenantsRequestDTO data){

        Tenants tenantsData = new Tenants(data);
        repository.save(tenantsData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTenants(@PathVariable(value = "id") Long id, @RequestBody TenantsRequestDTO upData){

        Optional<Tenants> tenants = repository.findById(id);
        if(tenants.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Tenants tenantsModel = tenants.get();
        BeanUtils.copyProperties(upData, tenantsModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(tenantsModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTenants(@PathVariable(value = "id") Long id){

        Optional<Tenants> tenants = repository.findById(id);
        if(tenants.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(tenants.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Tenants deleted");
    }
}
