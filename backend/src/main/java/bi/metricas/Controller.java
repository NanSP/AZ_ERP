package bi.metricas;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bi/metricas")
public class Controller {

    @Autowired
    private MetricasRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<MetricasResponseDTO> getAll(){

        List<MetricasResponseDTO> metricasList = repository.findAll().stream().map(MetricasResponseDTO::new).toList();
        return metricasList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Metricas> metricas = repository.findById(id);
        if(metricas.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        MetricasResponseDTO metricasDTO = new MetricasResponseDTO(metricas.get());
        return  ResponseEntity.ok(metricasDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveCompras(@RequestBody MetricasRequestDTO data){

        Metricas metricasData = new Metricas(data);
        repository.save(metricasData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMetricas(@PathVariable(value = "id") Integer id, @RequestBody MetricasRequestDTO upData){

        Optional<Metricas> metricas = repository.findById(id);
        if(metricas.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Metricas metricasModel = metricas.get();
        BeanUtils.copyProperties(upData, metricasModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(metricasModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMetricas(@PathVariable(value = "id") Integer id){

        Optional<Metricas> metricas = repository.findById(id);
        if(metricas.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(metricas.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Metricas deleted");
    }
}
