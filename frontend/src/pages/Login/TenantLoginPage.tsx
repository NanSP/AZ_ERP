import { AxiosError } from "axios";
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../auth/useAuth";
import AuthLayout from "../../layouts/AuthLayout";
import { forgotTenantPassword } from "../../services/authService";

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
  const [forgotOpen, setForgotOpen] = useState(false);
  const [forgotTenantCode, setForgotTenantCode] = useState("");
  const [forgotIdentity, setForgotIdentity] = useState("");
  const [forgotMessage, setForgotMessage] = useState("");
  const [forgotLoading, setForgotLoading] = useState(false);

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

  async function handleForgotPassword(event: React.FormEvent) {
    event.preventDefault();

    const currentTenantCode = forgotTenantCode.trim() || tenantCode.trim();
    const currentIdentity = forgotIdentity.trim() || login.trim();

    if (!currentTenantCode) {
      setForgotMessage("Informe o codigo do tenant para orientar a redefinicao.");
      return;
    }

    if (!currentIdentity) {
      setForgotMessage("Informe seu login ou email para orientar a redefinicao.");
      return;
    }

    setForgotLoading(true);

    try {
      const response = await forgotTenantPassword({
        tenantCode: currentTenantCode,
        identificador: currentIdentity,
      });

      const contactDetails = [
        response.emailResponsavel
          ? `Email responsavel: ${response.emailResponsavel}.`
          : "",
        response.telefoneResponsavel
          ? `Telefone responsavel: ${response.telefoneResponsavel}.`
          : "",
      ]
        .filter(Boolean)
        .join(" ");

      setForgotMessage(
        [response.mensagem, contactDetails].filter(Boolean).join(" ").trim(),
      );
    } catch (error) {
      setForgotMessage(
        getAuthErrorMessage(
          error,
          "Nao foi possivel iniciar a orientacao de recuperacao de senha.",
        ),
      );
    } finally {
      setForgotLoading(false);
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

      <div className="auth-secondary">
        <button
          type="button"
          className="auth-secondary__toggle"
          onClick={() => {
            setForgotOpen((current) => !current);
            setForgotMessage("");
          }}
        >
          Esqueci a senha
        </button>

        {forgotOpen ? (
          <form className="auth-secondary__panel" onSubmit={handleForgotPassword}>
            <p className="auth-secondary__text">
              Informe os dados abaixo para orientar a redefinicao com o administrador do tenant.
            </p>

            <div className="auth-field">
              <label className="auth-label" htmlFor="forgot-tenant-code">
                Codigo do tenant
              </label>
              <input
                id="forgot-tenant-code"
                className="auth-input"
                value={forgotTenantCode}
                onChange={(e) => setForgotTenantCode(e.target.value)}
                placeholder={tenantCode || "Ex.: TENANT01"}
              />
            </div>

            <div className="auth-field">
              <label className="auth-label" htmlFor="forgot-identity">
                Login ou email
              </label>
              <input
                id="forgot-identity"
                className="auth-input"
                value={forgotIdentity}
                onChange={(e) => setForgotIdentity(e.target.value)}
                placeholder={login || "Seu login ou email"}
              />
            </div>

            <button className="auth-submit auth-submit--secondary" type="submit">
              {forgotLoading ? "Consultando..." : "Mostrar orientacao"}
            </button>

            {forgotMessage ? (
              <p className="auth-secondary__message">{forgotMessage}</p>
            ) : null}
          </form>
        ) : null}
      </div>

      {erro && <p className="auth-error">{erro}</p>}

      <p className="auth-helper">
        Precisa acessar a camada administrativa?{" "}
        <Link to="/login">Ir para o login master</Link>
      </p>
    </AuthLayout>
  );
}
