package com.example.backend.master.platform.templateMigration;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/platform/templateMigration")
public class TemplateMigrationController {

    private final TemplateMigrationService service;

    public TemplateMigrationController(TemplateMigrationService service) {
        this.service = service;
    }

    @PostMapping("/migrate")
    @ResponseStatus(HttpStatus.OK)
    public String migrate(@RequestParam Long systemUserId) {
        service.migrateTemplate(systemUserId);
        return "Migrations do template aplicadas com sucesso";
    }

    @GetMapping("/validate")
    @ResponseStatus(HttpStatus.OK)
    public String validate(@RequestParam Long systemUserId) {
        service.validateTemplate(systemUserId);
        return "Template validado com sucesso";
    }

    @GetMapping("/info")
    @ResponseStatus(HttpStatus.OK)
    public String info(@RequestParam Long systemUserId) {
        return service.infoTemplate(systemUserId);
    }
}
