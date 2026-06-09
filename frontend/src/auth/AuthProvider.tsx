import { useState, type ReactNode, useEffect } from "react";

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
  getMe,
} from "../services/authService";
import { setAuthToken } from "../services/api";

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<AuthSession | null>(() => loadSession());

  // Ao montar, tentamos popular a sessão consultando o endpoint /auth/me.
  useEffect(() => {
    let mounted = true;

    (async () => {
      try {
        const me = await getMe();
        if (mounted && me) {
          saveSession(me);
          setAuthToken(me.token ?? null);
          setSession(me);
        }
      } catch {
        // sem sessão — estado inicial fica nulo
      } finally {
        if (mounted) setReady(true);
      }
    })();

    return () => {
      mounted = false;
    };
  }, []);

  const [ready, setReady] = useState(false);

  const loginMasterAction = async (payload: MasterLoginPayload) => {
    const next = await loginMaster(payload);
    // Caso o backend retorne token (legacy), armazenamos em memória.
    saveSession(next);
    setAuthToken(next.token ?? null);
    setSession(next);
  };

  const loginTenantAction = async (payload: TenantLoginPayload) => {
    const next = await loginTenant(payload);
    saveSession(next);
    setAuthToken(next.token ?? null);
    setSession(next);
  };

  const logout = () => {
    // Chamar endpoint de logout seria ideal para revogar refresh token no servidor.
    clearSession();
    setAuthToken(null);
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

  if (!ready) return null;

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
