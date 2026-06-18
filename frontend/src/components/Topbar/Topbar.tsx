import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../auth/useAuth";
import "./topbar.css";

export default function Topbar() {
  const navigate = useNavigate();
  const { session, logout } = useAuth();

  const isMasterScope = session?.scope === "master";
  const scopeLabel = isMasterScope ? "Master" : "Tenant";
  const tenantLabel =
    session?.scope === "tenant" ? session.tenantCode || "Tenant" : "Plataforma";

  async function handleLogout() {
    await logout();
    navigate("/", { replace: true });
  }

  return (
    <div className="topbar">
      <div className="topbar__intro">
        <span className="topbar__eyebrow">ERP Workspace</span>
        <h1 className="topbar__title">
          {isMasterScope ? "Operacao da plataforma" : "Operacao integrada"}
        </h1>
        <p className="topbar__subtitle">
          {isMasterScope
            ? "Gerencie tenants, operadores globais e servicos centrais do ecossistema."
            : "Acesse modulos, acompanhe processos e navegue pelo ecossistema do ERP."}
        </p>
      </div>

      <div className="topbar__actions">
        <Link to="/app" className="topbar__ghost">
          Dashboard
        </Link>

        <div className="topbar__session">
          <div className="topbar__session-badge">{scopeLabel}</div>

          <div className="topbar__session-meta">
            <strong className="topbar__session-login">{session?.login}</strong>
            <span className="topbar__session-scope">{tenantLabel}</span>
          </div>
        </div>

        <button
          type="button"
          className="topbar__button"
          onClick={() => void handleLogout()}
        >
          Sair
        </button>
      </div>
    </div>
  );
}
