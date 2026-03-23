package pp.bom;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("pp/bom")
public class Controller {

    @Autowired
    private BomRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<BomResponseDTO> getAll(){

        List<BomResponseDTO> bomList = repository.findAll().stream().map(BomResponseDTO::new).toList();
        return bomList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Bom> bom = repository.findById(id);
        if(bom.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        BomResponseDTO bomDTO = new BomResponseDTO(bom.get());
        return  ResponseEntity.ok(bomDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveBom(@RequestBody BomRequestDTO data){

        Bom bomData = new Bom(data);
        repository.save(bomData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBom(@PathVariable(value = "id") Integer id, @RequestBody BomRequestDTO upData){

        Optional<Bom> bom = repository.findById(id);
        if(bom.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Bom bomModel = bom.get();
        BeanUtils.copyProperties(upData, bomModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(bomModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBom(@PathVariable(value = "id") Integer id){

        Optional<Bom> bom = repository.findById(id);
        if(bom.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(bom.get());
        return  ResponseEntity.status(HttpStatus.OK).body("BOM deleted");
    }
}
