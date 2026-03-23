package fiscal.efdRegistros;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fiscal/efdRegistros")
public class Controller {

    @Autowired
    private EfdRegistrosRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<EfdRegistrosResponseDTO> getAll(){

        List<EfdRegistrosResponseDTO> efdRegistrosList = repository.findAll().stream().map(EfdRegistrosResponseDTO::new).toList();
        return efdRegistrosList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<EfdRegistros> efdRegistros = repository.findById(id);
        if(efdRegistros.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        EfdRegistrosResponseDTO efdRegistrosDTO = new EfdRegistrosResponseDTO(efdRegistros.get());
        return  ResponseEntity.ok(efdRegistrosDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveCompras(@RequestBody EfdRegistrosRequestDTO data){

        EfdRegistros efdRegistrosData = new EfdRegistros(data);
        repository.save(efdRegistrosData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEfdRegistros(@PathVariable(value = "id") Integer id, @RequestBody EfdRegistrosRequestDTO upData){

        Optional<EfdRegistros> efdRegistros = repository.findById(id);
        if(efdRegistros.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        EfdRegistros efdRegistrosModel = efdRegistros.get();
        BeanUtils.copyProperties(upData, efdRegistrosModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(efdRegistrosModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEfdRegistros(@PathVariable(value = "id") Integer id){

        Optional<EfdRegistros> efdRegistros = repository.findById(id);
        if(efdRegistros.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(efdRegistros.get());
        return  ResponseEntity.status(HttpStatus.OK).body("EfdRegistros deleted");
    }
}
