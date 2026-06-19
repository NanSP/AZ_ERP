import { api } from "./api";

export type TenantProvisioningPayload = {
  systemUserId: number;
  codigo: string;
  nome: string;
  nomeFantasia: string | null;
  documento: string | null;
  tipoDocumento: string | null;
  emailResponsavel: string | null;
  telefoneResponsavel: string | null;
  plano: string;
  databaseName: string;
  dbHost: string;
  dbPort: number;
  dbUsername: string;
  dbPassword: string;
  adminNome: string;
  adminEmail: string;
  adminLogin: string;
  adminSenha: string;
};

export type TenantProvisioningResponse = {
  tenantId: number;
  tenantCodigo: string;
  tenantNome: string;
  tenantStatus: string;
  tenantDatabaseId: number;
  databaseName: string;
  provisionStatus: string;
  tenantAdminUserId: number;
  adminNome: string;
  adminEmail: string;
  adminLogin: string;
  provisionedAt: string | null;
  etapasExecutadas: string[];
};

export function provisionTenant(payload: TenantProvisioningPayload) {
  return api.post<TenantProvisioningResponse>("/platform/tenantProvisioning", payload);
}
