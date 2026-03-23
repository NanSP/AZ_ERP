package com.example.backend.sm.slaConfig;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("slaConfig")
public class SlaConfigController {

    @Autowired
    private SlaConfigRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<SlaConfigResponseDTO> getAll(){

        List<SlaConfigResponseDTO> slaConfigList = repository.findAll().stream().map(SlaConfigResponseDTO::new).toList();
        return slaConfigList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<SlaConfig> slaConfig = repository.findById(id);
        if(slaConfig.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        SlaConfigResponseDTO slaConfigDTO = new SlaConfigResponseDTO(slaConfig.get());
        return  ResponseEntity.ok(slaConfigDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveSlaConfig(@RequestBody SlaConfigRequestDTO data){

        SlaConfig slaConfigData = new SlaConfig(data);
        repository.save(slaConfigData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSlaConfig(@PathVariable(value = "id") Integer id, @RequestBody SlaConfigRequestDTO upData){

        Optional<SlaConfig> slaConfig = repository.findById(id);
        if(slaConfig.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        SlaConfig slaConfigModel = slaConfig.get();
        BeanUtils.copyProperties(upData, slaConfigModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(slaConfigModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSlaConfig(@PathVariable(value = "id") Integer id){

        Optional<SlaConfig> slaConfig = repository.findById(id);
        if(slaConfig.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(slaConfig.get());
        return  ResponseEntity.status(HttpStatus.OK).body("SlaConfig deleted");
    }
}
