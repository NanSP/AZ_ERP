import { Link } from "react-router-dom";
import { useAuth } from "../../auth/useAuth";
import {
  filterModulesByReadAccess,
  getFirstReadableResource,
} from "../../services/accessControl";
import ModuleGrid from "../../components/ModuleGrid/ModuleGrid";
import { platformResources } from "../../services/platformResources";
import { tenantModules } from "../../services/tenantModules";
import "./dashboard.css";

export default function DashboardPage() {
  const { session } = useAuth();
  const isMasterScope = session?.scope === "master";
  const visibleModules = filterModulesByReadAccess(tenantModules, session);
  const featuredModules = visibleModules.slice(0, 6);
  const firstResource = getFirstReadableResource(tenantModules, session);

  if (isMasterScope) {
    const highlights = [
      {
        label: "Recursos master",
        value: platformResources.length.toString().padStart(2, "0"),
        description: "Operacoes centrais disponiveis na camada de plataforma.",
      },
      {
        label: "Escopo ativo",
        value: "MST",
        description: "Sessao conectada como administracao global do produto.",
      },
      {
        label: "Ambiente",
        value: "Core",
        description: "Workspace reservado para governanca e provisionamento.",
      },
    ];

    return (
      <div className="dashboard-page">
        <section className="dashboard-hero">
          <div className="dashboard-hero__content">
            <span className="dashboard-hero__eyebrow">Plataforma</span>
            <h2 className="dashboard-hero__title">
              Controle tenants, operadores master e servicos centrais em um unico painel.
            </h2>
            <p className="dashboard-hero__text">
              Esta area concentra a camada administrativa da plataforma e separa
              o escopo master dos modulos operacionais de tenant.
            </p>
          </div>

          <div className="dashboard-hero__panel">
            {highlights.map((item) => (
              <article key={item.label} className="dashboard-stat">
                <span className="dashboard-stat__label">{item.label}</span>
                <strong className="dashboard-stat__value">{item.value}</strong>
                <p className="dashboard-stat__description">{item.description}</p>
              </article>
            ))}
          </div>
        </section>

        <section className="dashboard-section">
          <div className="dashboard-section__head">
            <div>
              <span className="dashboard-section__eyebrow">Recursos</span>
              <h3 className="dashboard-section__title">
                Camada administrativa da plataforma
              </h3>
            </div>

            <Link
              to={`/app/platform/${platformResources[0]?.entity ?? "systemUsers"}`}
              className="dashboard-section__link"
            >
              Abrir primeiro recurso
            </Link>
          </div>

          <div className="module-grid">
            {platformResources.map((resource) => (
              <article key={resource.entity} className="module-card">
                <div className="module-card__content">
                  <div className="module-card__meta">
                    <span className="module-card__sigla">MST</span>
                    <span className="module-card__eyebrow">{resource.category}</span>
                  </div>

                  <h4 className="module-card__title">{resource.label}</h4>
                  <p className="module-card__description">
                    {resource.description}
                  </p>
                </div>

                <div className="module-card__footer">
                  <Link
                    to={`/app/platform/${resource.entity}`}
                    className="module-card__link"
                  >
                    Abrir recurso
                  </Link>
                </div>
              </article>
            ))}
          </div>
        </section>
      </div>
    );
  }

  const highlights = [
    {
      label: "Modulos ativos",
      value: visibleModules.length.toString().padStart(2, "0"),
      description: "Dominios operacionais disponiveis para esta sessao.",
    },
    {
      label: "Recursos visiveis",
      value: visibleModules
        .reduce((acc, module) => acc + module.resources.length, 0)
        .toString(),
      description: "Recursos liberados pelas permissoes atuais.",
    },
    {
      label: "Workspace",
      value: "ERP",
      description: "Ambiente centralizado para operacao integrada.",
    },
  ];

  return (
    <div className="dashboard-page">
      <section className="dashboard-hero">
        <div className="dashboard-hero__content">
          <span className="dashboard-hero__eyebrow">Dashboard</span>
          <h2 className="dashboard-hero__title">
            Uma operacao inteira, organizada em um unico workspace.
          </h2>
          <p className="dashboard-hero__text">
            Navegue pelos modulos do ERP, acesse os principais recursos do
            tenant e avance pelos fluxos de gestao com uma base unica.
          </p>
        </div>

        <div className="dashboard-hero__panel">
          {highlights.map((item) => (
            <article key={item.label} className="dashboard-stat">
              <span className="dashboard-stat__label">{item.label}</span>
              <strong className="dashboard-stat__value">{item.value}</strong>
              <p className="dashboard-stat__description">{item.description}</p>
            </article>
          ))}
        </div>
      </section>

      <section className="dashboard-section">
        <div className="dashboard-section__head">
          <div>
            <span className="dashboard-section__eyebrow">Modulos</span>
            <h3 className="dashboard-section__title">
              Areas principais do ERP
            </h3>
          </div>

          {firstResource ? (
            <Link
              to={`/app/module/${firstResource.schema}/${firstResource.entity}`}
              className="dashboard-section__link"
            >
              Abrir primeiro recurso
            </Link>
          ) : (
            <span className="dashboard-section__link dashboard-section__link--disabled">
              Sem recursos liberados
            </span>
          )}
        </div>

        <ModuleGrid modules={featuredModules} />
      </section>
    </div>
  );
}
