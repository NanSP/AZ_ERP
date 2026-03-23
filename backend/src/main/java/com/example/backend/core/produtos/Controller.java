package com.example.backend.core.produtos;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("com/example/backend/core/produtos")
public class Controller {

    @Autowired
    private ProdutosRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<ProdutosResponseDTO> getAll(){

        List<ProdutosResponseDTO> produtosList = repository.findAll().stream().map(ProdutosResponseDTO::new).toList();
        return produtosList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Produtos> produtos = repository.findById(id);
        if(produtos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        ProdutosResponseDTO produtosDTO = new ProdutosResponseDTO(produtos.get());
        return  ResponseEntity.ok(produtosDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveProduto(@RequestBody ProdutosRequestDTO data){

        Produtos produtosData = new Produtos(data);
        repository.save(produtosData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduto(@PathVariable(value = "id") Integer id, @RequestBody ProdutosRequestDTO upData){

        Optional<Produtos> produtos = repository.findById(id);
        if(produtos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Produtos produtosModel = produtos.get();
        BeanUtils.copyProperties(upData, produtosModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(produtosModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduto(@PathVariable(value = "id") Integer id){

        Optional<Produtos> produtos = repository.findById(id);
        if(produtos.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(produtos.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Produto deleted");
    }
}
