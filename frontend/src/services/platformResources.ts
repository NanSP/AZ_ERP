export type PlatformResource = {
  entity: string;
  label: string;
  description: string;
  category: string;
};

export const platformResources: PlatformResource[] = [
  {
    entity: "systemUsers",
    label: "Usuarios do sistema",
    description: "Gerencie operadores master e acessos administrativos da plataforma.",
    category: "Governanca",
  },
  {
    entity: "tenants",
    label: "Tenants",
    description: "Controle empresas provisionadas, status e dados gerais do ecossistema.",
    category: "Clientes",
  },
  {
    entity: "tenantDatabases",
    label: "Bases dos tenants",
    description: "Acompanhe bancos provisionados, conexoes e estados operacionais.",
    category: "Infraestrutura",
  },
  {
    entity: "tenantAdminUsers",
    label: "Admins dos tenants",
    description: "Gerencie os administradores iniciais vinculados a cada tenant.",
    category: "Clientes",
  },
  {
    entity: "provisioningLogs",
    label: "Logs de provisionamento",
    description: "Audite eventos tecnicos e historico das operacoes de onboarding.",
    category: "Observabilidade",
  },
];

export function findPlatformResource(entity?: string) {
  return platformResources.find((resource) => resource.entity === entity) ?? null;
}
