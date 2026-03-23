package mm.compras;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("mm/compras")
public class Controller {

    @Autowired
    private ComprasRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<ComprasResponseDTO> getAll(){

        List<ComprasResponseDTO> comprasList = repository.findAll().stream().map(ComprasResponseDTO::new).toList();
        return comprasList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Compras> compras = repository.findById(id);
        if(compras.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        ComprasResponseDTO comprasDTO = new ComprasResponseDTO(compras.get());
        return  ResponseEntity.ok(comprasDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveCompras(@RequestBody ComprasRequestDTO data){

        Compras comprasData = new Compras(data);
        repository.save(comprasData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCompras(@PathVariable(value = "id") Integer id, @RequestBody ComprasRequestDTO upData){

        Optional<Compras> compras = repository.findById(id);
        if(compras.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Compras comprasModel = compras.get();
        BeanUtils.copyProperties(upData, comprasModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(comprasModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCompras(@PathVariable(value = "id") Integer id){

        Optional<Compras> compras = repository.findById(id);
        if(compras.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(compras.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Compra deleted");
    }
}
