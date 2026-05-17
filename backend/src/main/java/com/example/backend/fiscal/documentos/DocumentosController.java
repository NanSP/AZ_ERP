package com.example.backend.fiscal.documentos;

import com.example.backend.core.parceiros.Parceiros;
import com.example.backend.core.parceiros.ParceirosRepository;
import com.example.backend.sd.pedidos.Pedidos;
import com.example.backend.sd.pedidos.PedidosRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fiscal/documentos")
public class DocumentosController {

    private final DocumentosRepository repository;
    private final PedidosRepository pedidosRepository;
    private final ParceirosRepository parceirosRepository;

    public DocumentosController(
            DocumentosRepository repository,
            PedidosRepository pedidosRepository,
            ParceirosRepository parceirosRepository
    ) {
        this.repository = repository;
        this.pedidosRepository = pedidosRepository;
        this.parceirosRepository = parceirosRepository;
    }

    @GetMapping
    public List<DocumentosResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(DocumentosResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> ResponseEntity.ok(new DocumentosResponseDTO(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> saveDocumentos(@RequestBody DocumentosRequestDTO data) {
        try {
            Pedidos pedido = data.pedido() != null
                    ? pedidosRepository.findById(data.pedido())
                    .orElseThrow(() -> new RuntimeException("Pedido nao encontrado"))
                    : null;

            Parceiros cliente = data.cliente() != null
                    ? parceirosRepository.findById(data.cliente())
                    .orElseThrow(() -> new RuntimeException("Cliente nao encontrado"))
                    : null;

            Documentos entity = new Documentos();
            entity.setTipoDocumento(data.tipoDocumento());
            entity.setNumero(data.numero());
            entity.setSerie(data.serie());
            entity.setChaveAcesso(data.chaveAcesso());
            entity.setDataEmissao(data.dataEmissao());
            entity.setPedido(pedido);
            entity.setCliente(cliente);
            entity.setValorTotal(data.valorTotal());
            entity.setStatus(data.status());
            entity.setXml_file(data.xml_file());
            entity.setCreatedAt(data.createdAt());

            Documentos saved = repository.save(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(new DocumentosResponseDTO(saved));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDocumentos(@PathVariable Integer id, @RequestBody DocumentosRequestDTO data) {
        try {
            Documentos entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Documento nao encontrado"));

            Pedidos pedido = data.pedido() != null
                    ? pedidosRepository.findById(data.pedido())
                    .orElseThrow(() -> new RuntimeException("Pedido nao encontrado"))
                    : null;

            Parceiros cliente = data.cliente() != null
                    ? parceirosRepository.findById(data.cliente())
                    .orElseThrow(() -> new RuntimeException("Cliente nao encontrado"))
                    : null;

            entity.setTipoDocumento(data.tipoDocumento());
            entity.setNumero(data.numero());
            entity.setSerie(data.serie());
            entity.setChaveAcesso(data.chaveAcesso());
            entity.setDataEmissao(data.dataEmissao());
            entity.setPedido(pedido);
            entity.setCliente(cliente);
            entity.setValorTotal(data.valorTotal());
            entity.setStatus(data.status());
            entity.setXml_file(data.xml_file());
            entity.setCreatedAt(data.createdAt());

            Documentos updated = repository.save(entity);
            return ResponseEntity.ok(new DocumentosResponseDTO(updated));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocumentos(@PathVariable Integer id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.ok("Documento deleted");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nao encontrado"));
    }
}