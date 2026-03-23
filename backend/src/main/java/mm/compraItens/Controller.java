package mm.compraItens;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/mm/comprasItens")
public class Controller {

    @Autowired
    private CompraItensRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<CompraItensResponseDTO> getAll(){

        List<CompraItensResponseDTO> compraItensList = repository.findAll().stream().map(CompraItensResponseDTO::new).toList();
        return compraItensList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<CompraItens> compraItens = repository.findById(id);
        if(compraItens.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        CompraItensResponseDTO compraItensDTO = new CompraItensResponseDTO(compraItens.get());
        return  ResponseEntity.ok(compraItensDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveCompras(@RequestBody CompraItensRequestDTO data){

        CompraItens compraItensData = new CompraItens(data);
        repository.save(compraItensData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCompraItens(@PathVariable(value = "id") Integer id, @RequestBody CompraItensRequestDTO upData){

        Optional<CompraItens> compraItens = repository.findById(id);
        if(compraItens.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        CompraItens compraItensModel = compraItens.get();
        BeanUtils.copyProperties(upData, compraItensModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(compraItensModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCompraItens(@PathVariable(value = "id") Integer id){

        Optional<CompraItens> compraItens = repository.findById(id);
        if(compraItens.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(compraItens.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Compra Itens deleted");
    }
}
