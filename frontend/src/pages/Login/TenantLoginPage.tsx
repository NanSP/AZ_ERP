import { AxiosError } from "axios";
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../auth/useAuth";
import AuthLayout from "../../layouts/AuthLayout";

function getAuthErrorMessage(error: unknown, fallback: string) {
  if (error instanceof AxiosError) {
    const message = error.response?.data?.message;
    if (typeof message === "string" && message.trim()) {
      return message;
    }
  }

  return fallback;
}

export default function TenantLoginPage() {
  const navigate = useNavigate();
  const { loginTenantAction } = useAuth();
  const [tenantCode, setTenantCode] = useState("");
  const [login, setLogin] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState("");

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setErro("");

    try {
      await loginTenantAction({ tenantCode, login, senha });
      navigate("/app", { replace: true });
    } catch (error) {
      setErro(getAuthErrorMessage(error, "Nao foi possivel autenticar no tenant."));
    }
  }

  return (
    <AuthLayout>
      <div className="auth-badge auth-badge--light">Acesso tenant</div>
      <h1 className="auth-title">Entrar no ambiente operacional</h1>
      <p className="auth-subtitle">
        Informe o código do tenant e siga para a rotina do cliente.
      </p>

      <form className="auth-form" onSubmit={handleSubmit}>
        <div className="auth-field">
          <label className="auth-label" htmlFor="tenant-code">
            Código do tenant
          </label>
          <input
            id="tenant-code"
            className="auth-input"
            value={tenantCode}
            onChange={(e) => setTenantCode(e.target.value)}
            placeholder="Ex.: TENANT01"
          />
        </div>

        <div className="auth-field">
          <label className="auth-label" htmlFor="tenant-login">
            Login
          </label>
          <input
            id="tenant-login"
            className="auth-input"
            value={login}
            onChange={(e) => setLogin(e.target.value)}
            placeholder="Seu usuario"
          />
        </div>

        <div className="auth-field">
          <label className="auth-label" htmlFor="tenant-password">
            Senha
          </label>
          <input
            id="tenant-password"
            className="auth-input"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            placeholder="Sua senha"
            type="password"
          />
        </div>

        <button className="auth-submit" type="submit">
          Entrar
        </button>
      </form>

      {erro && <p className="auth-error">{erro}</p>}

      <p className="auth-helper">
        Precisa acessar a camada administrativa?{" "}
        <Link to="/login">Ir para o login master</Link>
      </p>
    </AuthLayout>
  );
}
