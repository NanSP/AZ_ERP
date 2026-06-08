import { Link } from "react-router-dom";
import {
  benefits,
  moduleCatalog,
  testimonials,
} from "../../services/moduleCatalog";
import "./entry.css";

export default function EntryPage() {
  return (
    <div className="landing">
      <section className="landing-hero">
        <div className="landing-hero__copy">
          <span className="landing-pill">Enterprise ERP Platform</span>
          <h1>Um ERP. Controle total do negócio.</h1>
          <p>
            Gerencie financeiro, estoque, vendas, projetos, pessoas, compliance
            e business intelligence em uma única plataforma pensada para
            empresas médias e grandes.
          </p>

          <div className="landing-hero__actions">
            <Link to="/login" className="landing-button landing-button--solid">
              Iniciar demonstração
            </Link>
            <a
              href="#tecnologia"
              className="landing-button landing-button--ghost"
            >
              Ver apresentação
            </a>
          </div>
        </div>
      </section>

      <section className="landing-trust">
        <div className="landing-section__title">
          <span className="landing-pill landing-pill--light">Confiança</span>
          <h2>
            Arquitetura enterprise com foco em segurança e rastreabilidade
          </h2>
        </div>

        <div className="landing-trust__grid">
          <article>
            <strong>99,9% Uptime</strong>
            <p>Base preparada para operação contínua e monitoramento ativo.</p>
          </article>
          <article>
            <strong>100% Rastreabilidade</strong>
            <p>Logs, auditoria e trilhas completas para governança real.</p>
          </article>
          <article>
            <strong>LGPD e compliance</strong>
            <p>
              Controles, segregação e governança alinhados ao contexto
              corporativo.
            </p>
          </article>
          <article>
            <strong>Analytics em tempo real</strong>
            <p>Decisão baseada em indicadores, dashboards e KPIs vivos.</p>
          </article>
        </div>
      </section>

      <section id="modulos" className="landing-section">
        <div className="landing-section__title">
          <span className="landing-pill landing-pill--light">Modulos</span>
          <h2>Todo o ecossistema empresarial em um unico ambiente integrado</h2>
          <p>
            Cada domínio do ERP tem uma representação visual premium,
            monocromática e consistente com o restante da plataforma.
          </p>
        </div>

        <div className="landing-modules">
          {moduleCatalog.map((module, index) => (
            <article key={module.sigla} className="landing-module-card">
              <div className="landing-module-visual">
                <span>{module.sigla}</span>
                <img
                  src={module.image}
                  alt={`Ilustracao do modulo ${module.sigla}`}
                  className="landing-module-visual__image"
                  loading={index < 2 ? "eager" : "lazy"}
                  fetchPriority={index < 2 ? "high" : "low"}
                  decoding="async"
                />
              </div>
              <div className="landing-module-card__body">
                <h3>{module.sigla}</h3>
                <strong>{module.title}</strong>
                <p>{module.description}</p>
              </div>
            </article>
          ))}
        </div>
      </section>

      <section id="beneficios" className="landing-section">
        <div className="landing-section__title">
          <span className="landing-pill landing-pill--light">Benefícios</span>
          <h2>
            Ganhos operacionais claros para empresas que precisam de escala
          </h2>
        </div>

        <div className="landing-benefits">
          {benefits.map((item) => (
            <article key={item} className="landing-benefit-card">
              <h3>{item}</h3>
              <p>
                Estruture operações, aumente previsibilidade e reduza fricção
                entre áreas com uma base integrada.
              </p>
            </article>
          ))}
        </div>
      </section>

      <section id="tecnologia" className="landing-section">
        <div className="landing-section__title">
          <span className="landing-pill landing-pill--light">Tecnologia</span>
          <h2>Camadas conectadas com mentalidade de plataforma</h2>
        </div>

        <div className="landing-architecture">
          <div className="landing-architecture__node">Frontend</div>
          <div className="landing-architecture__node">Backend</div>
          <div className="landing-architecture__node">API</div>
          <div className="landing-architecture__node">Database</div>
          <div className="landing-architecture__node">Cloud</div>
        </div>
      </section>

      <section id="pricing" className="landing-section">
        <div className="landing-section__title">
          <span className="landing-pill landing-pill--light">Preços</span>
          <h2>
            Modelo comercial consultivo para operações de maior complexidade
          </h2>
          <p>
            O AZ ERP foi pensado para implantação orientada ao contexto do
            cliente, com arquitetura e módulos ajustados ao tamanho da operação.
          </p>
        </div>

        <div className="landing-pricing">
          <article className="landing-pricing-card">
            <strong>Enterprise Fit</strong>
            <h3>Proposta sob medida</h3>
            <p>
              Escopo, módulos, implantação e suporte alinhados à estrutura
              operacional da empresa.
            </p>
            <a href="#contato" className="landing-button landing-button--solid">
              Falar com vendas
            </a>
          </article>
        </div>
      </section>

      <section className="landing-section">
        <div className="landing-section__title">
          <span className="landing-pill landing-pill--light">Depoimentos</span>
          <h2>Percepção de valor no estilo enterprise</h2>
        </div>

        <div className="landing-testimonials">
          {testimonials.map((item) => (
            <article key={item.author} className="landing-testimonial-card">
              <p>"{item.quote}"</p>
              <strong>{item.author}</strong>
              <span>{item.company}</span>
            </article>
          ))}
        </div>
      </section>

      <section id="contato" className="landing-cta">
        <div className="landing-cta__copy">
          <span className="landing-pill">Transforme sua gestão</span>
          <h2>
            Leve sua operação para um ERP único, integrado e enterprise-grade.
          </h2>
          <p>
            Agende uma demonstração ou converse com o time comercial para
            avaliar aderência, arquitetura e implantação.
          </p>
        </div>

        <div className="landing-cta__actions">
          <Link to="/login" className="landing-button landing-button--solid">
            Agendar demo
          </Link>
          <Link
            to="/tenant-login"
            className="landing-button landing-button--ghost"
          >
            Contatar vendas
          </Link>
        </div>
      </section>
    </div>
  );
}
