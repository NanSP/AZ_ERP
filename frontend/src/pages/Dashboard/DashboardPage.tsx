import { Link } from "react-router-dom";
import ModuleGrid from "../../components/ModuleGrid/ModuleGrid";
import { tenantModules } from "../../services/tenantModules";
import "./dashboard.css";

const highlights = [
  {
    label: "Modulos ativos",
    value: tenantModules.length.toString().padStart(2, "0"),
    description: "Dominios operacionais disponiveis no tenant.",
  },
  {
    label: "Recursos mapeados",
    value: tenantModules
      .reduce((acc, module) => acc + module.resources.length, 0)
      .toString(),
    description: "Recursos conectados ao backend atual.",
  },
  {
    label: "Workspace",
    value: "ERP",
    description: "Ambiente centralizado para operacao integrada.",
  },
];

export default function DashboardPage() {
  const featuredModules = tenantModules.slice(0, 6);

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

          <Link
            to="/app/module/sys/usuarios"
            className="dashboard-section__link"
          >
            Abrir primeiro recurso
          </Link>
        </div>

        <ModuleGrid modules={featuredModules} />
      </section>
    </div>
  );
}
