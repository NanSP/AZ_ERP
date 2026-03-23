package portal.notificacoes;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("notificacoes")
public class Controller {

    @Autowired
    private NotificacoesRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<NotificacoesResponseDTO> getAll(){

        List<NotificacoesResponseDTO> notificacoesList = repository.findAll().stream().map(NotificacoesResponseDTO::new).toList();
        return notificacoesList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Notificacoes> notificacoes = repository.findById(id);
        if(notificacoes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        NotificacoesResponseDTO notificacoesDTO = new NotificacoesResponseDTO(notificacoes.get());
        return  ResponseEntity.ok(notificacoesDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveCompras(@RequestBody NotificacoesRequestDTO data){

        Notificacoes notificacoesData = new Notificacoes(data);
        repository.save(notificacoesData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNotificacoes(@PathVariable(value = "id") Integer id, @RequestBody NotificacoesRequestDTO upData){

        Optional<Notificacoes> notificacoes = repository.findById(id);
        if(notificacoes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Notificacoes notificacoesModel = notificacoes.get();
        BeanUtils.copyProperties(upData, notificacoesModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(notificacoesModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotificacoes(@PathVariable(value = "id") Integer id){

        Optional<Notificacoes> notificacoes = repository.findById(id);
        if(notificacoes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(notificacoes.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Notificacoes deleted");
    }
}
