import "./privacy.css";

const principles = [
  "tratamento limitado ao necessario para operacao, seguranca e obrigacoes legais",
  "segregacao entre plataforma master e ambientes de tenants",
  "registro de acessos, sessoes e eventos tecnicos para seguranca e auditoria",
  "controles de permissao por perfil e contexto operacional",
  "retencao de dados orientada por finalidade, compliance e historico operacional",
];

const rights = [
  "confirmacao da existencia de tratamento",
  "acesso aos dados pessoais tratados",
  "correcao de dados incompletos, inexatos ou desatualizados",
  "anonimizacao, bloqueio ou eliminacao quando cabivel",
  "portabilidade, oposicao e revisao de tratamento conforme a LGPD",
];

export default function PrivacyPage() {
  return (
    <section className="privacy-page">
      <div className="privacy-page__hero">
        <span className="privacy-page__eyebrow">Legal</span>
        <h1 className="privacy-page__title">Privacidade, termos e LGPD</h1>
        <p className="privacy-page__lead">
          Esta pagina resume como o AZ ERP trata dados da plataforma e dos
          ambientes dos tenants, quais controles aplicamos e como os titulares
          podem solicitar atendimento relacionado a privacidade.
        </p>
      </div>

      <div className="privacy-page__grid">
        <article className="privacy-page__card" id="privacy">
          <h2>Politica de Privacidade</h2>
          <p>
            O AZ ERP trata dados pessoais para autenticacao, administracao de
            acesso, provisionamento de ambientes, operacao dos modulos,
            seguranca, suporte tecnico, trilha de auditoria e cumprimento de
            obrigacoes legais e regulatorias.
          </p>
          <p>
            Os dados podem incluir identificadores de usuarios, contatos,
            dados cadastrais, informacoes operacionais inseridas pelos tenants,
            sessoes, dispositivos, registros tecnicos e evidencias de
            governanca.
          </p>
        </article>

        <article className="privacy-page__card" id="lgpd">
          <h2>Compromissos LGPD</h2>
          <ul className="privacy-page__list">
            {principles.map((item) => (
              <li key={item}>{item}</li>
            ))}
          </ul>
        </article>

        <article className="privacy-page__card" id="terms">
          <h2>Termos de Uso</h2>
          <p>
            O uso da plataforma pressupoe autenticacao valida, observancia das
            permissoes concedidas ao perfil do usuario e responsabilidade do
            tenant sobre os dados inseridos no ambiente operacional.
          </p>
          <p>
            Funcionalidades administrativas e trilhas de auditoria podem ser
            mantidas para seguranca, investigacao de incidentes, continuidade
            operacional e atendimento a requisitos contratuais e legais.
          </p>
        </article>

        <article className="privacy-page__card">
          <h2>Direitos do titular</h2>
          <ul className="privacy-page__list">
            {rights.map((item) => (
              <li key={item}>{item}</li>
            ))}
          </ul>
        </article>

        <article className="privacy-page__card privacy-page__card--wide">
          <h2>Canal de atendimento</h2>
          <p>
            Solicitacoes relacionadas a privacidade, seguranca e exercicio de
            direitos podem ser direcionadas ao canal operacional do tenant ou
            ao atendimento institucional da plataforma.
          </p>
          <p>
            Para ambientes produtivos, recomendamos divulgar nesta pagina o
            e-mail oficial do encarregado, o fluxo de atendimento e o prazo
            interno de resposta.
          </p>
        </article>
      </div>
    </section>
  );
}
