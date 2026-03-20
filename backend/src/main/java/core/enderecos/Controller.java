package core.enderecos;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("enderecos")
public class Controller {

    @Autowired
    private EnderecosRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<EnderecosResponseDTO> getAll(){

        List<EnderecosResponseDTO> enderecosList = repository.findAll().stream().map(EnderecosResponseDTO::new).toList();
        return enderecosList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Enderecos> enderecos = repository.findById(id);
        if(enderecos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        EnderecosResponseDTO enderecosDTO = new EnderecosResponseDTO(enderecos.get());
        return  ResponseEntity.ok(enderecosDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveEndereco(@RequestBody EnderecosRequestDTO data){

        Enderecos enderecoData = new Enderecos(data);
        repository.save(enderecoData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEndereco(@PathVariable(value = "id") Integer id, @RequestBody EnderecosRequestDTO upData){

        Optional<Enderecos> enderecos = repository.findById(id);
        if(enderecos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Enderecos enderecosModel = enderecos.get();
        BeanUtils.copyProperties(upData, enderecosModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(enderecosModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEndereco(@PathVariable(value = "id") Integer id){

        Optional<Enderecos> enderecos = repository.findById(id);
        if(enderecos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(enderecos.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Beneficio deleted");
    }
}
