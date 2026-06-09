import { createContext } from "react";
import type {
  AuthSession,
  ChangePasswordPayload,
  MasterLoginPayload,
  TenantLoginPayload,
} from "./authTypes";

export type AuthContextValue = {
  session: AuthSession | null;
  isAuthenticated: boolean;
  loginMasterAction: (payload: MasterLoginPayload) => Promise<void>;
  loginTenantAction: (payload: TenantLoginPayload) => Promise<void>;
  logout: () => void;
  changePassword: (payload: ChangePasswordPayload) => Promise<void>;
};

export const AuthContext = createContext<AuthContextValue | null>(null);
