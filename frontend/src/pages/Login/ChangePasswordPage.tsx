import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../auth/useAuth";
import AuthLayout from "../../layouts/AuthLayout";

export default function ChangePasswordPage() {
  const navigate = useNavigate();
  const { changePassword } = useAuth();
  const [senhaAtual, setSenhaAtual] = useState("");
  const [novaSenha, setNovaSenha] = useState("");
  const [erro, setErro] = useState("");

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setErro("");

    try {
      await changePassword({ senhaAtual, novaSenha });
      navigate("/app", { replace: true });
    } catch {
      setErro("Nao foi possivel alterar a senha");
    }
  }

  return (
    <AuthLayout>
      <div className="auth-badge">Primeiro acesso</div>
      <h1 className="auth-title">Atualize sua senha</h1>
      <p className="auth-subtitle">
        Antes de continuar, defina uma nova credencial para sua sessão.
      </p>

      <form className="auth-form" onSubmit={handleSubmit}>
        <div className="auth-field">
          <label className="auth-label" htmlFor="current-password">
            Senha atual
          </label>
          <input
            id="current-password"
            className="auth-input"
            value={senhaAtual}
            onChange={(e) => setSenhaAtual(e.target.value)}
            placeholder="Senha atual"
            type="password"
          />
        </div>

        <div className="auth-field">
          <label className="auth-label" htmlFor="new-password">
            Nova senha
          </label>
          <input
            id="new-password"
            className="auth-input"
            value={novaSenha}
            onChange={(e) => setNovaSenha(e.target.value)}
            placeholder="Nova senha"
            type="password"
          />
        </div>

        <button className="auth-submit" type="submit">
          Alterar senha
        </button>
      </form>

      {erro && <p className="auth-error">{erro}</p>}
    </AuthLayout>
  );
}
