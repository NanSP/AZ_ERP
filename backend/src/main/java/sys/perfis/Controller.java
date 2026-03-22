package sys.perfis;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("perfis")
public class Controller {

    @Autowired
    private PerfisRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<PerfisResponseDTO> getAll(){

        List<PerfisResponseDTO> perfisList = repository.findAll().stream().map(PerfisResponseDTO::new).toList();
        return perfisList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Perfis> perfis = repository.findById(id);
        if(perfis.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        PerfisResponseDTO perfisDTO = new PerfisResponseDTO(perfis.get());
        return  ResponseEntity.ok(perfisDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void savePerfis(@RequestBody PerfisRequestDTO data){

        Perfis perfisData = new Perfis(data);
        repository.save(perfisData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePerfis(@PathVariable(value = "id") Integer id, @RequestBody PerfisRequestDTO upData){

        Optional<Perfis> perfis = repository.findById(id);
        if(perfis.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Perfis perfisModel = perfis.get();
        BeanUtils.copyProperties(upData, perfisModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(perfisModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePerfis(@PathVariable(value = "id") Integer id){

        Optional<Perfis> perfis = repository.findById(id);
        if(perfis.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(perfis.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Perfil deleted");
    }
}
