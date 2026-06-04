import { api } from "./api";

import type {
  AuthSession,
  ChangePasswordPayload,
  MasterLoginPayload,
  TenantLoginPayload,
} from "../auth/authTypes";

export async function loginMaster(
  payload: MasterLoginPayload,
): Promise<AuthSession> {
  const { data } = await api.post("/auth/login", payload);

  return {
    token: data.token,
    scope: data.scope,
    login: data.login,
    userId: data.userId,
    role: data.role,
    passwordChangeRequired: data.passwordChangeRequired ?? false,
  };
}

export async function loginTenant(
  payload: TenantLoginPayload,
): Promise<AuthSession> {
  const { data } = await api.post("/tenant/auth/login", payload);

  return {
    token: data.token,
    scope: data.scope,
    login: data.login,
    userId: data.userId,
    role: data.role,
    tenantId: data.tenantId,
    tenantCode: data.tenantCode,
    passwordChangeRequired: data.passwordChangeRequired ?? false,
  };
}

export async function changeMasterPassword(
  token: string,
  payload: ChangePasswordPayload,
): Promise<void> {
  await api.post("/auth/change-password", payload, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
}

export async function changeTenantPassword(
  token: string,
  payload: ChangePasswordPayload,
): Promise<void> {
  await api.post("/tenant/auth/change-password", payload, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
}
