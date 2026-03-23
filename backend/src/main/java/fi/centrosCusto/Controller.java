package fi.centrosCusto;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fi/centrosCusto")
public class Controller {

    @Autowired
    private CentrosCustoRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<CentrosCustoResponseDTO> getAll(){

        List<CentrosCustoResponseDTO> centrosCustoList = repository.findAll().stream().map(CentrosCustoResponseDTO::new).toList();
        return centrosCustoList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<CentrosCusto> centrosCusto = repository.findById(id);
        if(centrosCusto.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        CentrosCustoResponseDTO centrosCustoDTO = new CentrosCustoResponseDTO(centrosCusto.get());
        return  ResponseEntity.ok(centrosCustoDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveCentrosCusto(@RequestBody CentrosCustoRequestDTO data){

        CentrosCusto centrosCustoData = new CentrosCusto(data);
        repository.save(centrosCustoData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCentrosCusto(@PathVariable(value = "id") Integer id, @RequestBody CentrosCustoRequestDTO upData){

        Optional<CentrosCusto> centrosCusto = repository.findById(id);
        if(centrosCusto.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        CentrosCusto centrosCustoModel = centrosCusto.get();
        BeanUtils.copyProperties(upData, centrosCustoModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(centrosCustoModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCentrosCusto(@PathVariable(value = "id") Integer id){

        Optional<CentrosCusto> centrosCusto = repository.findById(id);
        if(centrosCusto.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(centrosCusto.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Plano Contas deleted");
    }
}
