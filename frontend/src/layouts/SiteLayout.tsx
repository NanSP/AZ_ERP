import { NavLink, Outlet } from "react-router-dom";
import { useAuth } from "../auth/useAuth";
import { brandingAssets } from "../services/publicAssets";
import "./site-layout.css";

const { mainLogo, standaloneLogo } = brandingAssets;

const navItems = [
  { label: "Home", href: "/" },
  { label: "Modulos", href: "/#modulos" },
  { label: "Beneficios", href: "/#beneficios" },
  { label: "Tecnologia", href: "/#tecnologia" },
  { label: "Precos", href: "/#pricing" },
  { label: "Contato", href: "/#contato" },
];

export default function SiteLayout() {
  const { isAuthenticated, session, logout } = useAuth();

  return (
    <div className="site-shell">
      <header className="site-header">
        <div className="site-header__inner">
          <NavLink to="/" className="site-brand" aria-label="AZ ERP">
            <img src={mainLogo} alt="AZ ERP" className="site-brand__image" />
          </NavLink>

          <nav className="site-nav" aria-label="Principal">
            {navItems.map((item) => (
              <a key={item.label} href={item.href} className="site-nav__link">
                {item.label}
              </a>
            ))}
            {isAuthenticated && (
              <NavLink to="/app" className="site-nav__link">
                Painel
              </NavLink>
            )}
          </nav>

          <div className="site-header__actions">
            {isAuthenticated ? (
              <>
                <span className="site-session">
                  {session?.scope === "tenant" ? "Tenant" : "Master"} ·{" "}
                  {session?.login}
                </span>
                <button
                  type="button"
                  className="site-cta site-cta--ghost"
                  onClick={() => void logout()}
                >
                  Sair
                </button>
              </>
            ) : (
              <NavLink to="/login" className="site-cta">
                Login
              </NavLink>
            )}
          </div>
        </div>
      </header>

      <main className="site-main">
        <Outlet />
      </main>

      <footer className="site-footer">
        <div className="site-footer__inner">
          <div className="site-footer__brand-column">
            <img
              src={standaloneLogo}
              alt="Icone AZ ERP"
              className="site-footer__logo"
            />
          </div>

          <div className="site-footer__column">
            <h4>Produto</h4>
            <a href="/#modulos">Modulos</a>
            <a href="/#beneficios">Beneficios</a>
            <a href="/#tecnologia">Tecnologia</a>
          </div>

          <div className="site-footer__column">
            <h4>Modulos</h4>
            <a href="/#modulos">Financeiro</a>
            <a href="/#modulos">Supply Chain</a>
            <a href="/#modulos">Recursos Humanos</a>
          </div>

          <div className="site-footer__column">
            <h4>Companhia</h4>
            <a href="/#sobre">Sobre</a>
            <a href="/#contato">Contato</a>
            <a href="/#pricing">Planos</a>
          </div>

          <div className="site-footer__column">
            <h4>Suporte</h4>
            <NavLink to="/tenant-login">Tenant Access</NavLink>
            <NavLink to="/login">Master Access</NavLink>
            <a href="/#tecnologia">Arquitetura</a>
          </div>

          <div className="site-footer__column">
            <h4>Legal</h4>
            <NavLink to="/privacy">Politica de Privacidade</NavLink>
            <NavLink to="/privacy#terms">Termos de Servico</NavLink>
            <NavLink to="/privacy#lgpd">LGPD</NavLink>
          </div>
        </div>

        <div className="site-footer__bottom">
          <span>© 2026 AZ ERP. Todos os direitos reservados.</span>
          <div className="site-footer__social">
            <a href="/#contato">LinkedIn</a>
            <a href="/#contato">YouTube</a>
            <a href="/#contato">Vendas</a>
          </div>
        </div>
      </footer>
    </div>
  );
}
