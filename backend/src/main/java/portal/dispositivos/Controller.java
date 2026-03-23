package portal.dispositivos;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("dispositivos")
public class Controller {

    @Autowired
    private DispositivosRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<DispositivosResponseDTO> getAll(){

        List<DispositivosResponseDTO> dispositivosList = repository.findAll().stream().map(DispositivosResponseDTO::new).toList();
        return dispositivosList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Dispositivos> dispositivos = repository.findById(id);
        if(dispositivos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        DispositivosResponseDTO dispositivosDTO = new DispositivosResponseDTO(dispositivos.get());
        return  ResponseEntity.ok(dispositivosDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveCompras(@RequestBody DispositivosRequestDTO data){

        Dispositivos dispositivosData = new Dispositivos(data);
        repository.save(dispositivosData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDispositivos(@PathVariable(value = "id") Integer id, @RequestBody DispositivosRequestDTO upData){

        Optional<Dispositivos> dispositivos = repository.findById(id);
        if(dispositivos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Dispositivos dispositivosModel = dispositivos.get();
        BeanUtils.copyProperties(upData, dispositivosModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(dispositivosModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDispositivos(@PathVariable(value = "id") Integer id){

        Optional<Dispositivos> dispositivos = repository.findById(id);
        if(dispositivos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(dispositivos.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Dispositivos deleted");
    }
}
