package com.example.backend.core.parceiros;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/core/parceiros")
public class ParceirosController {

    @Autowired
    private ParceirosRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<ParceirosResponseDTO> getAll(){

        List<ParceirosResponseDTO> parceirosList = repository.findAll().stream().map(ParceirosResponseDTO::new).toList();
        return parceirosList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Parceiros> parceiros = repository.findById(id);
        if(parceiros.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        ParceirosResponseDTO parceirosDTO = new ParceirosResponseDTO(parceiros.get());
        return  ResponseEntity.ok(parceirosDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveParceiro(@RequestBody ParceirosRequestDTO data){

        Parceiros parceiroData = new Parceiros(data);
        repository.save(parceiroData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateParceiro(@PathVariable(value = "id") Integer id, @RequestBody ParceirosRequestDTO upData){

        Optional<Parceiros> parceiros = repository.findById(id);
        if(parceiros.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Parceiros parceirosModel = parceiros.get();
        BeanUtils.copyProperties(upData, parceirosModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(parceirosModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteParceiro(@PathVariable(value = "id") Integer id){

        Optional<Parceiros> parceiros = repository.findById(id);
        if(parceiros.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(parceiros.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Parceiro deleted");
    }
}
