import { NavLink } from "react-router-dom";
import { tenantModules } from "../../services/tenantModules";
import "./sidebar.css";

export default function Sidebar() {
  return (
    <div className="sidebar">
      <div className="sidebar__header">
        <span className="sidebar__eyebrow">Workspace</span>
        <h2 className="sidebar__title">Modulos</h2>
        <p className="sidebar__subtitle">
          Navegue pelos dominios operacionais do ERP.
        </p>
      </div>

      <nav className="sidebar__nav" aria-label="Modulos do tenant">
        {tenantModules.map((module) => (
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
