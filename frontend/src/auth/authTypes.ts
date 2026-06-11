export type AuthScope = "master" | "tenant";

export type AuthSession = {
  token?: string;
  scope: AuthScope;
  login: string;
  userId: number;
  role?: string;
  tenantId?: number;
  tenantCode?: string;
  perfis?: string[];
  permissoes?: string[];
  passwordChangeRequired: boolean;
};

export type MasterLoginPayload = {
  login: string;
  senha: string;
};

export type TenantLoginPayload = {
  tenantCode: string;
  login: string;
  senha: string;
};

export type ChangePasswordPayload = {
  senhaAtual: string;
  novaSenha: string;
};
