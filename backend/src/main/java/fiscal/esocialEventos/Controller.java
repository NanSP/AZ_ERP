package fiscal.esocialEventos;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fiscal/esocialEventos")
public class Controller {

    @Autowired
    private EsocialEventosRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<EsocialEventosResponseDTO> getAll(){

        List<EsocialEventosResponseDTO> esocialEventosList = repository.findAll().stream().map(EsocialEventosResponseDTO::new).toList();
        return esocialEventosList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<EsocialEventos> esocialEventos = repository.findById(id);
        if(esocialEventos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        EsocialEventosResponseDTO esocialEventosDTO = new EsocialEventosResponseDTO(esocialEventos.get());
        return  ResponseEntity.ok(esocialEventosDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveCompras(@RequestBody EsocialEventosRequestDTO data){

        EsocialEventos esocialEventosData = new EsocialEventos(data);
        repository.save(esocialEventosData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEsocialEventos(@PathVariable(value = "id") Integer id, @RequestBody EsocialEventosRequestDTO upData){

        Optional<EsocialEventos> esocialEventos = repository.findById(id);
        if(esocialEventos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        EsocialEventos esocialEventosModel = esocialEventos.get();
        BeanUtils.copyProperties(upData, esocialEventosModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(esocialEventosModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEsocialEventos(@PathVariable(value = "id") Integer id){

        Optional<EsocialEventos> esocialEventos = repository.findById(id);
        if(esocialEventos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(esocialEventos.get());
        return  ResponseEntity.status(HttpStatus.OK).body("EsocialEventos deleted");
    }
}
