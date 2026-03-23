package grc.controles;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/grc/controles")
public class Controller {

    @Autowired
    private ControlesRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<ControlesResponseDTO> getAll(){

        List<ControlesResponseDTO> controlesList = repository.findAll().stream().map(ControlesResponseDTO::new).toList();
        return controlesList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Controles> controles = repository.findById(id);
        if(controles.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        ControlesResponseDTO controlesDTO = new ControlesResponseDTO(controles.get());
        return  ResponseEntity.ok(controlesDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveCompras(@RequestBody ControlesRequestDTO data){

        Controles controlesData = new Controles(data);
        repository.save(controlesData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateControles(@PathVariable(value = "id") Integer id, @RequestBody ControlesRequestDTO upData){

        Optional<Controles> controles = repository.findById(id);
        if(controles.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Controles controlesModel = controles.get();
        BeanUtils.copyProperties(upData, controlesModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(controlesModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteControles(@PathVariable(value = "id") Integer id){

        Optional<Controles> controles = repository.findById(id);
        if(controles.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(controles.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Controles deleted");
    }
}
