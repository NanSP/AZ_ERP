import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../auth/useAuth";
import AuthLayout from "../layouts/AuthLayout";

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
      <h1>Troca de Senha</h1>
      <form onSubmit={handleSubmit}>
        <input
          value={senhaAtual}
          onChange={(e) => setSenhaAtual(e.target.value)}
          placeholder="Senha atual"
          type="password"
        />
        <input
          value={novaSenha}
          onChange={(e) => setNovaSenha(e.target.value)}
          placeholder="Nova senha"
          type="password"
        />
        <button type="submit">Alterar senha</button>
      </form>
      {erro && <p>{erro}</p>}
    </AuthLayout>
  );
}
