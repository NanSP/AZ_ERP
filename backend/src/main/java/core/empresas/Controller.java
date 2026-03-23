package core.empresas;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("core/empresas")
public class Controller {

    @Autowired
    private EmpresasRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<EmpresasResponseDTO> getAll(){

        List<EmpresasResponseDTO> empresasList = repository.findAll().stream().map(EmpresasResponseDTO::new).toList();
        return empresasList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Empresas> empresas = repository.findById(id);
        if(empresas.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        EmpresasResponseDTO empresasDTO = new EmpresasResponseDTO(empresas.get());
        return  ResponseEntity.ok(empresasDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveEmpresa(@RequestBody EmpresasRequestDTO data){

        Empresas empresaData = new Empresas(data);
        repository.save(empresaData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmpresa(@PathVariable(value = "id") Integer id, @RequestBody EmpresasRequestDTO upData){

        Optional<Empresas> empresas = repository.findById(id);
        if(empresas.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Empresas empresasModel = empresas.get();
        BeanUtils.copyProperties(upData, empresasModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(empresasModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmpresa(@PathVariable(value = "id") Integer id){

        Optional<Empresas> empresas = repository.findById(id);
        if(empresas.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(empresas.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Empresa deleted");
    }
}
