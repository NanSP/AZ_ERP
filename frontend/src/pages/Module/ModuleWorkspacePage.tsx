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
import DependentsPage from "../Rh/DependentsPage";
import EmployeesPage from "../Rh/EmployeesPage";
import BenefitsPage from "../Rh/BenefitsPage";
import TimeTrackingPage from "../Rh/TimeTrackingPage";
import PayrollPage from "../Rh/PayrollPage";
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
