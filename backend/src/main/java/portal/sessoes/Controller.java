package portal.sessoes;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("portal/sessoes")
public class Controller {

    @Autowired
    private SessoesRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<SessoesResponseDTO> getAll(){

        List<SessoesResponseDTO> sessoesList = repository.findAll().stream().map(SessoesResponseDTO::new).toList();
        return sessoesList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Sessoes> sessoes = repository.findById(id);
        if(sessoes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        SessoesResponseDTO sessoesDTO = new SessoesResponseDTO(sessoes.get());
        return  ResponseEntity.ok(sessoesDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveCompras(@RequestBody SessoesRequestDTO data){

        Sessoes sessoesData = new Sessoes(data);
        repository.save(sessoesData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSessoes(@PathVariable(value = "id") Integer id, @RequestBody SessoesRequestDTO upData){

        Optional<Sessoes> sessoes = repository.findById(id);
        if(sessoes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Sessoes sessoesModel = sessoes.get();
        BeanUtils.copyProperties(upData, sessoesModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(sessoesModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSessoes(@PathVariable(value = "id") Integer id){

        Optional<Sessoes> sessoes = repository.findById(id);
        if(sessoes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(sessoes.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Sessoes deleted");
    }
}
