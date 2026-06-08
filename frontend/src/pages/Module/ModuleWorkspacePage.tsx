import { Link, useParams } from "react-router-dom";
import { useAuth } from "../../auth/useAuth";
import Breadcrumbs from "../../components/Breadcrumbs/Breadcrumbs";
import ModuleCrud from "../../components/ModuleCrud/ModuleCrud";
import { canReadResource } from "../../services/accessControl";
import AddressesPage from "../Core/AddressesPage";
import CompaniesPage from "../Core/CompaniesPage";
import ContactsPage from "../Core/ContactsPage";
import PartnersPage from "../Core/PartnersPage";
import ProductsPage from "../Core/ProductsPage";
import AccountsPayablePage from "../Fi/AccountsPayablePage";
import CostCentersPage from "../Fi/CostCentersPage";
import ChartOfAccountsPage from "../Fi/ChartOfAccountsPage";
import CashFlowPage from "../Fi/CashFlowPage";
import BankMovementsPage from "../Fi/BankMovementsPage";
import AccountsReceivablePage from "../Fi/AccountsReceivablePage";
import {
  DocumentsPage,
  EcdRecordsPage,
  EfdRecordsPage,
  EsocialEventsPage,
} from "../Fiscal";
import { AssetsPage, MaintenancesPage } from "../Am";
import { ActionLogsPage, ErrorLogsPage } from "../Auditoria";
import DashboardsPage from "../Bi/DashboardsPage";
import MetricHistoryPage from "../Bi/MetricHistoryPage";
import MetricsPage from "../Bi/MetricsPage";
import ReportsPage from "../Bi/ReportsPage";
import AuditsPage from "../Grc/AuditsPage";
import ConsentsPage from "../Grc/ConsentsPage";
import ControlsPage from "../Grc/ControlsPage";
import RisksPage from "../Grc/RisksPage";
import MaterialsPage from "../Mm/MaterialsPage";
import MovementsPage from "../Mm/MovementsPage";
import InventoriesPage from "../Mm/InventoriesPage";
import PurchaseItemsPage from "../Mm/PurchaseItemsPage";
import PurchasesPage from "../Mm/PurchasesPage";
import StocksPage from "../Mm/StocksPage";
import DevicesPage from "../Portal/DevicesPage";
import NotificationsPage from "../Portal/NotificationsPage";
import SessionsPage from "../Portal/SessionsPage";
import AllocatedResourcesPage from "../Ps/AllocatedResourcesPage";
import ProjectsPage from "../Ps/ProjectsPage";
import TasksPage from "../Ps/TasksPage";
import BomPage from "../Pp/BomPage";
import MrpPage from "../Pp/MrpPage";
import ProductionEntriesPage from "../Pp/ProductionEntriesPage";
import ProductionOrdersPage from "../Pp/ProductionOrdersPage";
import AttendancesPage from "../Sm/AttendancesPage";
import OrdersPage from "../Sm/OrdersPage";
import SlaConfigPage from "../Sm/SlaConfigPage";
import DependentsPage from "../Rh/DependentsPage";
import EmployeesPage from "../Rh/EmployeesPage";
import BenefitsPage from "../Rh/BenefitsPage";
import TimeTrackingPage from "../Rh/TimeTrackingPage";
import PayrollPage from "../Rh/PayrollPage";
import { InspectionsPage, NonConformitiesPage } from "../Qm";
import ClientsPage from "../Sd/ClientsPage";
import OpportunitiesPage from "../Sd/OpportunitiesPage";
import PermissionsPage from "../Sys/PermissionsPage";
import ProfilePermissionsPage from "../Sys/ProfilePermissionsPage";
import ProfilesPage from "../Sys/ProfilesPage";
import UserProfilesPage from "../Sys/UserProfilesPage";
import UsersPage from "../Sys/UsersPage";
import { tenantModules } from "../../services/tenantModules";
import "./module-workspace.css";

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
  const isPartnersPilot =
    resource.schema === "core" && resource.entity === "parceiros";
  const isCompaniesPilot =
    resource.schema === "core" && resource.entity === "empresas";
  const isProductsPilot =
    resource.schema === "core" && resource.entity === "produtos";
  const isContactsPilot =
    resource.schema === "core" && resource.entity === "contatos";
  const isAddressesPilot =
    resource.schema === "core" && resource.entity === "enderecos";
  const isUsersPilot =
    resource.schema === "sys" && resource.entity === "usuarios";
  const isProfilesPilot =
    resource.schema === "sys" && resource.entity === "perfis";
  const isPermissionsPilot =
    resource.schema === "sys" && resource.entity === "permissoes";
  const isUserProfilesPilot =
    resource.schema === "sys" && resource.entity === "usuarioPerfil";
  const isProfilePermissionsPilot =
    resource.schema === "sys" && resource.entity === "perfilPermissao";
  const isAccountsPayablePilot =
    resource.schema === "fi" && resource.entity === "contasPagar";
  const isCostCentersPilot =
    resource.schema === "fi" && resource.entity === "centrosCusto";
  const isChartOfAccountsPilot =
    resource.schema === "fi" && resource.entity === "planoContas";
  const isCashFlowPilot =
    resource.schema === "fi" && resource.entity === "fluxoCaixa";
  const isBankMovementsPilot =
    resource.schema === "fi" && resource.entity === "movimentacoesBancarias";
  const isAssetsPilot =
    resource.schema === "am" && resource.entity === "bensPatrimoniais";
  const isMaintenancesPilot =
    resource.schema === "am" && resource.entity === "manutencoes";
  const isProjectsPilot =
    resource.schema === "ps" && resource.entity === "projetos";
  const isTasksPilot =
    resource.schema === "ps" && resource.entity === "tarefas";
  const isAllocatedResourcesPilot =
    resource.schema === "ps" && resource.entity === "recursosAlocados";
  const isProductionOrdersPilot =
    resource.schema === "pp" && resource.entity === "ordemProducao";
  const isBomPilot = resource.schema === "pp" && resource.entity === "bom";
  const isProductionEntriesPilot =
    resource.schema === "pp" && resource.entity === "apontamentos";
  const isMrpPilot = resource.schema === "pp" && resource.entity === "mrp";
  const isNotificationsPilot =
    resource.schema === "portal" && resource.entity === "notificacoes";
  const isSessionsPilot =
    resource.schema === "portal" && resource.entity === "sessoes";
  const isDevicesPilot =
    resource.schema === "portal" && resource.entity === "dispositivos";
  const isOrdersPilot =
    resource.schema === "sm" && resource.entity === "ordensServico";
  const isAttendancesPilot =
    resource.schema === "sm" && resource.entity === "atendimentos";
  const isSlaConfigPilot =
    resource.schema === "sm" && resource.entity === "slaConfig";
  const isActionLogsPilot =
    resource.schema === "auditoria" && resource.entity === "logAcoes";
  const isErrorLogsPilot =
    resource.schema === "auditoria" && resource.entity === "logErros";
  const isInspectionsPilot =
    resource.schema === "qm" && resource.entity === "inspecoes";
  const isNonConformitiesPilot =
    resource.schema === "qm" && resource.entity === "naoConformidade";
  const isEmployeesPilot =
    resource.schema === "rh" && resource.entity === "colaboradores";
  const isDependentsPilot =
    resource.schema === "rh" && resource.entity === "dependentes";
  const isBenefitsPilot =
    resource.schema === "rh" && resource.entity === "beneficios";
  const isTimeTrackingPilot =
    resource.schema === "rh" && resource.entity === "controleDePonto";
  const isPayrollPilot =
    resource.schema === "rh" && resource.entity === "folhaDePagamento";
  const isAccountsReceivablePilot =
    resource.schema === "fi" && resource.entity === "contasReceber";
  const isDocumentsPilot =
    resource.schema === "fiscal" && resource.entity === "documentos";
  const isEsocialEventsPilot =
    resource.schema === "fiscal" && resource.entity === "esocialEventos";
  const isEfdRecordsPilot =
    resource.schema === "fiscal" && resource.entity === "efdRegistros";
  const isEcdRecordsPilot =
    resource.schema === "fiscal" && resource.entity === "ecdRegistros";
  const isDashboardsPilot =
    resource.schema === "bi" && resource.entity === "dashboards";
  const isMetricsPilot = resource.schema === "bi" && resource.entity === "metricas";
  const isMetricHistoryPilot =
    resource.schema === "bi" && resource.entity === "historicoMetricas";
  const isReportsPilot =
    resource.schema === "bi" && resource.entity === "relatorios";
  const isRisksPilot = resource.schema === "grc" && resource.entity === "riscos";
  const isControlsPilot =
    resource.schema === "grc" && resource.entity === "controles";
  const isAuditsPilot =
    resource.schema === "grc" && resource.entity === "auditorias";
  const isConsentsPilot =
    resource.schema === "grc" && resource.entity === "consentimentos";
  const isMaterialsPilot =
    resource.schema === "mm" && resource.entity === "materiais";
  const isStocksPilot =
    resource.schema === "mm" && resource.entity === "estoques";
  const isMovementsPilot =
    resource.schema === "mm" && resource.entity === "movimentacoes";
  const isPurchasesPilot =
    resource.schema === "mm" && resource.entity === "compras";
  const isPurchaseItemsPilot =
    resource.schema === "mm" && resource.entity === "compraItens";
  const isInventoriesPilot =
    resource.schema === "mm" && resource.entity === "inventarios";
  const isClientsPilot = resource.schema === "sd" && resource.entity === "clientes";
  const isOpportunitiesPilot =
    resource.schema === "sd" && resource.entity === "oportunidades";

  if (!hasReadAccess) {
    return (
      <section className="module-workspace module-workspace--empty">
        <span className="module-workspace__eyebrow">Acesso</span>
        <h2 className="module-workspace__title">Recurso indisponivel</h2>
        <p className="module-workspace__description">
          Sua sessao nao possui permissao de leitura para este recurso.
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
              Workspace do modulo
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
            <span className="module-workspace__panel-eyebrow">Operacao</span>
            <h3 className="module-workspace__panel-title">Gestao do recurso</h3>
          </div>

          <p className="module-workspace__panel-text">
            Abaixo esta a camada operacional atual conectada ao backend.
          </p>
        </div>

        <div className="module-workspace__crud">
          {isPartnersPilot ? (
            <PartnersPage embedded />
          ) : isCompaniesPilot ? (
            <CompaniesPage embedded />
          ) : isProductsPilot ? (
            <ProductsPage embedded />
          ) : isContactsPilot ? (
            <ContactsPage embedded />
          ) : isAddressesPilot ? (
            <AddressesPage embedded />
          ) : isUsersPilot ? (
            <UsersPage embedded />
          ) : isProfilesPilot ? (
            <ProfilesPage embedded />
          ) : isPermissionsPilot ? (
            <PermissionsPage embedded />
          ) : isUserProfilesPilot ? (
            <UserProfilesPage embedded />
          ) : isProfilePermissionsPilot ? (
            <ProfilePermissionsPage embedded />
          ) : isAccountsPayablePilot ? (
            <AccountsPayablePage embedded />
          ) : isCostCentersPilot ? (
            <CostCentersPage embedded />
          ) : isChartOfAccountsPilot ? (
            <ChartOfAccountsPage embedded />
          ) : isCashFlowPilot ? (
            <CashFlowPage embedded />
          ) : isBankMovementsPilot ? (
            <BankMovementsPage embedded />
          ) : isAssetsPilot ? (
            <AssetsPage embedded />
          ) : isMaintenancesPilot ? (
            <MaintenancesPage embedded />
          ) : isProjectsPilot ? (
            <ProjectsPage embedded />
          ) : isTasksPilot ? (
            <TasksPage embedded />
          ) : isAllocatedResourcesPilot ? (
            <AllocatedResourcesPage embedded />
          ) : isProductionOrdersPilot ? (
            <ProductionOrdersPage embedded />
          ) : isBomPilot ? (
            <BomPage embedded />
          ) : isProductionEntriesPilot ? (
            <ProductionEntriesPage embedded />
          ) : isMrpPilot ? (
            <MrpPage embedded />
          ) : isNotificationsPilot ? (
            <NotificationsPage embedded />
          ) : isSessionsPilot ? (
            <SessionsPage embedded />
          ) : isDevicesPilot ? (
            <DevicesPage embedded />
          ) : isOrdersPilot ? (
            <OrdersPage embedded />
          ) : isAttendancesPilot ? (
            <AttendancesPage embedded />
          ) : isSlaConfigPilot ? (
            <SlaConfigPage embedded />
          ) : isActionLogsPilot ? (
            <ActionLogsPage embedded />
          ) : isErrorLogsPilot ? (
            <ErrorLogsPage embedded />
          ) : isInspectionsPilot ? (
            <InspectionsPage embedded />
          ) : isNonConformitiesPilot ? (
            <NonConformitiesPage embedded />
          ) : isEmployeesPilot ? (
            <EmployeesPage embedded />
          ) : isDependentsPilot ? (
            <DependentsPage embedded />
          ) : isBenefitsPilot ? (
            <BenefitsPage embedded />
          ) : isTimeTrackingPilot ? (
            <TimeTrackingPage embedded />
          ) : isPayrollPilot ? (
            <PayrollPage embedded />
          ) : isAccountsReceivablePilot ? (
            <AccountsReceivablePage embedded />
          ) : isDocumentsPilot ? (
            <DocumentsPage embedded />
          ) : isEsocialEventsPilot ? (
            <EsocialEventsPage embedded />
          ) : isEfdRecordsPilot ? (
            <EfdRecordsPage embedded />
          ) : isEcdRecordsPilot ? (
            <EcdRecordsPage embedded />
          ) : isDashboardsPilot ? (
            <DashboardsPage embedded />
          ) : isMetricsPilot ? (
            <MetricsPage embedded />
          ) : isMetricHistoryPilot ? (
            <MetricHistoryPage embedded />
          ) : isReportsPilot ? (
            <ReportsPage embedded />
          ) : isRisksPilot ? (
            <RisksPage embedded />
          ) : isControlsPilot ? (
            <ControlsPage embedded />
          ) : isAuditsPilot ? (
            <AuditsPage embedded />
          ) : isConsentsPilot ? (
            <ConsentsPage embedded />
          ) : isMaterialsPilot ? (
            <MaterialsPage embedded />
          ) : isStocksPilot ? (
            <StocksPage embedded />
          ) : isMovementsPilot ? (
            <MovementsPage embedded />
          ) : isPurchasesPilot ? (
            <PurchasesPage embedded />
          ) : isPurchaseItemsPilot ? (
            <PurchaseItemsPage embedded />
          ) : isInventoriesPilot ? (
            <InventoriesPage embedded />
          ) : isClientsPilot ? (
            <ClientsPage embedded />
          ) : isOpportunitiesPilot ? (
            <OpportunitiesPage embedded />
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
