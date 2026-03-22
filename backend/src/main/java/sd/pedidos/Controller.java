package sd.pedidos;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("oportunidades")
public class Controller {

    @Autowired
    private PedidosRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<PedidosResponseDTO> getAll(){

        List<PedidosResponseDTO> pedidosList = repository.findAll().stream().map(PedidosResponseDTO::new).toList();
        return pedidosList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Pedidos> pedidos = repository.findById(id);
        if(pedidos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        PedidosResponseDTO pedidosDTO = new PedidosResponseDTO(pedidos.get());
        return  ResponseEntity.ok(pedidosDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void savePedidos(@RequestBody PedidosRequestDTO data){

        Pedidos pedidosData = new Pedidos(data);
        repository.save(pedidosData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOportunidades(@PathVariable(value = "id") Integer id, @RequestBody PedidosRequestDTO upData){

        Optional<Pedidos> pedidos = repository.findById(id);
        if(pedidos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Pedidos oportunidadesModel = pedidos.get();
        BeanUtils.copyProperties(upData, oportunidadesModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(oportunidadesModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePedidos(@PathVariable(value = "id") Integer id){

        Optional<Pedidos> pedidos = repository.findById(id);
        if(pedidos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(pedidos.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Pedido deleted");
    }
}
