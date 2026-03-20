package rh.dependentes;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("dependentes")
public class Controller {

    @Autowired
    private DependentesRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<DependentesResponseDTO> getAll(){

        List<DependentesResponseDTO> dependentesList = repository.findAll().stream().map(DependentesResponseDTO::new).toList();
        return dependentesList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Dependentes> dependentes = repository.findById(id);
        if(dependentes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        DependentesResponseDTO dependentesDTO = new DependentesResponseDTO(dependentes.get());
        return  ResponseEntity.ok(dependentesDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveDependente(@RequestBody DependentesRequestDTO data){

        Dependentes dependentesData = new Dependentes(data);
        repository.save(dependentesData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDependente(@PathVariable(value = "id") Integer id, @RequestBody DependentesRequestDTO upData){

        Optional<Dependentes> dependentes = repository.findById(id);
        if(dependentes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Dependentes dependentesModel = dependentes.get();
        BeanUtils.copyProperties(upData, dependentesModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(dependentesModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDependente(@PathVariable(value = "id") Integer id){

        Optional<Dependentes> dependentes = repository.findById(id);
        if(dependentes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(dependentes.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Beneficio deleted");
    }
}
