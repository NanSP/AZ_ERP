import { NavLink } from "react-router-dom";
import { useAuth } from "../../auth/useAuth";
import { filterModulesByReadAccess } from "../../services/accessControl";
import { platformResources } from "../../services/platformResources";
import { tenantModules } from "../../services/tenantModules";
import "./sidebar.css";

export default function Sidebar() {
  const { session } = useAuth();
  const visibleModules = filterModulesByReadAccess(tenantModules, session);
  const isMasterScope = session?.scope === "master";

  if (isMasterScope) {
    return (
      <div className="sidebar">
        <div className="sidebar__header">
          <span className="sidebar__eyebrow">Workspace</span>
          <h2 className="sidebar__title">Plataforma</h2>
          <p className="sidebar__subtitle">
            Acesse governanca, tenants e operacoes centrais do ambiente master.
          </p>
        </div>

        <nav className="sidebar__nav" aria-label="Recursos da plataforma">
          <section className="sidebar__group">
            <div className="sidebar__group-head">
              <span className="sidebar__sigla">MST</span>
              <div>
                <h3 className="sidebar__group-title">Camada administrativa</h3>
                <p className="sidebar__group-description">
                  Recursos internos da plataforma AZ ERP.
                </p>
              </div>
            </div>

            <div className="sidebar__links">
              {platformResources.map((resource) => (
                <NavLink
                  key={resource.entity}
                  to={`/app/platform/${resource.entity}`}
                  className={({ isActive }) =>
                    isActive
                      ? "sidebar__link sidebar__link--active"
                      : "sidebar__link"
                  }
                >
                  <span className="sidebar__link-label">{resource.label}</span>
                  <span className="sidebar__link-meta">{resource.category}</span>
                </NavLink>
              ))}
            </div>
          </section>
        </nav>
      </div>
    );
  }

  return (
    <div className="sidebar">
      <div className="sidebar__header">
        <span className="sidebar__eyebrow">Workspace</span>
        <h2 className="sidebar__title">Módulos</h2>
        <p className="sidebar__subtitle">
          Navegue pelos domínios operacionais do ERP.
        </p>
      </div>

      <nav className="sidebar__nav" aria-label="Módulos do tenant">
        {visibleModules.map((module) => (
          <section key={module.sigla} className="sidebar__group">
            <div className="sidebar__group-head">
              <span className="sidebar__sigla">{module.sigla}</span>
              <div>
                <h3 className="sidebar__group-title">{module.title}</h3>
                <p className="sidebar__group-description">
                  {module.description}
                </p>
              </div>
            </div>

            <div className="sidebar__links">
              {module.resources.map((resource) => (
                <NavLink
                  key={`${resource.schema}-${resource.entity}`}
                  to={`/app/module/${resource.schema}/${resource.entity}`}
                  className={({ isActive }) =>
                    isActive
                      ? "sidebar__link sidebar__link--active"
                      : "sidebar__link"
                  }
                >
                  <span className="sidebar__link-label">{resource.label}</span>
                  <span className="sidebar__link-meta">{resource.entity}</span>
                </NavLink>
              ))}
            </div>
          </section>
        ))}
      </nav>
    </div>
  );
}
