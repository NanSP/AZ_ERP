package fi.fluxoCaixa;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("fluxoCaixa")
public class Controller {

    @Autowired
    private FluxoCaixaRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<FluxoCaixaResponseDTO> getAll(){

        List<FluxoCaixaResponseDTO> fluxoCaixaList = repository.findAll().stream().map(FluxoCaixaResponseDTO::new).toList();
        return fluxoCaixaList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<FluxoCaixa> fluxoCaixa = repository.findById(id);
        if(fluxoCaixa.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        FluxoCaixaResponseDTO contasPagarDTO = new FluxoCaixaResponseDTO(fluxoCaixa.get());
        return  ResponseEntity.ok(contasPagarDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveFluxoCaixa(@RequestBody FluxoCaixaRequestDTO data){

        FluxoCaixa fluxoCaixaData = new FluxoCaixa(data);
        repository.save(fluxoCaixaData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFluxoCaixa(@PathVariable(value = "id") Integer id, @RequestBody FluxoCaixaRequestDTO upData){

        Optional<FluxoCaixa> fluxoCaixa = repository.findById(id);
        if(fluxoCaixa.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        FluxoCaixa fluxoCaixaModel = fluxoCaixa.get();
        BeanUtils.copyProperties(upData, fluxoCaixaModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(fluxoCaixaModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContasReceber(@PathVariable(value = "id") Integer id){

        Optional<FluxoCaixa> fluxoCaixa = repository.findById(id);
        if(fluxoCaixa.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(fluxoCaixa.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Fluxo de Caixa deleted");
    }
}
