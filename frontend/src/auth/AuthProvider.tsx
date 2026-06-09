import { useState, type ReactNode } from "react";

import type {
  AuthSession,
  ChangePasswordPayload,
  MasterLoginPayload,
  TenantLoginPayload,
} from "./authTypes";
import { AuthContext } from "./authContext";

import { clearSession, loadSession, saveSession } from "./authStorage";
import {
  changeMasterPassword,
  changeTenantPassword,
  loginMaster,
  loginTenant,
} from "../services/authService";

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<AuthSession | null>(() => loadSession());

  const loginMasterAction = async (payload: MasterLoginPayload) => {
    const next = await loginMaster(payload);
    saveSession(next);
    setSession(next);
  };

  const loginTenantAction = async (payload: TenantLoginPayload) => {
    const next = await loginTenant(payload);
    saveSession(next);
    setSession(next);
  };

  const logout = () => {
    clearSession();
    setSession(null);
  };

  const changePassword = async (payload: ChangePasswordPayload) => {
    if (!session) throw new Error("Sessao nao encontrada");

    if (session.scope === "master") {
      await changeMasterPassword(session.token, payload);
    } else {
      await changeTenantPassword(session.token, payload);
    }

    const updated = { ...session, passwordChangeRequired: false };
    saveSession(updated);
    setSession(updated);
  };

  const value = {
    session,
    isAuthenticated: !!session?.token,
    loginMasterAction,
    loginTenantAction,
    logout,
    changePassword,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
