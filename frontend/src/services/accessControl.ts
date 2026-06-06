import type { AuthSession } from "../auth/authTypes";
import type { TenantModule, TenantResource } from "./tenantModules";

function toSnakeCase(value: string) {
  return value
    .replace(/([a-z0-9])([A-Z])/g, "$1_$2")
    .replace(/[-\s]+/g, "_")
    .toLowerCase();
}

export function isMasterSession(session: AuthSession | null) {
  return session?.scope === "master";
}

export function getReadPermissionKey(resource: TenantResource) {
  return `${resource.schema}:${toSnakeCase(resource.entity)}:read`;
}

export function canReadResource(
  session: AuthSession | null,
  resource: TenantResource,
) {
  if (isMasterSession(session)) {
    return true;
  }

  const permissions = new Set(session?.permissoes ?? []);
  return permissions.has(getReadPermissionKey(resource));
}

export function filterModulesByReadAccess(
  modules: TenantModule[],
  session: AuthSession | null,
) {
  return modules
    .map((module) => ({
      ...module,
      resources: module.resources.filter((resource) =>
        canReadResource(session, resource),
      ),
    }))
    .filter((module) => module.resources.length > 0);
}

export function getFirstReadableResource(
  modules: TenantModule[],
  session: AuthSession | null,
) {
  const filteredModules = filterModulesByReadAccess(modules, session);
  return filteredModules[0]?.resources[0] ?? null;
}
