package pp.ordemProducao;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("ordemProducao")
public class Controller {

    @Autowired
    private OrdemProducaoRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<OrdemProducaoResponseDTO> getAll(){

        List<OrdemProducaoResponseDTO> ordemProducaoList = repository.findAll().stream().map(OrdemProducaoResponseDTO::new).toList();
        return ordemProducaoList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<OrdemProducao> ordemProducao = repository.findById(id);
        if(ordemProducao.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        OrdemProducaoResponseDTO ordemProducaoDTO = new OrdemProducaoResponseDTO(ordemProducao.get());
        return  ResponseEntity.ok(ordemProducaoDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveOrdemProducao(@RequestBody OrdemProducaoRequestDTO data){

        OrdemProducao ordemProducaoData = new OrdemProducao(data);
        repository.save(ordemProducaoData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrdemProducao(@PathVariable(value = "id") Integer id, @RequestBody OrdemProducaoRequestDTO upData){

        Optional<OrdemProducao> ordemProducao = repository.findById(id);
        if(ordemProducao.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        OrdemProducao ordemProducaoModel = ordemProducao.get();
        BeanUtils.copyProperties(upData, ordemProducaoModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(ordemProducaoModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrdemProducao(@PathVariable(value = "id") Integer id){

        Optional<OrdemProducao> ordemProducao = repository.findById(id);
        if(ordemProducao.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(ordemProducao.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Ordem Producao deleted");
    }
}
