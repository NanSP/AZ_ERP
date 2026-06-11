import { api } from "./api";

import type {
  AuthSession,
  ChangePasswordPayload,
  MasterLoginPayload,
  TenantLoginPayload,
} from "../auth/authTypes";

function normalizeSession(data: Record<string, unknown>): AuthSession {
  return {
    token: typeof data.token === "string" ? data.token : undefined,
    scope: data.scope === "master" ? "master" : "tenant",
    login: String(data.login ?? ""),
    userId: Number(data.userId ?? 0),
    role: data.role == null ? undefined : String(data.role),
    tenantId:
      typeof data.tenantId === "number" ? data.tenantId : undefined,
    tenantCode:
      data.tenantCode == null ? undefined : String(data.tenantCode),
    perfis: Array.isArray(data.perfis)
      ? data.perfis.map((item) => String(item))
      : undefined,
    permissoes: Array.isArray(data.permissoes)
      ? data.permissoes.map((item) => String(item))
      : undefined,
    passwordChangeRequired: Boolean(data.passwordChangeRequired ?? false),
  };
}

export async function loginMaster(
  payload: MasterLoginPayload,
): Promise<AuthSession> {
  const { data } = await api.post("/auth/login", payload);
  return normalizeSession(data as Record<string, unknown>);
}

export async function loginTenant(
  payload: TenantLoginPayload,
): Promise<AuthSession> {
  const { data } = await api.post("/tenant/auth/login", payload);
  return normalizeSession(data as Record<string, unknown>);
}

export async function changeMasterPassword(
  payload: ChangePasswordPayload,
): Promise<void> {
  await api.post("/auth/change-password", payload);
}

export async function changeTenantPassword(
  payload: ChangePasswordPayload,
): Promise<void> {
  await api.post("/tenant/auth/change-password", payload);
}

export async function logoutMaster(): Promise<void> {
  await api.post("/auth/logout");
}

export async function logoutTenant(): Promise<void> {
  await api.post("/tenant/auth/logout");
}

export async function getMe(): Promise<AuthSession | null> {
  try {
    const { data } = await api.get("/auth/me");
    if (!data) return null;
    return normalizeSession(data as Record<string, unknown>);
  } catch {
    return null;
  }
}
