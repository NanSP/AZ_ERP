import {
  Suspense,
  lazy,
  type ComponentType,
  type LazyExoticComponent,
} from "react";
import { Link, useParams } from "react-router-dom";
import { useAuth } from "../../auth/useAuth";
import Breadcrumbs from "../../components/Breadcrumbs/Breadcrumbs";
import ModuleCrud from "../../components/ModuleCrud/ModuleCrud";
import { canReadResource } from "../../services/accessControl";
import { tenantModules } from "../../services/tenantModules";
import "./module-workspace.css";

type EmbeddedPageProps = {
  embedded?: boolean;
};

type LazyPage = LazyExoticComponent<ComponentType<EmbeddedPageProps>>;

function lazyDefault(
  factory: () => Promise<{ default: ComponentType<EmbeddedPageProps> }>,
): LazyPage {
  return lazy(factory);
}

function lazyNamed<TModule extends Record<string, unknown>>(
  factory: () => Promise<TModule>,
  key: keyof TModule,
): LazyPage {
  return lazy(async () => {
    const module = await factory();
    return {
      default: module[key] as ComponentType<EmbeddedPageProps>,
    };
  });
}

const resourcePages: Record<string, LazyPage> = {
  "core.parceiros": lazyDefault(() => import("../Core/PartnersPage")),
  "core.empresas": lazyDefault(() => import("../Core/CompaniesPage")),
  "core.produtos": lazyDefault(() => import("../Core/ProductsPage")),
  "core.contatos": lazyDefault(() => import("../Core/ContactsPage")),
  "core.enderecos": lazyDefault(() => import("../Core/AddressesPage")),
  "sys.usuarios": lazyDefault(() => import("../Sys/UsersPage")),
  "sys.perfis": lazyDefault(() => import("../Sys/ProfilesPage")),
  "sys.permissoes": lazyDefault(() => import("../Sys/PermissionsPage")),
  "sys.usuarioPerfil": lazyDefault(() => import("../Sys/UserProfilesPage")),
  "sys.perfilPermissao": lazyDefault(
    () => import("../Sys/ProfilePermissionsPage"),
  ),
  "fi.contasPagar": lazyDefault(() => import("../Fi/AccountsPayablePage")),
  "fi.centrosCusto": lazyDefault(() => import("../Fi/CostCentersPage")),
  "fi.planoContas": lazyDefault(() => import("../Fi/ChartOfAccountsPage")),
  "fi.fluxoCaixa": lazyDefault(() => import("../Fi/CashFlowPage")),
  "fi.movimentacoesBancarias": lazyDefault(
    () => import("../Fi/BankMovementsPage"),
  ),
  "fi.contasReceber": lazyDefault(() => import("../Fi/AccountsReceivablePage")),
  "fiscal.documentos": lazyNamed(() => import("../Fiscal"), "DocumentsPage"),
  "fiscal.esocialEventos": lazyNamed(
    () => import("../Fiscal"),
    "EsocialEventsPage",
  ),
  "fiscal.efdRegistros": lazyNamed(() => import("../Fiscal"), "EfdRecordsPage"),
  "fiscal.ecdRegistros": lazyNamed(() => import("../Fiscal"), "EcdRecordsPage"),
  "am.bensPatrimoniais": lazyNamed(() => import("../Am"), "AssetsPage"),
  "am.manutencoes": lazyNamed(() => import("../Am"), "MaintenancesPage"),
  "auditoria.logAcoes": lazyNamed(
    () => import("../Auditoria"),
    "ActionLogsPage",
  ),
  "auditoria.logErros": lazyNamed(
    () => import("../Auditoria"),
    "ErrorLogsPage",
  ),
  "bi.dashboards": lazyDefault(() => import("../Bi/DashboardsPage")),
  "bi.metricas": lazyDefault(() => import("../Bi/MetricsPage")),
  "bi.historicoMetricas": lazyDefault(() => import("../Bi/MetricHistoryPage")),
  "bi.relatorios": lazyDefault(() => import("../Bi/ReportsPage")),
  "grc.riscos": lazyDefault(() => import("../Grc/RisksPage")),
  "grc.controles": lazyDefault(() => import("../Grc/ControlsPage")),
  "grc.auditorias": lazyDefault(() => import("../Grc/AuditsPage")),
  "grc.consentimentos": lazyDefault(() => import("../Grc/ConsentsPage")),
  "grc.solicitacoesTitular": lazyDefault(
    () => import("../Grc/SubjectRequestsPage"),
  ),
  "mm.materiais": lazyDefault(() => import("../Mm/MaterialsPage")),
  "mm.estoques": lazyDefault(() => import("../Mm/StocksPage")),
  "mm.movimentacoes": lazyDefault(() => import("../Mm/MovementsPage")),
  "mm.compras": lazyDefault(() => import("../Mm/PurchasesPage")),
  "mm.compraItens": lazyDefault(() => import("../Mm/PurchaseItemsPage")),
  "mm.inventarios": lazyDefault(() => import("../Mm/InventoriesPage")),
  "portal.notificacoes": lazyDefault(
    () => import("../Portal/NotificationsPage"),
  ),
  "portal.sessoes": lazyDefault(() => import("../Portal/SessionsPage")),
  "portal.dispositivos": lazyDefault(() => import("../Portal/DevicesPage")),
  "ps.projetos": lazyDefault(() => import("../Ps/ProjectsPage")),
  "ps.tarefas": lazyDefault(() => import("../Ps/TasksPage")),
  "ps.recursosAlocados": lazyDefault(
    () => import("../Ps/AllocatedResourcesPage"),
  ),
  "pp.ordemProducao": lazyDefault(() => import("../Pp/ProductionOrdersPage")),
  "pp.bom": lazyDefault(() => import("../Pp/BomPage")),
  "pp.apontamentos": lazyDefault(() => import("../Pp/ProductionEntriesPage")),
  "pp.mrp": lazyDefault(() => import("../Pp/MrpPage")),
  "sm.ordensServico": lazyDefault(() => import("../Sm/OrdersPage")),
  "sm.atendimentos": lazyDefault(() => import("../Sm/AttendancesPage")),
  "sm.slaConfig": lazyDefault(() => import("../Sm/SlaConfigPage")),
  "rh.colaboradores": lazyDefault(() => import("../Rh/EmployeesPage")),
  "rh.dependentes": lazyDefault(() => import("../Rh/DependentsPage")),
  "rh.beneficios": lazyDefault(() => import("../Rh/BenefitsPage")),
  "rh.controleDePonto": lazyDefault(() => import("../Rh/TimeTrackingPage")),
  "rh.folhaDePagamento": lazyDefault(() => import("../Rh/PayrollPage")),
  "qm.inspecoes": lazyNamed(() => import("../Qm"), "InspectionsPage"),
  "qm.naoConformidade": lazyNamed(() => import("../Qm"), "NonConformitiesPage"),
  "sd.clientes": lazyDefault(() => import("../Sd/ClientsPage")),
  "sd.oportunidades": lazyDefault(() => import("../Sd/OpportunitiesPage")),
  "sd.contratos": lazyDefault(() => import("../Sd/ContractsPage")),
  "sd.pedidos": lazyDefault(() => import("../Sd/OrdersPage")),
  "sd.pedidoItens": lazyDefault(() => import("../Sd/OrderItemsPage")),
  "sd.faturas": lazyDefault(() => import("../Sd/InvoicesPage")),
};

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
  const { session } = useAuth();
  const { schema, entity } = useParams();
  const match = findResource(schema, entity);

  if (session?.scope === "master") {
    return (
      <section className="module-workspace module-workspace--empty">
        <span className="module-workspace__eyebrow">Escopo</span>
        <h2 className="module-workspace__title">Workspace de tenant bloqueado</h2>
        <p className="module-workspace__description">
          Sessoes master acessam apenas a camada de plataforma, separada dos modulos operacionais do tenant.
        </p>
        <Link to="/app" className="module-workspace__back">
          Voltar ao dashboard
        </Link>
      </section>
    );
  }

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
  const hasReadAccess = canReadResource(session, resource);
  const resourceKey = `${resource.schema}.${resource.entity}`;
  const ResourcePage = resourcePages[resourceKey];

  if (!hasReadAccess) {
    return (
      <section className="module-workspace module-workspace--empty">
        <span className="module-workspace__eyebrow">Acesso</span>
        <h2 className="module-workspace__title">Recurso indisponivel</h2>
        <p className="module-workspace__description">
          Sua sessao nao possui permissão de leitura para este recurso.
        </p>
        <Link to="/app" className="module-workspace__back">
          Voltar ao dashboard
        </Link>
      </section>
    );
  }

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
              Workspace do módulo
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
            <span className="module-workspace__panel-eyebrow">Operação</span>
            <h3 className="module-workspace__panel-title">Gestão do recurso</h3>
          </div>

          <p className="module-workspace__panel-text">
            Abaixo esta a camada operacional atual conectada ao backend.
          </p>
        </div>

        <div className="module-workspace__crud">
          {ResourcePage ? (
            <Suspense
              fallback={
                <div className="module-workspace__loading">
                  Carregando interface do recurso...
                </div>
              }
            >
              <ResourcePage embedded />
            </Suspense>
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
