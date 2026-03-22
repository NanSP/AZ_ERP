package mm.estoques;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("estoques")
public class Controller {

    @Autowired
    private EstoquesRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<EstoquesResponseDTO> getAll(){

        List<EstoquesResponseDTO> estoquesList = repository.findAll().stream().map(EstoquesResponseDTO::new).toList();
        return estoquesList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Estoques> estoques = repository.findById(id);
        if(estoques.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        EstoquesResponseDTO estoquesDTO = new EstoquesResponseDTO(estoques.get());
        return  ResponseEntity.ok(estoquesDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveEstoques(@RequestBody EstoquesRequestDTO data){

        Estoques estoquesData = new Estoques(data);
        repository.save(estoquesData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEstoques(@PathVariable(value = "id") Integer id, @RequestBody EstoquesRequestDTO upData){

        Optional<Estoques> estoques = repository.findById(id);
        if(estoques.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Estoques estoquesModel = estoques.get();
        BeanUtils.copyProperties(upData, estoquesModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(estoquesModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEstoques(@PathVariable(value = "id") Integer id){

        Optional<Estoques> estoques = repository.findById(id);
        if(estoques.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(estoques.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Estoque deleted");
    }
}
