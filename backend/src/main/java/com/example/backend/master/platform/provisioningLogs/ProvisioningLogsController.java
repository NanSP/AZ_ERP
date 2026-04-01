package com.example.backend.master.platform.provisioningLogs;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/platform/provisioningLogs")
public class ProvisioningLogsController {

    @Autowired
    private ProvisioningLogsRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<ProvisioningLogsResponseDTO> getAll(){

        List<ProvisioningLogsResponseDTO> ProvisioningLogsList = repository.findAll().stream().map(ProvisioningLogsResponseDTO::new).toList();
        return ProvisioningLogsList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<ProvisioningLogs> provisioningLogs = repository.findById(Long.valueOf(id));
        if(provisioningLogs.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        ProvisioningLogsResponseDTO provisioningLogsDTO = new ProvisioningLogsResponseDTO(provisioningLogs.get());
        return  ResponseEntity.ok(provisioningLogsDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveProvisioningLog(@RequestBody ProvisioningLogsRequestDTO data){

        ProvisioningLogs provisioningLogsData = new ProvisioningLogs(data);
        repository.save(provisioningLogsData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProvisioningLogs(@PathVariable(value = "id") Integer id, @RequestBody ProvisioningLogsRequestDTO upData){

        Optional<ProvisioningLogs> provisioningLogs = repository.findById(Long.valueOf(id));
        if(provisioningLogs.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        ProvisioningLogs provisioningLogsModel = provisioningLogs.get();
        BeanUtils.copyProperties(upData, provisioningLogsModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(provisioningLogsModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProvisioningLogs(@PathVariable(value = "id") Integer id){

        Optional<ProvisioningLogs> provisioningLogs = repository.findById(Long.valueOf(id));
        if(provisioningLogs.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(provisioningLogs.get());
        return  ResponseEntity.status(HttpStatus.OK).body("ProvisioningLogs deleted");
    }
}
