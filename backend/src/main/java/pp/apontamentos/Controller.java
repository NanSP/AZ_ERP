package pp.apontamentos;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("pp/apontamentos")
public class Controller {

    @Autowired
    private ApontamentosRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<ApontamentosResponseDTO> getAll(){

        List<ApontamentosResponseDTO> apontamentosList = repository.findAll().stream().map(ApontamentosResponseDTO::new).toList();
        return apontamentosList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Apontamentos> apontamentos = repository.findById(id);
        if(apontamentos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        ApontamentosResponseDTO apontamentosDTO = new ApontamentosResponseDTO(apontamentos.get());
        return  ResponseEntity.ok(apontamentosDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveApontamentos(@RequestBody ApontamentosRequestDTO data){

        Apontamentos apontamentosData = new Apontamentos(data);
        repository.save(apontamentosData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateApontamentos(@PathVariable(value = "id") Integer id, @RequestBody ApontamentosRequestDTO upData){

        Optional<Apontamentos> apontamentos = repository.findById(id);
        if(apontamentos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Apontamentos apontamentosModel = apontamentos.get();
        BeanUtils.copyProperties(upData, apontamentosModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(apontamentosModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteApontamentos(@PathVariable(value = "id") Integer id){

        Optional<Apontamentos> apontamentos = repository.findById(id);
        if(apontamentos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(apontamentos.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Apontamentos deleted");
    }
}
