package fi.movimentacoesBancarias;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("fi/movimentacoesBancarias")
public class Controller {

    @Autowired
    private MovimentacoesBancariasRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<MovimentacoesBancariasResponseDTO> getAll(){

        List<MovimentacoesBancariasResponseDTO> movimentacoesBancariasList = repository.findAll().stream().map(MovimentacoesBancariasResponseDTO::new).toList();
        return movimentacoesBancariasList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<MovimentacoesBancarias> movimentacoesBancarias = repository.findById(id);
        if(movimentacoesBancarias.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        MovimentacoesBancariasResponseDTO movimentacoesBancariasDTO = new MovimentacoesBancariasResponseDTO(movimentacoesBancarias.get());
        return  ResponseEntity.ok(movimentacoesBancariasDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveMovimentacoesBancarias(@RequestBody MovimentacoesBancariasRequestDTO data){

        MovimentacoesBancarias movimentacoesBancariasData = new MovimentacoesBancarias(data);
        repository.save(movimentacoesBancariasData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMovimentacoesBancarias(@PathVariable(value = "id") Integer id, @RequestBody MovimentacoesBancariasRequestDTO upData){

        Optional<MovimentacoesBancarias> movimentacoesBancarias = repository.findById(id);
        if(movimentacoesBancarias.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        MovimentacoesBancarias movimentacoesBancariasModel = movimentacoesBancarias.get();
        BeanUtils.copyProperties(upData, movimentacoesBancariasModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(movimentacoesBancariasModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovimentacoesBancarias(@PathVariable(value = "id") Integer id){

        Optional<MovimentacoesBancarias> movimentacoesBancarias = repository.findById(id);
        if(movimentacoesBancarias.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(movimentacoesBancarias.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Movimentação Bancaria deleted");
    }
}
