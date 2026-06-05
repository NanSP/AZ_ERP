import { Link, useParams } from "react-router-dom";
import Breadcrumbs from "../../components/Breadcrumbs/Breadcrumbs";
import ModuleCrud from "../../components/ModuleCrud/ModuleCrud";
import PartnersPage from "../Core/PartnersPage";
import { tenantModules } from "../../services/tenantModules";
import "./module-workspace.css";

function findResource(schema?: string, entity?: string) {
  for (const module of tenantModules) {
    const resource = module.resources.find(
      (item) => item.schema === schema && item.entity === entity,
    );

    if (resource) {
      return { module, resource };
    }
  }

  return null;
}

export default function ModuleWorkspacePage() {
  const { schema, entity } = useParams();
  const match = findResource(schema, entity);

  if (!schema || !entity || !match) {
    return (
      <section className="module-workspace module-workspace--empty">
        <span className="module-workspace__eyebrow">Modulo</span>
        <h2 className="module-workspace__title">Recurso nao encontrado</h2>
        <p className="module-workspace__description">
          O recurso solicitado nao foi localizado no catalogo interno do tenant.
        </p>
        <Link to="/app" className="module-workspace__back">
          Voltar ao dashboard
        </Link>
      </section>
    );
  }

  const { module, resource } = match;
  const isPartnersPilot =
    resource.schema === "core" && resource.entity === "parceiros";

  return (
    <div className="module-workspace">
      <Breadcrumbs
        items={[
          { label: "Dashboard", to: "/app" },
          { label: module.sigla },
          { label: resource.label },
        ]}
      />

      <section className="module-workspace__hero">
        <div className="module-workspace__hero-main">
          <div className="module-workspace__sigla">{module.sigla}</div>

          <div className="module-workspace__hero-copy">
            <span className="module-workspace__eyebrow">
              Workspace do modulo
            </span>
            <h2 className="module-workspace__title">{resource.label}</h2>
            <p className="module-workspace__description">
              {resource.description}
            </p>
          </div>
        </div>

        <div className="module-workspace__meta">
          <div className="module-workspace__meta-card">
            <span className="module-workspace__meta-label">Dominio</span>
            <strong className="module-workspace__meta-value">
              {module.title}
            </strong>
          </div>

          <div className="module-workspace__meta-card">
            <span className="module-workspace__meta-label">Schema</span>
            <strong className="module-workspace__meta-value">
              {resource.schema}
            </strong>
          </div>

          <div className="module-workspace__meta-card">
            <span className="module-workspace__meta-label">Entidade</span>
            <strong className="module-workspace__meta-value">
              {resource.entity}
            </strong>
          </div>
        </div>
      </section>

      <section className="module-workspace__panel">
        <div className="module-workspace__panel-head">
          <div>
            <span className="module-workspace__panel-eyebrow">Operacao</span>
            <h3 className="module-workspace__panel-title">Gestao do recurso</h3>
          </div>

          <p className="module-workspace__panel-text">
            Abaixo esta a camada operacional atual conectada ao backend.
          </p>
        </div>

        <div className="module-workspace__crud">
          {isPartnersPilot ? (
            <PartnersPage embedded />
          ) : (
            <ModuleCrud
              schema={resource.schema}
              entity={resource.entity}
              label={resource.label}
            />
          )}
        </div>
      </section>
    </div>
  );
}
