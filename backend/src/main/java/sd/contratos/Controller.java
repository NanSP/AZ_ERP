package sd.contratos;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("contratos")
public class Controller {

    @Autowired
    private ContratosRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<ContratosResponseDTO> getAll(){

        List<ContratosResponseDTO> contratosList = repository.findAll().stream().map(ContratosResponseDTO::new).toList();
        return contratosList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Contratos> contratos = repository.findById(id);
        if(contratos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        ContratosResponseDTO contratosDTO = new ContratosResponseDTO(contratos.get());
        return  ResponseEntity.ok(contratosDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveContratos(@RequestBody ContratosRequestDTO data){

        Contratos contratosData = new Contratos(data);
        repository.save(contratosData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateContratos(@PathVariable(value = "id") Integer id, @RequestBody ContratosRequestDTO upData){

        Optional<Contratos> contratos = repository.findById(id);
        if(contratos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Contratos contratosModel = contratos.get();
        BeanUtils.copyProperties(upData, contratosModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(contratosModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContratos(@PathVariable(value = "id") Integer id){

        Optional<Contratos> contratos = repository.findById(id);
        if(contratos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(contratos.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Fatura deleted");
    }
}
