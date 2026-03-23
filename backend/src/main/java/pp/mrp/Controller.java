package pp.mrp;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pp/mrp")
public class Controller {

    @Autowired
    private MrpRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<MrpResponseDTO> getAll(){

        List<MrpResponseDTO> mrpList = repository.findAll().stream().map(MrpResponseDTO::new).toList();
        return mrpList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Mrp> mrp = repository.findById(id);
        if(mrp.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        MrpResponseDTO MrpDTO = new MrpResponseDTO(mrp.get());
        return  ResponseEntity.ok(MrpDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveMrp(@RequestBody MrpRequestDTO data){

        Mrp mrpData = new Mrp(data);
        repository.save(mrpData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMrp(@PathVariable(value = "id") Integer id, @RequestBody MrpRequestDTO upData){

        Optional<Mrp> mrp = repository.findById(id);
        if(mrp.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Mrp mrpModel = mrp.get();
        BeanUtils.copyProperties(upData, mrpModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(mrpModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMrp(@PathVariable(value = "id") Integer id){

        Optional<Mrp> mrp = repository.findById(id);
        if(mrp.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(mrp.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Cliente deleted");
    }
}
