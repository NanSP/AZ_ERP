package com.example.backend.master.platform.templateMigration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/platform/templateMigration")
public class TemplateMigrationController {

    private final TemplateMigrationService service;

    public TemplateMigrationController(TemplateMigrationService service) {
        this.service = service;
    }

    @PostMapping("/migrate")
    public ResponseEntity<?> migrate(@RequestParam Long systemUserId) {
        try {
            service.migrateTemplate(systemUserId);
            return ResponseEntity.ok("Migrations do template aplicadas com sucesso");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao migrar template: " + ex.getMessage());
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestParam Long systemUserId) {
        try {
            service.validateTemplate(systemUserId);
            return ResponseEntity.ok("Template validado com sucesso");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao validar template: " + ex.getMessage());
        }
    }

    @GetMapping("/info")
    public ResponseEntity<?> info(@RequestParam Long systemUserId) {
        try {
            return ResponseEntity.ok(service.infoTemplate(systemUserId));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao consultar template: " + ex.getMessage());
        }
    }
}
