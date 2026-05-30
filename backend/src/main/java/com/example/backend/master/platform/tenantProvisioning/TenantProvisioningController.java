package com.example.backend.master.platform.tenantProvisioning;

import com.example.backend.master.platform.tenantProvisioning.services.TenantProvisioningOrchestratorService;
import org.springframework.http.HttpStatus;
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
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    public TenantProvisioningResponseDTO provision(@RequestBody TenantProvisioningRequestDTO data) {
        return service.provision(data);
    }
}

