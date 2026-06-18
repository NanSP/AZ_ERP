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

export default function MasterLoginPage() {
  const navigate = useNavigate();
  const { loginMasterAction } = useAuth();
  const [login, setLogin] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState("");

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setErro("");

    try {
      await loginMasterAction({ login, senha });
      navigate("/app", { replace: true });
    } catch (error) {
      setErro(getAuthErrorMessage(error, "Nao foi possivel autenticar no master."));
    }
  }

  return (
    <AuthLayout>
      <div className="auth-badge">Acesso master</div>
      <h1 className="auth-title">Entrar na camada administrativa</h1>
      <p className="auth-subtitle">
        Acesse tenants, configurações e operações centrais da plataforma.
      </p>

      <form className="auth-form" onSubmit={handleSubmit}>
        <div className="auth-field">
          <label className="auth-label" htmlFor="master-login">
            Login
          </label>
          <input
            id="master-login"
            className="auth-input"
            value={login}
            onChange={(e) => setLogin(e.target.value)}
            placeholder="Seu login master"
          />
        </div>

        <div className="auth-field">
          <label className="auth-label" htmlFor="master-password">
            Senha
          </label>
          <input
            id="master-password"
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
        Precisa acessar um ambiente de cliente?{" "}
        <Link to="/tenant-login">Ir para o login do tenant</Link>
      </p>
    </AuthLayout>
  );
}
