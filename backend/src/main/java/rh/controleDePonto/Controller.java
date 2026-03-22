package rh.controleDePonto;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("controleDePonto")
public class Controller {

    @Autowired
    private ControleDePontoRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<ControleDePontoResponseDTO> getAll(){

        List<ControleDePontoResponseDTO> controleDePontoList = repository.findAll().stream().map(ControleDePontoResponseDTO::new).toList();
        return controleDePontoList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<ControleDePonto> controleDePonto = repository.findById(id);
        if(controleDePonto.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        ControleDePontoResponseDTO controleDePontoDTO = new ControleDePontoResponseDTO(controleDePonto.get());
        return  ResponseEntity.ok(controleDePontoDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveControleDePonto(@RequestBody ControleDePontoRequestDTO data){

        ControleDePonto controleDePontoData = new ControleDePonto(data);
        repository.save(controleDePontoData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateControleDePonto(@PathVariable(value = "id") Integer id, @RequestBody ControleDePontoRequestDTO upData){

        Optional<ControleDePonto> controleDePonto = repository.findById(id);
        if(controleDePonto.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        ControleDePonto controleDePontoModel = controleDePonto.get();
        BeanUtils.copyProperties(upData, controleDePontoModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(controleDePontoModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteControleDePonto(@PathVariable(value = "id") Integer id){

        Optional<ControleDePonto> controleDePonto = repository.findById(id);
        if(controleDePonto.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(controleDePonto.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Ponto deleted");
    }
}
