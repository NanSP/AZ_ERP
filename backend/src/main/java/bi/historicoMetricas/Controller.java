package bi.historicoMetricas;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("bi/historicoMetricas")
public class Controller {

    @Autowired
    private HistoricoMetricasRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<HistoricoMetricasResponseDTO> getAll(){

        List<HistoricoMetricasResponseDTO> historicoMetricasList = repository.findAll().stream().map(HistoricoMetricasResponseDTO::new).toList();
        return historicoMetricasList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<HistoricoMetricas> historicoMetricas = repository.findById(id);
        if(historicoMetricas.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        HistoricoMetricasResponseDTO historicoMetricasDTO = new HistoricoMetricasResponseDTO(historicoMetricas.get());
        return  ResponseEntity.ok(historicoMetricasDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveCompras(@RequestBody HistoricoMetricasRequestDTO data){

        HistoricoMetricas historicoMetricasData = new HistoricoMetricas(data);
        repository.save(historicoMetricasData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateHistoricoMetricas(@PathVariable(value = "id") Integer id, @RequestBody HistoricoMetricasRequestDTO upData){

        Optional<HistoricoMetricas> historicoMetricas = repository.findById(id);
        if(historicoMetricas.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        HistoricoMetricas historicoMetricasModel = historicoMetricas.get();
        BeanUtils.copyProperties(upData, historicoMetricasModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(historicoMetricasModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHistoricoMetricas(@PathVariable(value = "id") Integer id){

        Optional<HistoricoMetricas> historicoMetricas = repository.findById(id);
        if(historicoMetricas.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(historicoMetricas.get());
        return  ResponseEntity.status(HttpStatus.OK).body("HistoricoMetricas deleted");
    }
}
