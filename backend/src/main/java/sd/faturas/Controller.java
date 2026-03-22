package sd.faturas;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("faturas")
public class Controller {

    @Autowired
    private FaturasRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<FaturasResponseDTO> getAll(){

        List<FaturasResponseDTO> faturasList = repository.findAll().stream().map(FaturasResponseDTO::new).toList();
        return faturasList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Faturas> faturas = repository.findById(id);
        if(faturas.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        FaturasResponseDTO faturasDTO = new FaturasResponseDTO(faturas.get());
        return  ResponseEntity.ok(faturasDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveFaturas(@RequestBody FaturasRequestDTO data){

        Faturas faturasData = new Faturas(data);
        repository.save(faturasData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFaturas(@PathVariable(value = "id") Integer id, @RequestBody FaturasRequestDTO upData){

        Optional<Faturas> faturas = repository.findById(id);
        if(faturas.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Faturas faturasModel = faturas.get();
        BeanUtils.copyProperties(upData, faturasModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(faturasModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFaturas(@PathVariable(value = "id") Integer id){

        Optional<Faturas> faturas = repository.findById(id);
        if(faturas.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(faturas.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Fatura deleted");
    }
}
