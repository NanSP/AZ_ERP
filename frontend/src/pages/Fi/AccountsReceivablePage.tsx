import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import AccountsReceivableForm from "../../components/Fi/AccountsReceivableForm";
import AccountsReceivableTable from "../../components/Fi/AccountsReceivableTable";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./accounts-receivable-page.css";

type RelatedOption = {
  id: number;
  label: string;
};

type RelatedAccess = "idle" | "loaded" | "unavailable";

export type AccountReceivable = {
  id?: number;
  empresa: string;
  cliente: string;
  centroCusto: string;
  numeroDocumento: string;
  descricao: string;
  valorOriginal: string;
  valorRecebido: string;
  dataEmissao: string;
  dataVencimento: string;
  dataRecebimento: string;
  status: string;
  formaPagamento: string;
  createdAt?: string;
};

type AccountsReceivablePageProps = {
  embedded?: boolean;
};

const emptyAccount: AccountReceivable = {
  empresa: "",
  cliente: "",
  centroCusto: "",
  numeroDocumento: "",
  descricao: "",
  valorOriginal: "",
  valorRecebido: "",
  dataEmissao: "",
  dataVencimento: "",
  dataRecebimento: "",
  status: "pendente",
  formaPagamento: "",
};

function normalizeDecimal(value: unknown) {
  if (value == null) {
    return "";
  }

  return String(value);
}

function normalizeAccount(data: Record<string, unknown>): AccountReceivable {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    empresa: data.empresa == null ? "" : String(data.empresa),
    cliente: data.cliente == null ? "" : String(data.cliente),
    centroCusto: data.centroCusto == null ? "" : String(data.centroCusto),
    numeroDocumento: String(data.numeroDocumento ?? ""),
    descricao: String(data.descricao ?? ""),
    valorOriginal: normalizeDecimal(data.valorOriginal),
    valorRecebido: normalizeDecimal(data.valorRecebido),
    dataEmissao: String(data.dataEmissao ?? ""),
    dataVencimento: String(data.dataVencimento ?? ""),
    dataRecebimento: String(data.dataRecebimento ?? ""),
    status: String(data.status ?? "pendente"),
    formaPagamento: String(data.formaPagamento ?? ""),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(account: AccountReceivable) {
  return {
    empresa: account.empresa.trim() === "" ? null : Number(account.empresa),
    cliente: account.cliente.trim() === "" ? null : Number(account.cliente),
    centroCusto:
      account.centroCusto.trim() === "" ? null : Number(account.centroCusto),
    numeroDocumento: account.numeroDocumento.trim() || null,
    descricao: account.descricao.trim() || null,
    valorOriginal:
      account.valorOriginal.trim() === ""
        ? null
        : Number(account.valorOriginal.replace(",", ".")),
    valorRecebido:
      account.valorRecebido.trim() === ""
        ? null
        : Number(account.valorRecebido.replace(",", ".")),
    dataEmissao: account.dataEmissao.trim() || null,
    dataVencimento: account.dataVencimento.trim() || null,
    dataRecebimento: account.dataRecebimento.trim() || null,
    formaPagamento: account.formaPagamento.trim() || null,
  };
}

function getErrorMessage(error: unknown, fallback: string) {
  if (error instanceof AxiosError) {
    const message = error.response?.data?.message;
    if (typeof message === "string" && message.trim()) {
      return message;
    }
  }

  return fallback;
}

export default function AccountsReceivablePage({
  embedded = false,
}: AccountsReceivablePageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<AccountReceivable[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<AccountReceivable | null>(null);
  const [draft, setDraft] = useState<AccountReceivable>(emptyAccount);
  const [companyOptions, setCompanyOptions] = useState<RelatedOption[]>([]);
  const [clientOptions, setClientOptions] = useState<RelatedOption[]>([]);
  const [costCenterOptions, setCostCenterOptions] = useState<RelatedOption[]>(
    [],
  );
  const [companyAccess, setCompanyAccess] = useState<RelatedAccess>("idle");
  const [clientAccess, setClientAccess] = useState<RelatedAccess>("idle");
  const [costCenterAccess, setCostCenterAccess] = useState<RelatedAccess>("idle");
  const isBusy = loading || saving;
  const permissionSet = useMemo(
    () => new Set(session?.permissoes ?? []),
    [session?.permissoes],
  );
  const isMasterScope = session?.scope === "master";
  const canRead = isMasterScope || permissionSet.has("fi:contas_receber:read");
  const canCreate =
    isMasterScope || permissionSet.has("fi:contas_receber:create");
  const canUpdate =
    isMasterScope || permissionSet.has("fi:contas_receber:update");
  const canDelete =
    isMasterScope || permissionSet.has("fi:contas_receber:delete");
  const canReadCompanies =
    isMasterScope || permissionSet.has("core:empresas:read");
  const canReadPartners =
    isMasterScope || permissionSet.has("core:parceiros:read");
  const canReadCostCenters =
    isMasterScope || permissionSet.has("fi:centros_custo:read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;

  async function loadAccountsReceivable() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyAccount });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("fi", "contasReceber");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeAccount(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          "Nao foi possivel carregar as contas a receber.",
        ),
      );
    } finally {
      setLoading(false);
    }
  }

  async function loadCompanies() {
    if (!canReadCompanies) {
      setCompanyOptions([]);
      setCompanyAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("core", "empresas");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) => item as Record<string, unknown>)
            .filter((item) => typeof item.id === "number")
            .map((item) => ({
              id: item.id as number,
              label: `${String(item.nomeFantasia ?? item.razaoSocial ?? "Empresa")} (#${String(item.id)})`,
            }))
        : [];
      setCompanyOptions(nextItems);
      setCompanyAccess("loaded");
    } catch {
      setCompanyOptions([]);
      setCompanyAccess("unavailable");
    }
  }

  async function loadClients() {
    if (!canReadPartners) {
      setClientOptions([]);
      setClientAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("core", "parceiros");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) => item as Record<string, unknown>)
            .filter(
              (item) =>
                typeof item.id === "number" &&
                String(item.tipoParceiro ?? "").toLowerCase() === "cliente",
            )
            .map((item) => ({
              id: item.id as number,
              label: `${String(item.nomeFantasia ?? item.nome ?? "Cliente")} (#${String(item.id)})`,
            }))
        : [];
      setClientOptions(nextItems);
      setClientAccess("loaded");
    } catch {
      setClientOptions([]);
      setClientAccess("unavailable");
    }
  }

  async function loadCostCenters() {
    if (!canReadCostCenters) {
      setCostCenterOptions([]);
      setCostCenterAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("fi", "centrosCusto");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) => item as Record<string, unknown>)
            .filter((item) => typeof item.id === "number")
            .map((item) => ({
              id: item.id as number,
              label: `${String(item.nome ?? item.codigo ?? "Centro de custo")} (#${String(item.id)})`,
            }))
        : [];
      setCostCenterOptions(nextItems);
      setCostCenterAccess("loaded");
    } catch {
      setCostCenterOptions([]);
      setCostCenterAccess("unavailable");
    }
  }

  useEffect(() => {
    void loadAccountsReceivable();
  }, [canRead]);

  useEffect(() => {
    void loadCompanies();
  }, [canReadCompanies]);

  useEffect(() => {
    void loadClients();
  }, [canReadPartners]);

  useEffect(() => {
    void loadCostCenters();
  }, [canReadCostCenters]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [
        item.numeroDocumento,
        item.descricao,
        item.status,
        item.formaPagamento,
        item.valorOriginal,
      ]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyAccount });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: AccountReceivable) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: AccountReceivable) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar contas a receber."
          : "Seu perfil nao possui permissao para criar contas a receber.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("fi", "contasReceber", selected.id, payload)
        : await createResource("fi", "contasReceber", payload);

      const saved = normalizeAccount(response.data as Record<string, unknown>);
      await loadAccountsReceivable();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Conta a receber atualizada com sucesso."
          : "Conta a receber criada com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar a conta a receber."
            : "Nao foi possivel criar a conta a receber.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: AccountReceivable) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir contas a receber.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar a conta a receber para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir a conta a receber "${item.numeroDocumento || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("fi", "contasReceber", item.id);
      await loadAccountsReceivable();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyAccount });
      }

      setSuccess("Conta a receber excluida com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel excluir a conta a receber."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "accounts-receivable-page accounts-receivable-page--embedded"
          : "accounts-receivable-page"
      }
    >
      {!embedded ? (
        <header className="accounts-receivable-page__header">
          <div>
            <span className="accounts-receivable-page__eyebrow">FI</span>
            <h2 className="accounts-receivable-page__title">
              Contas a Receber
            </h2>
            <p className="accounts-receivable-page__subtitle">
              Gerencie recebimentos com controle de emissao, vencimento e baixa
              financeira.
            </p>
          </div>

          <div className="accounts-receivable-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por documento, descricao, status ou forma"
              className="accounts-receivable-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="accounts-receivable-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova conta
            </button>
          </div>
        </header>
      ) : (
        <div className="accounts-receivable-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por documento, descricao, status ou forma"
            className="accounts-receivable-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="accounts-receivable-page__toolbar-actions">
            <button
              type="button"
              className="accounts-receivable-page__ghost"
              onClick={() => void loadAccountsReceivable()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="accounts-receivable-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova conta
            </button>
          </div>
        </div>
      )}

      {error ? (
        <div className="accounts-receivable-page__alert">{error}</div>
      ) : null}
      {success ? (
        <div className="accounts-receivable-page__alert accounts-receivable-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="accounts-receivable-page__alert accounts-receivable-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" · ")}
        </div>
      ) : null}

      <div className="accounts-receivable-page__layout">
        <AccountsReceivableTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          companyOptions={companyOptions}
          clientOptions={clientOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <AccountsReceivableForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          companyOptions={companyOptions}
          clientOptions={clientOptions}
          costCenterOptions={costCenterOptions}
          companyAccess={companyAccess}
          clientAccess={clientAccess}
          costCenterAccess={costCenterAccess}
          onChange={handleChange}
          onSave={() => void handleSave()}
          onReset={handleCreateNew}
        />
      </div>
    </div>
  );
}
