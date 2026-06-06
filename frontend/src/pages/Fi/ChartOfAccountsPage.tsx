import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import ChartOfAccountsForm from "../../components/Fi/ChartOfAccountsForm";
import ChartOfAccountsTable from "../../components/Fi/ChartOfAccountsTable";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./chart-of-accounts-page.css";

type ParentOption = {
  id: number;
  label: string;
};

export type ChartOfAccount = {
  id?: number;
  codigo: string;
  nome: string;
  tipoConta: string;
  natureza: string;
  contaPai: string;
  situacao: string;
};

type ChartOfAccountsPageProps = {
  embedded?: boolean;
};

const emptyAccount: ChartOfAccount = {
  codigo: "",
  nome: "",
  tipoConta: "analitica",
  natureza: "devedora",
  contaPai: "",
  situacao: "ativo",
};

function normalizeAccount(data: Record<string, unknown>): ChartOfAccount {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    codigo: String(data.codigo ?? ""),
    nome: String(data.nome ?? ""),
    tipoConta: String(data.tipoConta ?? "analitica"),
    natureza: String(data.natureza ?? "devedora"),
    contaPai: data.contaPai == null ? "" : String(data.contaPai),
    situacao: String(data.situacao ?? "ativo"),
  };
}

function toRequestPayload(account: ChartOfAccount) {
  return {
    codigo: account.codigo.trim(),
    nome: account.nome.trim(),
    tipoConta: account.tipoConta.trim(),
    natureza: account.natureza.trim(),
    contaPai: account.contaPai.trim() === "" ? null : Number(account.contaPai),
    situacao: account.situacao.trim(),
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

export default function ChartOfAccountsPage({
  embedded = false,
}: ChartOfAccountsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<ChartOfAccount[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<ChartOfAccount | null>(null);
  const [draft, setDraft] = useState<ChartOfAccount>(emptyAccount);
  const permissionSet = useMemo(
    () => new Set(session?.permissoes ?? []),
    [session?.permissoes],
  );
  const isMasterScope = session?.scope === "master";
  const canRead = isMasterScope || permissionSet.has("fi:plano_contas:read");
  const canCreate =
    isMasterScope || permissionSet.has("fi:plano_contas:create");
  const canUpdate =
    isMasterScope || permissionSet.has("fi:plano_contas:update");
  const canDelete =
    isMasterScope || permissionSet.has("fi:plano_contas:delete");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadAccounts() {
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
      const response = await listResource("fi", "planoContas");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeAccount(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel carregar o plano de contas."),
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadAccounts();
  }, [canRead]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.codigo, item.nome, item.tipoConta, item.natureza, item.situacao]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  const parentOptions = useMemo<ParentOption[]>(() => {
    return items
      .filter((item) => item.id != null && item.id !== selected?.id)
      .map((item) => ({
        id: item.id as number,
        label: `${item.codigo} - ${item.nome}`,
      }));
  }, [items, selected?.id]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyAccount });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: ChartOfAccount) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: ChartOfAccount) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar o plano de contas."
          : "Seu perfil nao possui permissao para criar no plano de contas.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("fi", "planoContas", selected.id, payload)
        : await createResource("fi", "planoContas", payload);

      const saved = normalizeAccount(response.data as Record<string, unknown>);
      await loadAccounts();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Conta do plano atualizada com sucesso."
          : "Conta do plano criada com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar a conta do plano."
            : "Nao foi possivel criar a conta do plano.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: ChartOfAccount) {
    if (!canDelete) {
      setError(
        "Seu perfil nao possui permissao para excluir contas do plano.",
      );
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar a conta do plano para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir a conta "${item.codigo || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("fi", "planoContas", item.id);
      await loadAccounts();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyAccount });
      }

      setSuccess("Conta do plano excluida com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          "Nao foi possivel excluir a conta do plano de contas.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "chart-of-accounts-page chart-of-accounts-page--embedded"
          : "chart-of-accounts-page"
      }
    >
      {!embedded ? (
        <header className="chart-of-accounts-page__header">
          <div>
            <span className="chart-of-accounts-page__eyebrow">FI</span>
            <h2 className="chart-of-accounts-page__title">Plano de Contas</h2>
            <p className="chart-of-accounts-page__subtitle">
              Estruture a hierarquia contabil com tipo, natureza, conta pai e
              situacao.
            </p>
          </div>

          <div className="chart-of-accounts-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por codigo, nome, tipo, natureza ou situacao"
              className="chart-of-accounts-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="chart-of-accounts-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova conta
            </button>
          </div>
        </header>
      ) : (
        <div className="chart-of-accounts-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por codigo, nome, tipo, natureza ou situacao"
            className="chart-of-accounts-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="chart-of-accounts-page__toolbar-actions">
            <button
              type="button"
              className="chart-of-accounts-page__ghost"
              onClick={() => void loadAccounts()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="chart-of-accounts-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova conta
            </button>
          </div>
        </div>
      )}

      {error ? (
        <div className="chart-of-accounts-page__alert">{error}</div>
      ) : null}
      {success ? (
        <div className="chart-of-accounts-page__alert chart-of-accounts-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="chart-of-accounts-page__alert chart-of-accounts-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" · ")}
        </div>
      ) : null}

      <div className="chart-of-accounts-page__layout">
        <ChartOfAccountsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          parentOptions={parentOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <ChartOfAccountsForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          parentOptions={parentOptions}
          onChange={handleChange}
          onSave={() => void handleSave()}
          onReset={handleCreateNew}
        />
      </div>
    </div>
  );
}
