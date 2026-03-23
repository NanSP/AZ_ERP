package bi.relatorios;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bi/relatorios")
public class Controller {

    @Autowired
    private RelatoriosRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<RelatoriosResponseDTO> getAll(){

        List<RelatoriosResponseDTO> relatoriosList = repository.findAll().stream().map(RelatoriosResponseDTO::new).toList();
        return relatoriosList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Relatorios> relatorios = repository.findById(id);
        if(relatorios.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        RelatoriosResponseDTO relatoriosDTO = new RelatoriosResponseDTO(relatorios.get());
        return  ResponseEntity.ok(relatoriosDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveCompras(@RequestBody RelatoriosRequestDTO data){

        Relatorios relatoriosData = new Relatorios(data);
        repository.save(relatoriosData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRelatorios(@PathVariable(value = "id") Integer id, @RequestBody RelatoriosRequestDTO upData){

        Optional<Relatorios> relatorios = repository.findById(id);
        if(relatorios.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Relatorios relatoriosModel = relatorios.get();
        BeanUtils.copyProperties(upData, relatoriosModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(relatoriosModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRelatorios(@PathVariable(value = "id") Integer id){

        Optional<Relatorios> relatorios = repository.findById(id);
        if(relatorios.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(relatorios.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Relatorios deleted");
    }
}
