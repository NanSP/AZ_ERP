import { Link } from "react-router-dom";
import "./entry.css";

export default function EntryPage() {
  return (
    <main className="entry-page">
      <section className="entry-card">
        <div className="entry-badge">AZ ERP</div>

        <h1 className="entry-title">Escolha como deseja entrar</h1>
        <p className="entry-subtitle">
          Acesse o ambiente administrativo master ou entre no portal do seu
          tenant.
        </p>

        <div className="entry-actions">
          <Link to="/login" className="entry-button entry-button-master">
            Entrar no Master
          </Link>

          <Link to="/tenant-login" className="entry-button entry-button-tenant">
            Entrar no Tenant
          </Link>
        </div>

        <div className="entry-help">
          <p>
            Use <strong>Master</strong> para administracao da plataforma e
            <strong> Tenant</strong> para acesso operacional do cliente.
          </p>
        </div>
      </section>
    </main>
  );
}
