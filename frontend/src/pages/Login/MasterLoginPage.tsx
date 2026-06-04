import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../auth/useAuth";
import AuthLayout from "../layouts/AuthLayout";

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
    } catch {
      setErro("Nao foi possivel autenticar no master.");
    }
  }

  return (
    <AuthLayout>
      <h1>Login Master</h1>
      <form onSubmit={handleSubmit}>
        <input
          value={login}
          onChange={(e) => setLogin(e.target.value)}
          placeholder="Login"
        />
        <input
          value={senha}
          onChange={(e) => setSenha(e.target.value)}
          placeholder="Senha"
          type="password"
        />
        <button type="submit">Entrar</button>
      </form>
      {erro && <p>{erro}</p>}
    </AuthLayout>
  );
}
