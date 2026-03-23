package fi.planoContas;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fi/planoContas")
public class Controller {

    @Autowired
    private PlanoContasRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<PlanoContasResponseDTO> getAll(){

        List<PlanoContasResponseDTO> planoContasList = repository.findAll().stream().map(PlanoContasResponseDTO::new).toList();
        return planoContasList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<PlanoContas> planoContas = repository.findById(id);
        if(planoContas.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        PlanoContasResponseDTO planoContasDTO = new PlanoContasResponseDTO(planoContas.get());
        return  ResponseEntity.ok(planoContasDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void savePlanoContas(@RequestBody PlanoContasRequestDTO data){

        PlanoContas planoContasData = new PlanoContas(data);
        repository.save(planoContasData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlanoContas(@PathVariable(value = "id") Integer id, @RequestBody PlanoContasRequestDTO upData){

        Optional<PlanoContas> planoContas = repository.findById(id);
        if(planoContas.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        PlanoContas planoContasModel = planoContas.get();
        BeanUtils.copyProperties(upData, planoContasModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(planoContasModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlanoContas(@PathVariable(value = "id") Integer id){

        Optional<PlanoContas> planoContas = repository.findById(id);
        if(planoContas.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(planoContas.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Plano Contas deleted");
    }
}
