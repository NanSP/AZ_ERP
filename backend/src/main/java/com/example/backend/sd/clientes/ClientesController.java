package com.example.backend.sd.clientes;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("clientes")
public class ClientesController {

    @Autowired
    private ClientesRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<ClientesResponseDTO> getAll(){

        List<ClientesResponseDTO> clientesList = repository.findAll().stream().map(ClientesResponseDTO::new).toList();
        return clientesList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Clientes> clientes = repository.findById(id);
        if(clientes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        ClientesResponseDTO clientesDTO = new ClientesResponseDTO(clientes.get());
        return  ResponseEntity.ok(clientesDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveClientes(@RequestBody ClientesRequestDTO data){

        Clientes clientesData = new Clientes(data);
        repository.save(clientesData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateClientes(@PathVariable(value = "id") Integer id, @RequestBody ClientesRequestDTO upData){

        Optional<Clientes> clientes = repository.findById(id);
        if(clientes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Clientes clientesModel = clientes.get();
        BeanUtils.copyProperties(upData, clientesModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(clientesModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClientes(@PathVariable(value = "id") Integer id){

        Optional<Clientes> clientes = repository.findById(id);
        if(clientes.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(clientes.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Cliente deleted");
    }
}
