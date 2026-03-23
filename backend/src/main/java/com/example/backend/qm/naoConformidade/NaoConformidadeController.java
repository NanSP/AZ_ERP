package com.example.backend.qm.naoConformidade;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/qm/naoConformidade")
public class NaoConformidadeController {

    @Autowired
    private NaoConformidadeRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<NaoConformidadeResponseDTO> getAll(){

        List<NaoConformidadeResponseDTO> naoConformidadeList = repository.findAll().stream().map(NaoConformidadeResponseDTO::new).toList();
        return naoConformidadeList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<NaoConformidade> naoConformidade = repository.findById(id);
        if(naoConformidade.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        NaoConformidadeResponseDTO naoConformidadeDTO = new NaoConformidadeResponseDTO(naoConformidade.get());
        return  ResponseEntity.ok(naoConformidadeDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveNaoConformidade(@RequestBody NaoConformidadeRequestDTO data){

        NaoConformidade naoConformidadeData = new NaoConformidade(data);
        repository.save(naoConformidadeData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNaoConformidade(@PathVariable(value = "id") Integer id, @RequestBody NaoConformidadeRequestDTO upData){

        Optional<NaoConformidade> naoConformidade = repository.findById(id);
        if(naoConformidade.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        NaoConformidade naoConformidadeModel = naoConformidade.get();
        BeanUtils.copyProperties(upData, naoConformidadeModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(naoConformidadeModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNaoConformidade(@PathVariable(value = "id") Integer id){

        Optional<NaoConformidade> naoConformidade = repository.findById(id);
        if(naoConformidade.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(naoConformidade.get());
        return  ResponseEntity.status(HttpStatus.OK).body("NaoConformidade deleted");
    }
}
