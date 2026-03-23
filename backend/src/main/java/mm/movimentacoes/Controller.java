package mm.movimentacoes;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("mm/movimentacoes")
public class Controller {

    @Autowired
    private MovimentacoesRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<MovimentacoesResponseDTO> getAll(){

        List<MovimentacoesResponseDTO> movimentacoesList = repository.findAll().stream().map(MovimentacoesResponseDTO::new).toList();
        return movimentacoesList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Movimentacoes> movimentacoes = repository.findById(id);
        if(movimentacoes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        MovimentacoesResponseDTO movimentacoesDTO = new MovimentacoesResponseDTO(movimentacoes.get());
        return  ResponseEntity.ok(movimentacoesDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveMovimentacoes(@RequestBody MovimentacoesRequestDTO data){

        Movimentacoes movimentacoesData = new Movimentacoes(data);
        repository.save(movimentacoesData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMovimentacoes(@PathVariable(value = "id") Integer id, @RequestBody MovimentacoesRequestDTO upData){

        Optional<Movimentacoes> movimentacoes = repository.findById(id);
        if(movimentacoes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Movimentacoes movimentacoesModel = movimentacoes.get();
        BeanUtils.copyProperties(upData, movimentacoesModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(movimentacoesModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovimentacoes(@PathVariable(value = "id") Integer id){

        Optional<Movimentacoes> movimentacoes = repository.findById(id);
        if(movimentacoes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(movimentacoes.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Movimentação deleted");
    }
}
