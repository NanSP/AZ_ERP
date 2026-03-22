package am.bensPatrimoniais;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("bens_patrimoniais")
public class Controller {

    @Autowired
    private BensPatrimoniaisRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<BensPatrimoniaisResponseDTO> getAll(){

        List<BensPatrimoniaisResponseDTO> bensPatrimoniaisList = repository.findAll().stream().map(BensPatrimoniaisResponseDTO::new).toList();
        return bensPatrimoniaisList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<BensPatrimoniais> bensPatrimoniais = repository.findById(id);
        if(bensPatrimoniais.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        BensPatrimoniaisResponseDTO bensPatrimoniaisDTO = new BensPatrimoniaisResponseDTO(bensPatrimoniais.get());
        return  ResponseEntity.ok(bensPatrimoniaisDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveCompras(@RequestBody BensPatrimoniaisRequestDTO data){

        BensPatrimoniais bensPatrimoniaisData = new BensPatrimoniais(data);
        repository.save(bensPatrimoniaisData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBensPatrimoniais(@PathVariable(value = "id") Integer id, @RequestBody BensPatrimoniaisRequestDTO upData){

        Optional<BensPatrimoniais> bensPatrimoniais = repository.findById(id);
        if(bensPatrimoniais.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        BensPatrimoniais bensPatrimoniaisModel = bensPatrimoniais.get();
        BeanUtils.copyProperties(upData, bensPatrimoniaisModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(bensPatrimoniaisModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBensPatrimoniais(@PathVariable(value = "id") Integer id){

        Optional<BensPatrimoniais> bensPatrimoniais = repository.findById(id);
        if(bensPatrimoniais.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(bensPatrimoniais.get());
        return  ResponseEntity.status(HttpStatus.OK).body("BensPatrimoniais deleted");
    }
}
