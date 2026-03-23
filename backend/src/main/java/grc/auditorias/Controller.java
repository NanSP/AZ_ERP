package grc.auditorias;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("grc/auditorias")
public class Controller {

    @Autowired
    private AuditoriasRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<AuditoriasResponseDTO> getAll(){

        List<AuditoriasResponseDTO> auditoriasList = repository.findAll().stream().map(AuditoriasResponseDTO::new).toList();
        return auditoriasList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Auditorias> auditorias = repository.findById(id);
        if(auditorias.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        AuditoriasResponseDTO auditoriasDTO = new AuditoriasResponseDTO(auditorias.get());
        return  ResponseEntity.ok(auditoriasDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveCompras(@RequestBody AuditoriasRequestDTO data){

        Auditorias auditoriasData = new Auditorias(data);
        repository.save(auditoriasData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAuditorias(@PathVariable(value = "id") Integer id, @RequestBody AuditoriasRequestDTO upData){

        Optional<Auditorias> auditorias = repository.findById(id);
        if(auditorias.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Auditorias auditoriasModel = auditorias.get();
        BeanUtils.copyProperties(upData, auditoriasModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(auditoriasModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAuditorias(@PathVariable(value = "id") Integer id){

        Optional<Auditorias> auditorias = repository.findById(id);
        if(auditorias.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(auditorias.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Auditorias deleted");
    }
}
