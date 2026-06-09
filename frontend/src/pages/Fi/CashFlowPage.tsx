import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import CashFlowForm from "../../components/Fi/CashFlowForm";
import CashFlowTable from "../../components/Fi/CashFlowTable";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./cash-flow-page.css";

export type CashFlow = {
  id?: number;
  dataReferencia: string;
  saldoInicial: string;
  entradasPrevistas: string;
  saidasPrevistas: string;
  entradasRealizadas: string;
  saidasRealizadas: string;
  saldoFinalPrevisto: string;
  saldoFinalReal: string;
  createdAt?: string;
};

type CashFlowPageProps = {
  embedded?: boolean;
};

const emptyCashFlow: CashFlow = {
  dataReferencia: "",
  saldoInicial: "0",
  entradasPrevistas: "0",
  saidasPrevistas: "0",
  entradasRealizadas: "0",
  saidasRealizadas: "0",
  saldoFinalPrevisto: "0",
  saldoFinalReal: "0",
};

function normalizeDecimal(value: unknown) {
  if (value == null) {
    return "0";
  }

  return String(value);
}

function normalizeCashFlow(data: Record<string, unknown>): CashFlow {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    dataReferencia: String(data.dataReferencia ?? ""),
    saldoInicial: normalizeDecimal(data.saldoInicial),
    entradasPrevistas: normalizeDecimal(data.entradasPrevistas),
    saidasPrevistas: normalizeDecimal(data.saidasPrevistas),
    entradasRealizadas: normalizeDecimal(data.entradasRealizadas),
    saidasRealizadas: normalizeDecimal(data.saidasRealizadas),
    saldoFinalPrevisto: normalizeDecimal(data.saldoFinalPrevisto),
    saldoFinalReal: normalizeDecimal(data.saldoFinalReal),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toNumberOrZero(value: string) {
  const trimmed = value.trim();
  if (trimmed === "") {
    return 0;
  }

  return Number(trimmed.replace(",", "."));
}

function toRequestPayload(cashFlow: CashFlow) {
  return {
    dataReferencia: cashFlow.dataReferencia.trim() || null,
    saldoInicial: toNumberOrZero(cashFlow.saldoInicial),
    entradasPrevistas: toNumberOrZero(cashFlow.entradasPrevistas),
    saidasPrevistas: toNumberOrZero(cashFlow.saidasPrevistas),
    entradasRealizadas: toNumberOrZero(cashFlow.entradasRealizadas),
    saidasRealizadas: toNumberOrZero(cashFlow.saidasRealizadas),
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

export default function CashFlowPage({
  embedded = false,
}: CashFlowPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<CashFlow[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<CashFlow | null>(null);
  const [draft, setDraft] = useState<CashFlow>(emptyCashFlow);
  const permissionSet = useMemo(
    () => new Set(session?.permissoes ?? []),
    [session?.permissoes],
  );
  const isMasterScope = session?.scope === "master";
  const canRead = isMasterScope || permissionSet.has("fi:fluxo_caixa:read");
  const canCreate =
    isMasterScope || permissionSet.has("fi:fluxo_caixa:create");
  const canUpdate =
    isMasterScope || permissionSet.has("fi:fluxo_caixa:update");
  const canDelete =
    isMasterScope || permissionSet.has("fi:fluxo_caixa:delete");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  const loadCashFlow = useCallback(async () => {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyCashFlow });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("fi", "fluxoCaixa");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeCashFlow(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel carregar o fluxo de caixa."),
      );
    } finally {
      setLoading(false);
    }
  }, [canRead]);

  useEffect(() => {
    void loadCashFlow();
  }, [loadCashFlow]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [
        item.dataReferencia,
        item.saldoInicial,
        item.entradasPrevistas,
        item.saidasPrevistas,
        item.saldoFinalPrevisto,
        item.saldoFinalReal,
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
    setDraft({ ...emptyCashFlow });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: CashFlow) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: CashFlow) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar o fluxo de caixa."
          : "Seu perfil nao possui permissao para criar no fluxo de caixa.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("fi", "fluxoCaixa", selected.id, payload)
        : await createResource("fi", "fluxoCaixa", payload);

      const saved = normalizeCashFlow(response.data as Record<string, unknown>);
      await loadCashFlow();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Fluxo de caixa atualizado com sucesso."
          : "Fluxo de caixa criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o fluxo de caixa."
            : "Nao foi possivel criar o fluxo de caixa.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: CashFlow) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir fluxo de caixa.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar o fluxo de caixa para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o fluxo de caixa de "${item.dataReferencia || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("fi", "fluxoCaixa", item.id);
      await loadCashFlow();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyCashFlow });
      }

      setSuccess("Fluxo de caixa excluido com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel excluir o fluxo de caixa."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded ? "cash-flow-page cash-flow-page--embedded" : "cash-flow-page"
      }
    >
      {!embedded ? (
        <header className="cash-flow-page__header">
          <div>
            <span className="cash-flow-page__eyebrow">FI</span>
            <h2 className="cash-flow-page__title">Fluxo de Caixa</h2>
            <p className="cash-flow-page__subtitle">
              Acompanhe previsoes e realizados por data de referencia com saldo
              final calculado automaticamente.
            </p>
          </div>

          <div className="cash-flow-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por data ou valores do fluxo"
              className="cash-flow-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="cash-flow-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo fluxo
            </button>
          </div>
        </header>
      ) : (
        <div className="cash-flow-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por data ou valores do fluxo"
            className="cash-flow-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="cash-flow-page__toolbar-actions">
            <button
              type="button"
              className="cash-flow-page__ghost"
              onClick={() => void loadCashFlow()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="cash-flow-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo fluxo
            </button>
          </div>
        </div>
      )}

      {error ? <div className="cash-flow-page__alert">{error}</div> : null}
      {success ? (
        <div className="cash-flow-page__alert cash-flow-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="cash-flow-page__alert cash-flow-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" · ")}
        </div>
      ) : null}

      <div className="cash-flow-page__layout">
        <CashFlowTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <CashFlowForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          onChange={handleChange}
          onSave={() => void handleSave()}
          onReset={handleCreateNew}
        />
      </div>
    </div>
  );
}
