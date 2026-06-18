import { Link, useParams } from "react-router-dom";
import { useAuth } from "../../auth/useAuth";
import Breadcrumbs from "../../components/Breadcrumbs/Breadcrumbs";
import ModuleCrud from "../../components/ModuleCrud/ModuleCrud";
import {
  findPlatformResource,
  platformResources,
} from "../../services/platformResources";
import "./platform-workspace.css";

export default function PlatformWorkspacePage() {
  const { session } = useAuth();
  const { entity } = useParams();
  const resource = findPlatformResource(entity);

  if (session?.scope !== "master") {
    return (
      <section className="platform-workspace platform-workspace--empty">
        <span className="platform-workspace__eyebrow">Plataforma</span>
        <h2 className="platform-workspace__title">Acesso indisponivel</h2>
        <p className="platform-workspace__description">
          Esta area e exclusiva para sessoes master da plataforma.
        </p>
        <Link to="/app" className="platform-workspace__back">
          Voltar ao dashboard
        </Link>
      </section>
    );
  }

  if (!resource) {
    return (
      <section className="platform-workspace platform-workspace--empty">
        <span className="platform-workspace__eyebrow">Plataforma</span>
        <h2 className="platform-workspace__title">Recurso nao encontrado</h2>
        <p className="platform-workspace__description">
          O recurso solicitado nao faz parte do catalogo da camada master.
        </p>
        <Link to="/app" className="platform-workspace__back">
          Voltar ao dashboard
        </Link>
      </section>
    );
  }

  return (
    <div className="platform-workspace">
      <Breadcrumbs
        items={[
          { label: "Dashboard", to: "/app" },
          { label: "Plataforma" },
          { label: resource.label },
        ]}
      />

      <section className="platform-workspace__hero">
        <div className="platform-workspace__hero-copy">
          <span className="platform-workspace__eyebrow">Camada master</span>
          <h2 className="platform-workspace__title">{resource.label}</h2>
          <p className="platform-workspace__description">
            {resource.description}
          </p>
        </div>

        <div className="platform-workspace__meta">
          <div className="platform-workspace__meta-card">
            <span className="platform-workspace__meta-label">Categoria</span>
            <strong className="platform-workspace__meta-value">
              {resource.category}
            </strong>
          </div>

          <div className="platform-workspace__meta-card">
            <span className="platform-workspace__meta-label">Endpoint</span>
            <strong className="platform-workspace__meta-value">
              {`platform/${resource.entity}`}
            </strong>
          </div>

          <div className="platform-workspace__meta-card">
            <span className="platform-workspace__meta-label">Catalogo</span>
            <strong className="platform-workspace__meta-value">
              {platformResources.length} recursos
            </strong>
          </div>
        </div>
      </section>

      <section className="platform-workspace__panel">
        <div className="platform-workspace__panel-head">
          <div>
            <span className="platform-workspace__panel-eyebrow">Operacao</span>
            <h3 className="platform-workspace__panel-title">
              Gestao da plataforma
            </h3>
          </div>

          <p className="platform-workspace__panel-text">
            Esta interface atua sobre os endpoints master do backend.
          </p>
        </div>

        <ModuleCrud
          schema="platform"
          entity={resource.entity}
          label={resource.label}
        />
      </section>
    </div>
  );
}
