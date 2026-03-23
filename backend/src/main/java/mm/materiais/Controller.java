package mm.materiais;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("mm/materiais")
public class Controller {

    @Autowired
    private MateriaisRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<MateriaisResponseDTO> getAll(){

        List<MateriaisResponseDTO> materiaisList = repository.findAll().stream().map(MateriaisResponseDTO::new).toList();
        return materiaisList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Materiais> materiais = repository.findById(id);
        if(materiais.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        MateriaisResponseDTO materiaisDTO = new MateriaisResponseDTO(materiais.get());
        return  ResponseEntity.ok(materiaisDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveMateriais(@RequestBody MateriaisRequestDTO data){

        Materiais materiaisData = new Materiais(data);
        repository.save(materiaisData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMateriais(@PathVariable(value = "id") Integer id, @RequestBody MateriaisRequestDTO upData){

        Optional<Materiais> materiais = repository.findById(id);
        if(materiais.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Materiais materiaisModel = materiais.get();
        BeanUtils.copyProperties(upData, materiaisModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(materiaisModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMateriais(@PathVariable(value = "id") Integer id){

        Optional<Materiais> materiais = repository.findById(id);
        if(materiais.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(materiais.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Materiais deleted");
    }
}
