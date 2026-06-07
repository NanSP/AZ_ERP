import type { AuthSession } from "../auth/authTypes";
import type { TenantModule, TenantResource } from "./tenantModules";

export type PermissionAction = "read" | "create" | "update" | "delete";

const observedBackendAccess = new Set<string>([
  "fiscal:esocial_eventos:read",
  "fiscal:esocial_eventos:create",
  "fiscal:esocial_eventos:update",
  "fiscal:esocial_eventos:delete",
  "fiscal:efd_registros:read",
  "fiscal:efd_registros:create",
  "fiscal:efd_registros:update",
  "fiscal:efd_registros:delete",
  "fiscal:ecd_registros:read",
  "fiscal:ecd_registros:create",
  "fiscal:ecd_registros:update",
  "fiscal:ecd_registros:delete",
]);

function toSnakeCase(value: string) {
  return value
    .replace(/([a-z0-9])([A-Z])/g, "$1_$2")
    .replace(/[-\s]+/g, "_")
    .toLowerCase();
}

export function isMasterSession(session: AuthSession | null) {
  return session?.scope === "master";
}

export function getPermissionKey(
  resource: TenantResource,
  action: PermissionAction,
) {
  return `${resource.schema}:${toSnakeCase(resource.entity)}:${action}`;
}

export function getReadPermissionKey(resource: TenantResource) {
  return getPermissionKey(resource, "read");
}

export function canAccessResourceAction(
  session: AuthSession | null,
  resource: TenantResource,
  action: PermissionAction,
) {
  if (!session) {
    return false;
  }

  if (isMasterSession(session)) {
    return true;
  }

  const permissionKey = getPermissionKey(resource, action);
  const permissions = new Set(session.permissoes ?? []);
  return permissions.has(permissionKey) || observedBackendAccess.has(permissionKey);
}

export function canReadResource(
  session: AuthSession | null,
  resource: TenantResource,
) {
  return canAccessResourceAction(session, resource, "read");
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
