package mm.inventarios;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("inventarios")
public class Controller {

    @Autowired
    private InventariosRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<InventariosResponseDTO> getAll(){

        List<InventariosResponseDTO> inventariosList = repository.findAll().stream().map(InventariosResponseDTO::new).toList();
        return inventariosList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Inventarios> inventarios = repository.findById(id);
        if(inventarios.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        InventariosResponseDTO inventariosDTO = new InventariosResponseDTO(inventarios.get());
        return  ResponseEntity.ok(inventariosDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveInventarios(@RequestBody InventariosRequestDTO data){

        Inventarios inventariosData = new Inventarios(data);
        repository.save(inventariosData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInventarios(@PathVariable(value = "id") Integer id, @RequestBody InventariosRequestDTO upData){

        Optional<Inventarios> inventarios = repository.findById(id);
        if(inventarios.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Inventarios inventariosModel = inventarios.get();
        BeanUtils.copyProperties(upData, inventariosModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(inventariosModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInventarios(@PathVariable(value = "id") Integer id){

        Optional<Inventarios> inventarios = repository.findById(id);
        if(inventarios.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(inventarios.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Inventarios deleted");
    }
}
