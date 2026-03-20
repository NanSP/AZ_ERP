package core.contatos;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("contatos")
public class Controller {

    @Autowired
    private ContatosRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<ContatosResponseDTO> getAll(){

        List<ContatosResponseDTO> contatosList = repository.findAll().stream().map(ContatosResponseDTO::new).toList();
        return contatosList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Contatos> contatos = repository.findById(id);
        if(contatos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        ContatosResponseDTO contatosDTO = new ContatosResponseDTO(contatos.get());
        return  ResponseEntity.ok(contatosDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveContato(@RequestBody ContatosRequestDTO data){

        Contatos contatoData = new Contatos(data);
        repository.save(contatoData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateContato(@PathVariable(value = "id") Integer id, @RequestBody ContatosRequestDTO upData){

        Optional<Contatos> contatos = repository.findById(id);
        if(contatos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Contatos contatosModel = contatos.get();
        BeanUtils.copyProperties(upData, contatosModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(contatosModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContato(@PathVariable(value = "id") Integer id){

        Optional<Contatos> contatos = repository.findById(id);
        if(contatos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(contatos.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Beneficio deleted");
    }
}
