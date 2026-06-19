export type PlatformResource = {
  entity: string;
  label: string;
  description: string;
  category: string;
  endpoint?: string;
  view?: "crud" | "provisioning";
};

export const platformResources: PlatformResource[] = [
  {
    entity: "tenantProvisioning",
    label: "Provisionamento de tenant",
    description: "Execute o onboarding completo de um tenant com criacao de base fisica e administrador inicial.",
    category: "Onboarding",
    endpoint: "platform/tenantProvisioning",
    view: "provisioning",
  },
  {
    entity: "systemUsers",
    label: "Usuarios do sistema",
    description: "Gerencie operadores master e acessos administrativos da plataforma.",
    category: "Governanca",
    endpoint: "platform/systemUsers",
    view: "crud",
  },
  {
    entity: "tenants",
    label: "Tenants",
    description: "Controle empresas provisionadas, status e dados gerais do ecossistema.",
    category: "Clientes",
    endpoint: "platform/tenants",
    view: "crud",
  },
  {
    entity: "tenantDatabases",
    label: "Bases dos tenants",
    description: "Acompanhe o registro tecnico das bases provisionadas e os estados operacionais controlados pelo backend.",
    category: "Infraestrutura",
    endpoint: "platform/tenantDatabases",
    view: "crud",
  },
  {
    entity: "tenantAdminUsers",
    label: "Admins dos tenants",
    description: "Gerencie os administradores iniciais vinculados a cada tenant.",
    category: "Clientes",
    endpoint: "platform/tenantAdminUsers",
    view: "crud",
  },
  {
    entity: "provisioningLogs",
    label: "Logs de provisionamento",
    description: "Audite eventos tecnicos e historico das operacoes de onboarding.",
    category: "Observabilidade",
    endpoint: "platform/provisioningLogs",
    view: "crud",
  },
];

export function findPlatformResource(entity?: string) {
  return platformResources.find((resource) => resource.entity === entity) ?? null;
}
