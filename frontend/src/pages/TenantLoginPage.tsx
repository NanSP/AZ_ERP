import { useState } from "react";
import { useNavigate } from "react-router-dom";
import AuthLayout from "../layouts/AuthLayout";
import { useAuth } from "../auth/useAuth";

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
      navigate("/", { replace: true });
    } catch (error) {
      setErro("Nao foi possivel autenticar no tenant.");
    }
  }

  return (
    <AuthLayout>
      <h1>Login Tenant</h1>
      <form onSubmit={handleSubmit}>
        <input
          value={tenantCode}
          onChange={(e) => setTenantCode(e.target.value)}
          placeholder="Codigo do tenant"
        />
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
