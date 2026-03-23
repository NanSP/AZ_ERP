package com.example.backend.sd.pedidoItens;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("pedidoItens")
public class Controller {

    @Autowired
    private PedidoItensRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<PedidoItensResponseDTO> getAll(){

        List<PedidoItensResponseDTO> pedidoItensList = repository.findAll().stream().map(PedidoItensResponseDTO::new).toList();
        return pedidoItensList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<PedidoItens> pedidoItens = repository.findById(id);
        if(pedidoItens.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        PedidoItensResponseDTO pedidoItensDTO = new PedidoItensResponseDTO(pedidoItens.get());
        return  ResponseEntity.ok(pedidoItensDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void savePedidoItens(@RequestBody PedidoItensRequestDTO data){

        PedidoItens pedidoItensData = new PedidoItens(data);
        repository.save(pedidoItensData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePedidoItens(@PathVariable(value = "id") Integer id, @RequestBody PedidoItensRequestDTO upData){

        Optional<PedidoItens> pedidoItens = repository.findById(id);
        if(pedidoItens.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        PedidoItens pedidoItensModel = pedidoItens.get();
        BeanUtils.copyProperties(upData, pedidoItensModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(pedidoItensModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePedidoItens(@PathVariable(value = "id") Integer id){

        Optional<PedidoItens> pedidoItens = repository.findById(id);
        if(pedidoItens.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(pedidoItens.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Pedido Itens deleted");
    }
}
