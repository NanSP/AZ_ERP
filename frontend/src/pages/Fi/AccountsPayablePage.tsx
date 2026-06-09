import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import AccountsPayableTable from "../../components/Fi/AccountsPayableTable";
import AccountsPayableForm from "../../components/Fi/AccountsPayableForm";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./accounts-payable-page.css";

type RelatedOption = {
  id: number;
  label: string;
};

type RelatedAccess = "idle" | "loaded" | "unavailable";

export type AccountPayable = {
  id?: number;
  empresa: string;
  fornecedor: string;
  centroCusto: string;
  numeroDocumento: string;
  descricao: string;
  valorOriginal: string;
  valorPago: string;
  dataEmissao: string;
  dataVencimento: string;
  dataPagamento: string;
  status: string;
  formaPagamento: string;
  createdAt?: string;
};

type AccountsPayablePageProps = {
  embedded?: boolean;
};

const emptyAccount: AccountPayable = {
  empresa: "",
  fornecedor: "",
  centroCusto: "",
  numeroDocumento: "",
  descricao: "",
  valorOriginal: "",
  valorPago: "",
  dataEmissao: "",
  dataVencimento: "",
  dataPagamento: "",
  status: "pendente",
  formaPagamento: "",
};

function normalizeDecimal(value: unknown) {
  if (value == null) {
    return "";
  }

  return String(value);
}

function normalizeAccount(data: Record<string, unknown>): AccountPayable {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    empresa: data.empresa == null ? "" : String(data.empresa),
    fornecedor: data.fornecedor == null ? "" : String(data.fornecedor),
    centroCusto: data.centroCusto == null ? "" : String(data.centroCusto),
    numeroDocumento: String(data.numeroDocumento ?? ""),
    descricao: String(data.descricao ?? ""),
    valorOriginal: normalizeDecimal(data.valorOriginal),
    valorPago: normalizeDecimal(data.valorPago),
    dataEmissao: String(data.dataEmissao ?? ""),
    dataVencimento: String(data.dataVencimento ?? ""),
    dataPagamento: String(data.dataPagamento ?? ""),
    status: String(data.status ?? "pendente"),
    formaPagamento: String(data.formaPagamento ?? ""),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(account: AccountPayable) {
  return {
    empresa: account.empresa.trim() === "" ? null : Number(account.empresa),
    fornecedor:
      account.fornecedor.trim() === "" ? null : Number(account.fornecedor),
    centroCusto:
      account.centroCusto.trim() === "" ? null : Number(account.centroCusto),
    numeroDocumento: account.numeroDocumento.trim() || null,
    descricao: account.descricao.trim() || null,
    valorOriginal:
      account.valorOriginal.trim() === ""
        ? null
        : Number(account.valorOriginal.replace(",", ".")),
    valorPago:
      account.valorPago.trim() === ""
        ? null
        : Number(account.valorPago.replace(",", ".")),
    dataEmissao: account.dataEmissao.trim() || null,
    dataVencimento: account.dataVencimento.trim() || null,
    dataPagamento: account.dataPagamento.trim() || null,
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

export default function AccountsPayablePage({
  embedded = false,
}: AccountsPayablePageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<AccountPayable[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<AccountPayable | null>(null);
  const [draft, setDraft] = useState<AccountPayable>(emptyAccount);
  const [companyOptions, setCompanyOptions] = useState<RelatedOption[]>([]);
  const [supplierOptions, setSupplierOptions] = useState<RelatedOption[]>([]);
  const [costCenterOptions, setCostCenterOptions] = useState<RelatedOption[]>(
    [],
  );
  const [companyAccess, setCompanyAccess] = useState<RelatedAccess>("idle");
  const [supplierAccess, setSupplierAccess] = useState<RelatedAccess>("idle");
  const [costCenterAccess, setCostCenterAccess] =
    useState<RelatedAccess>("idle");
  const isBusy = loading || saving;
  const permissionSet = useMemo(
    () => new Set(session?.permissoes ?? []),
    [session?.permissoes],
  );
  const isMasterScope = session?.scope === "master";
  const canRead = isMasterScope || permissionSet.has("fi:contas_pagar:read");
  const canCreate =
    isMasterScope || permissionSet.has("fi:contas_pagar:create");
  const canUpdate =
    isMasterScope || permissionSet.has("fi:contas_pagar:update");
  const canDelete =
    isMasterScope || permissionSet.has("fi:contas_pagar:delete");
  const canReadCompanies =
    isMasterScope || permissionSet.has("core:empresas:read");
  const canReadPartners =
    isMasterScope || permissionSet.has("core:parceiros:read");
  const canReadCostCenters =
    isMasterScope || permissionSet.has("fi:centros_custo:read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;

  const loadAccountsPayable = useCallback(async () => {
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
      const response = await listResource("fi", "contasPagar");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeAccount(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Não foi possivel carregar as contas a pagar."),
      );
    } finally {
      setLoading(false);
    }
  }, [canRead]);

  const loadCompanies = useCallback(async () => {
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
  }, [canReadCompanies]);

  const loadSuppliers = useCallback(async () => {
    if (!canReadPartners) {
      setSupplierOptions([]);
      setSupplierAccess("unavailable");
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
                String(item.tipoParceiro ?? "").toLowerCase() === "fornecedor",
            )
            .map((item) => ({
              id: item.id as number,
              label: `${String(item.nomeFantasia ?? item.nome ?? "Fornecedor")} (#${String(item.id)})`,
            }))
        : [];
      setSupplierOptions(nextItems);
      setSupplierAccess("loaded");
    } catch {
      setSupplierOptions([]);
      setSupplierAccess("unavailable");
    }
  }, [canReadPartners]);

  const loadCostCenters = useCallback(async () => {
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
  }, [canReadCostCenters]);

  useEffect(() => {
    void loadAccountsPayable();
  }, [loadAccountsPayable]);

  useEffect(() => {
    void loadCompanies();
  }, [loadCompanies]);

  useEffect(() => {
    void loadSuppliers();
  }, [loadSuppliers]);

  useEffect(() => {
    void loadCostCenters();
  }, [loadCostCenters]);

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

  function handleSelect(item: AccountPayable) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: AccountPayable) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissão para atualizar contas a pagar."
          : "Seu perfil nao possui permissão para criar contas a pagar.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("fi", "contasPagar", selected.id, payload)
        : await createResource("fi", "contasPagar", payload);

      const saved = normalizeAccount(response.data as Record<string, unknown>);
      await loadAccountsPayable();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Conta a pagar atualizada com sucesso."
          : "Conta a pagar criada com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Não foi possivel atualizar a conta a pagar."
            : "Não foi possivel criar a conta a pagar.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: AccountPayable) {
    if (!canDelete) {
      setError("Seu perfil não possui permissao para excluir contas a pagar.");
      return;
    }

    if (!item.id) {
      setError("Não foi possivel identificar a conta a pagar para exclusão.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir a conta a pagar "${item.numeroDocumento || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("fi", "contasPagar", item.id);
      await loadAccountsPayable();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyAccount });
      }

      setSuccess("Conta a pagar excluida com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Não foi possivel excluir a conta a pagar."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "accounts-payable-page accounts-payable-page--embedded"
          : "accounts-payable-page"
      }
    >
      {!embedded ? (
        <header className="accounts-payable-page__header">
          <div>
            <span className="accounts-payable-page__eyebrow">FI</span>
            <h2 className="accounts-payable-page__title">Contas a Pagar</h2>
            <p className="accounts-payable-page__subtitle">
              Gerencie obrigacoes financeiras com controle de emissao,
              vencimento e pagamento.
            </p>
          </div>

          <div className="accounts-payable-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por documento, descricao, status ou forma"
              className="accounts-payable-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="accounts-payable-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova conta
            </button>
          </div>
        </header>
      ) : (
        <div className="accounts-payable-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por documento, descricao, status ou forma"
            className="accounts-payable-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="accounts-payable-page__toolbar-actions">
            <button
              type="button"
              className="accounts-payable-page__ghost"
              onClick={() => void loadAccountsPayable()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="accounts-payable-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova conta
            </button>
          </div>
        </div>
      )}

      {error ? (
        <div className="accounts-payable-page__alert">{error}</div>
      ) : null}
      {success ? (
        <div className="accounts-payable-page__alert accounts-payable-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="accounts-payable-page__alert accounts-payable-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" · ")}
        </div>
      ) : null}

      <div className="accounts-payable-page__layout">
        <AccountsPayableTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          companyOptions={companyOptions}
          supplierOptions={supplierOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <AccountsPayableForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          companyOptions={companyOptions}
          supplierOptions={supplierOptions}
          costCenterOptions={costCenterOptions}
          companyAccess={companyAccess}
          supplierAccess={supplierAccess}
          costCenterAccess={costCenterAccess}
          onChange={handleChange}
          onSave={() => void handleSave()}
          onReset={handleCreateNew}
        />
      </div>
    </div>
  );
}
