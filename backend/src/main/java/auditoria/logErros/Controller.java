package auditoria.logErros;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("auditoria/logErros")
public class Controller {

    @Autowired
    private LogErrosRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<LogErrosResponseDTO> getAll(){

        List<LogErrosResponseDTO> logErrosList = repository.findAll().stream().map(LogErrosResponseDTO::new).toList();
        return logErrosList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<LogErros> logErros = repository.findById(id);
        if(logErros.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        LogErrosResponseDTO logErrosDTO = new LogErrosResponseDTO(logErros.get());
        return  ResponseEntity.ok(logErrosDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveCompras(@RequestBody LogErrosRequestDTO data){

        LogErros logErrosData = new LogErros(data);
        repository.save(logErrosData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLogErros(@PathVariable(value = "id") Integer id, @RequestBody LogErrosRequestDTO upData){

        Optional<LogErros> logErros = repository.findById(id);
        if(logErros.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        LogErros logErrosModel = logErros.get();
        BeanUtils.copyProperties(upData, logErrosModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(logErrosModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLogErros(@PathVariable(value = "id") Integer id){

        Optional<LogErros> logErros = repository.findById(id);
        if(logErros.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(logErros.get());
        return  ResponseEntity.status(HttpStatus.OK).body("LogErros deleted");
    }
}
