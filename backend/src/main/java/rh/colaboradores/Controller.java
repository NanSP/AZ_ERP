package rh.colaboradores;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rh/colaboradores")
public class Controller {

    @Autowired
    private ColaboradoresRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<ColaboradoresResponseDTO> getAll(){

        List<ColaboradoresResponseDTO> colaboradoresList = repository.findAll().stream().map(ColaboradoresResponseDTO::new).toList();
        return colaboradoresList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Colaboradores> colaboradores = repository.findById(id);
        if(colaboradores.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        ColaboradoresResponseDTO colaboradoresDTO = new ColaboradoresResponseDTO(colaboradores.get());
        return  ResponseEntity.ok(colaboradoresDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveColaborador(@RequestBody ColaboradoresRequestDTO data){

        Colaboradores colaboradoresData = new Colaboradores(data);
        repository.save(colaboradoresData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateColaborador(@PathVariable(value = "id") Integer id, @RequestBody ColaboradoresRequestDTO upData){

        Optional<Colaboradores> colaboradores = repository.findById(id);
        if(colaboradores.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Colaboradores colaboradorModel = colaboradores.get();
        BeanUtils.copyProperties(upData, colaboradorModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(colaboradorModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteColaborador(@PathVariable(value = "id") Integer id){

        Optional<Colaboradores> colaboradores = repository.findById(id);
        if(colaboradores.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(colaboradores.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Colaborador deleted");
    }
}
