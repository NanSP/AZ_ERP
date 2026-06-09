import { Link } from "react-router-dom";
import type { TenantModule } from "../../services/tenantModules";
import "./module-grid.css";

type ModuleGridProps = {
  modules: TenantModule[];
};

export default function ModuleGrid({ modules }: ModuleGridProps) {
  return (
    <div className="module-grid">
      {modules.map((module) => {
        const firstResource = module.resources[0];

        return (
          <article key={module.sigla} className="module-grid__card">
            <div className="module-grid__top">
              <span className="module-grid__sigla">{module.sigla}</span>
              <span className="module-grid__count">
                {module.resources.length} recursos
              </span>
            </div>

            <div className="module-grid__body">
              <h3 className="module-grid__title">{module.title}</h3>
              <p className="module-grid__description">{module.description}</p>
            </div>

            <div className="module-grid__resources">
              {module.resources.slice(0, 4).map((resource) => (
                <span
                  key={`${resource.schema}-${resource.entity}`}
                  className="module-grid__resource"
                >
                  {resource.label}
                </span>
              ))}
            </div>

            {firstResource ? (
              <Link
                to={`/app/module/${firstResource.schema}/${firstResource.entity}`}
                className="module-grid__action"
              >
                Abrir módulo
              </Link>
            ) : (
              <span className="module-grid__action module-grid__action--disabled">
                Sem recursos
              </span>
            )}
          </article>
        );
      })}
    </div>
  );
}
