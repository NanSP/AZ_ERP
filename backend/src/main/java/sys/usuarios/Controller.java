package sys.usuarios;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("usuarios")
public class Controller {

    @Autowired
    private UsuariosRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<UsuariosResponseDTO> getAll(){

        List<UsuariosResponseDTO> usuariosList = repository.findAll().stream().map(UsuariosResponseDTO::new).toList();
        return usuariosList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Usuarios> usuarios = repository.findById(id);
        if(usuarios.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        UsuariosResponseDTO usuariosDTO = new UsuariosResponseDTO(usuarios.get());
        return  ResponseEntity.ok(usuariosDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveUsuarios(@RequestBody UsuariosRequestDTO data){

        Usuarios usuariosData = new Usuarios(data);
        repository.save(usuariosData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuarios(@PathVariable(value = "id") Integer id, @RequestBody UsuariosRequestDTO upData){

        Optional<Usuarios> usuarios = repository.findById(id);
        if(usuarios.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Usuarios usuariosModel = usuarios.get();
        BeanUtils.copyProperties(upData, usuariosModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(usuariosModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuarios(@PathVariable(value = "id") Integer id){

        Optional<Usuarios> usuarios = repository.findById(id);
        if(usuarios.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(usuarios.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Usuario deleted");
    }
}
