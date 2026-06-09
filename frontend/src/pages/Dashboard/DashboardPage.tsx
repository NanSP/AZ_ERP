import { Link } from "react-router-dom";
import { useAuth } from "../../auth/useAuth";
import {
  filterModulesByReadAccess,
  getFirstReadableResource,
} from "../../services/accessControl";
import ModuleGrid from "../../components/ModuleGrid/ModuleGrid";
import { tenantModules } from "../../services/tenantModules";
import "./dashboard.css";

export default function DashboardPage() {
  const { session } = useAuth();
  const visibleModules = filterModulesByReadAccess(tenantModules, session);
  const featuredModules = visibleModules.slice(0, 6);
  const firstResource = getFirstReadableResource(tenantModules, session);
  const highlights = [
    {
      label: "Módulos ativos",
      value: visibleModules.length.toString().padStart(2, "0"),
      description: "Domínios operacionais disponíveis para esta sessão.",
    },
    {
      label: "Recursos visíveis",
      value: visibleModules
        .reduce((acc, module) => acc + module.resources.length, 0)
        .toString(),
      description: "Recursos liberados pelas permissões atuais.",
    },
    {
      label: "Workspace",
      value: "ERP",
      description: "Ambiente centralizado para operação integrada.",
    },
  ];

  return (
    <div className="dashboard-page">
      <section className="dashboard-hero">
        <div className="dashboard-hero__content">
          <span className="dashboard-hero__eyebrow">Dashboard</span>
          <h2 className="dashboard-hero__title">
            Uma operação inteira, organizada em um único workspace.
          </h2>
          <p className="dashboard-hero__text">
            Navegue pelos módulos do ERP, acesse os principais recursos do
            tenant e avance pelos fluxos de gestão com uma base única.
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
            <span className="dashboard-section__eyebrow">Módulos</span>
            <h3 className="dashboard-section__title">
              Áreas principais do ERP
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
