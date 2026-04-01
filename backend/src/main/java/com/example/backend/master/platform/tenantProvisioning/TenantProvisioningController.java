package com.example.backend.master.platform.tenantProvisioning;

import com.example.backend.master.platform.tenantProvisioning.services.TenantProvisioningOrchestratorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/platform/tenantProvisioning")
public class TenantProvisioningController {

    private final TenantProvisioningOrchestratorService service;

    public TenantProvisioningController(TenantProvisioningOrchestratorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> provision(@RequestBody TenantProvisioningRequestDTO data) {
        try {
            TenantProvisioningResponseDTO response = service.provision(data);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}

