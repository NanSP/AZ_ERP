import {
  createContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from "react";

import type {
  AuthSession,
  ChangePasswordPayload,
  MasterLoginPayload,
  TenantLoginPayload,
} from "./authTypes";

import { clearSession, loadSession, saveSession } from "./authStorage";
import {
  changeMasterPassword,
  changeTenantPassword,
  loginMaster,
  loginTenant,
} from "../services/authService";

type AuthContextValue = {
  session: AuthSession | null;
  isAuthenticated: boolean;
  loginMasterAction: (payload: MasterLoginPayload) => Promise<void>;
  loginTenantAction: (payload: TenantLoginPayload) => Promise<void>;
  logout: () => void;
  changePassword: (payload: ChangePasswordPayload) => Promise<void>;
};

export const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<AuthSession | null>(null);

  useEffect(() => {
    setSession(loadSession());
  }, []);

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

  const value = useMemo(
    () => ({
      session,
      isAuthenticated: !!session?.token,
      loginMasterAction,
      loginTenantAction,
      logout,
      changePassword,
    }),
    [session],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
