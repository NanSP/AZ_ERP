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
  logoutMaster,
  logoutTenant,
  loginMaster,
  loginTenant,
  getMasterMe,
  getTenantMe,
} from "../services/authService";

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<AuthSession | null>(() => loadSession());
  const [ready, setReady] = useState(false);

  function commitSession(next: AuthSession | null) {
    if (next) {
      saveSession(next);
      setSession(next);
      return;
    }

    clearSession();
    setSession(null);
  }

  // Ao montar, tentamos popular a sessão consultando o endpoint /auth/me.
  useEffect(() => {
    let mounted = true;

    (async () => {
      try {
        const savedSession = loadSession();
        const me = savedSession?.scope === "tenant"
          ? await getTenantMe()
          : await getMasterMe();
        if (mounted) {
          commitSession(me);
        }
      } catch {
        if (mounted) {
          commitSession(null);
        }
      } finally {
        if (mounted) setReady(true);
      }
    })();

    return () => {
      mounted = false;
    };
  }, []);

  const loginMasterAction = async (payload: MasterLoginPayload) => {
    const next = await loginMaster(payload);
    const me = await getMasterMe();
    commitSession(me ?? next);
  };

  const loginTenantAction = async (payload: TenantLoginPayload) => {
    const next = await loginTenant(payload);
    const me = await getTenantMe();
    commitSession(me ?? next);
  };

  const logout = async () => {
    try {
      if (session?.scope === "master") {
        await logoutMaster();
      } else if (session?.scope === "tenant") {
        await logoutTenant();
      }
    } catch {
      // Mesmo com falha remota, limpamos o estado local para encerrar a sessão no frontend.
    } finally {
      commitSession(null);
    }
  };

  const changePassword = async (payload: ChangePasswordPayload) => {
    if (!session) throw new Error("Sessao nao encontrada");

    if (session.scope === "master") {
      await changeMasterPassword(payload);
    } else {
      await changeTenantPassword(payload);
    }

    const me = session.scope === "tenant"
      ? await getTenantMe()
      : await getMasterMe();
    commitSession(
      me
        ? { ...me, passwordChangeRequired: false }
        : { ...session, passwordChangeRequired: false },
    );
  };

  if (!ready) return null;

  const value = {
    session,
    isAuthenticated: !!session,
    loginMasterAction,
    loginTenantAction,
    logout,
    changePassword,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
