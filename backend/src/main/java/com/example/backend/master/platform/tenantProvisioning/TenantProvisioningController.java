package com.example.backend.master.platform.tenantProvisioning;

import com.example.backend.master.platform.tenantProvisioning.services.TenantProvisioningService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/platform/tenantProvisioning")
public class TenantProvisioningController {

    private final TenantProvisioningService service;

    public TenantProvisioningController(TenantProvisioningService service) {
        this.service = service;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
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
