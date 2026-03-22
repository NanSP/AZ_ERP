package sm.ordensServico;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("ordensServico")
public class Controller {

    @Autowired
    private OrdensServicoRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<OrdensServicoResponseDTO> getAll(){

        List<OrdensServicoResponseDTO> ordensServicoList = repository.findAll().stream().map(OrdensServicoResponseDTO::new).toList();
        return ordensServicoList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<OrdensServico> ordensServico = repository.findById(id);
        if(ordensServico.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        OrdensServicoResponseDTO ordensServicoDTO = new OrdensServicoResponseDTO(ordensServico.get());
        return  ResponseEntity.ok(ordensServicoDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveOrdensServico(@RequestBody OrdensServicoRequestDTO data){

        OrdensServico ordensServicoData = new OrdensServico(data);
        repository.save(ordensServicoData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrdensServico(@PathVariable(value = "id") Integer id, @RequestBody OrdensServicoRequestDTO upData){

        Optional<OrdensServico> ordensServico = repository.findById(id);
        if(ordensServico.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        OrdensServico ordensServicoModel = ordensServico.get();
        BeanUtils.copyProperties(upData, ordensServicoModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(ordensServicoModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrdensServico(@PathVariable(value = "id") Integer id){

        Optional<OrdensServico> ordensServico = repository.findById(id);
        if(ordensServico.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(ordensServico.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Ordens Servico deleted");
    }
}
