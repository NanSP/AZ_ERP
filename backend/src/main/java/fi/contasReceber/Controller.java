package fi.contasReceber;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("fi/contasReceber")
public class Controller {

    @Autowired
    private ContasReceberRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<ContasReceberResponseDTO> getAll(){

        List<ContasReceberResponseDTO> contasReceberList = repository.findAll().stream().map(ContasReceberResponseDTO::new).toList();
        return contasReceberList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<ContasReceber> contasReceber = repository.findById(id);
        if(contasReceber.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        ContasReceberResponseDTO contasPagarDTO = new ContasReceberResponseDTO(contasReceber.get());
        return  ResponseEntity.ok(contasPagarDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveContasReceber(@RequestBody ContasReceberRequestDTO data){

        ContasReceber contasReceberData = new ContasReceber(data);
        repository.save(contasReceberData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateContasReceber(@PathVariable(value = "id") Integer id, @RequestBody ContasReceberRequestDTO upData){

        Optional<ContasReceber> contasReceber = repository.findById(id);
        if(contasReceber.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        ContasReceber contasReceberModel = contasReceber.get();
        BeanUtils.copyProperties(upData, contasReceberModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(contasReceberModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContasReceber(@PathVariable(value = "id") Integer id){

        Optional<ContasReceber> contasReceber = repository.findById(id);
        if(contasReceber.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(contasReceber.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Contas a Receber deleted");
    }
}
