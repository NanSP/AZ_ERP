import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import MetricHistoryForm from "../../components/Bi/MetricHistoryForm";
import MetricHistoryTable from "../../components/Bi/MetricHistoryTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./metric-history-page.css";

export type MetricOption = {
  id: number;
  label: string;
};

export type MetricAccess = "idle" | "loaded" | "unavailable";

export type MetricHistoryRecord = {
  id?: number;
  metrica: string;
  periodo: string;
  valorApurado: string;
  createdAt?: string;
};

type MetricHistoryPageProps = {
  embedded?: boolean;
};

const historyResource = {
  schema: "bi",
  entity: "historicoMetricas",
  label: "Historico de Metricas",
  description: "Evolucao historica dos indicadores.",
} as const;

const metricsResource = {
  schema: "bi",
  entity: "metricas",
  label: "Metricas",
  description: "Metricas e indicadores.",
} as const;

const emptyHistory: MetricHistoryRecord = {
  metrica: "",
  periodo: "",
  valorApurado: "",
};

function normalizeHistory(
  data: Record<string, unknown>,
): MetricHistoryRecord {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    metrica: data.metrica == null ? "" : String(data.metrica),
    periodo: String(data.periodo ?? ""),
    valorApurado: data.valorApurado == null ? "" : String(data.valorApurado),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(item: MetricHistoryRecord) {
  return {
    metrica: item.metrica.trim() === "" ? null : Number(item.metrica),
    periodo: item.periodo.trim() || null,
    valorApurado:
      item.valorApurado.trim() === ""
        ? null
        : Number(item.valorApurado.replace(",", ".")),
  };
}

function getErrorMessage(error: unknown, fallback: string) {
  if (error instanceof AxiosError) {
    const message =
      error.response?.data?.message ?? error.response?.data?.error;
    if (typeof message === "string" && message.trim()) {
      return message;
    }
  }

  return fallback;
}

function mapMetricOption(item: Record<string, unknown>): MetricOption | null {
  if (typeof item.id !== "number") {
    return null;
  }

  const name = String(item.nome ?? "Metrica");
  const category = String(item.categoria ?? "");
  return {
    id: item.id,
    label: `${name}${category ? ` - ${category}` : ""} (#${String(item.id)})`,
  };
}

export default function MetricHistoryPage({
  embedded = false,
}: MetricHistoryPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<MetricHistoryRecord[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<MetricHistoryRecord | null>(null);
  const [draft, setDraft] = useState<MetricHistoryRecord>(emptyHistory);
  const [metricOptions, setMetricOptions] = useState<MetricOption[]>([]);
  const [metricAccess, setMetricAccess] = useState<MetricAccess>("idle");
  const canRead = canAccessResourceAction(session, historyResource, "read");
  const canCreate = canAccessResourceAction(session, historyResource, "create");
  const canUpdate = canAccessResourceAction(session, historyResource, "update");
  const canDelete = canAccessResourceAction(session, historyResource, "delete");
  const canReadMetrics = canAccessResourceAction(session, metricsResource, "read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadHistory() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyHistory });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("bi", "historicoMetricas");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeHistory(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          "Nao foi possivel carregar o historico de metricas.",
        ),
      );
    } finally {
      setLoading(false);
    }
  }

  async function loadMetrics() {
    if (!canReadMetrics) {
      setMetricOptions([]);
      setMetricAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("bi", "metricas");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) => mapMetricOption(item as Record<string, unknown>))
            .filter((item): item is MetricOption => item !== null)
        : [];
      setMetricOptions(nextItems);
      setMetricAccess("loaded");
    } catch {
      setMetricOptions([]);
      setMetricAccess("unavailable");
    }
  }

  useEffect(() => {
    void loadHistory();
  }, [canRead]);

  useEffect(() => {
    void loadMetrics();
  }, [canReadMetrics]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.metrica, item.periodo, item.valorApurado]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyHistory });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: MetricHistoryRecord) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: MetricHistoryRecord) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar historico de metricas."
          : "Seu perfil nao possui permissao para criar historico de metricas.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("bi", "historicoMetricas", selected.id, payload)
        : await createResource("bi", "historicoMetricas", payload);

      const saved = normalizeHistory(response.data as Record<string, unknown>);
      await loadHistory();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Historico atualizado com sucesso."
          : "Historico criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o historico."
            : "Nao foi possivel criar o historico.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: MetricHistoryRecord) {
    if (!canDelete) {
      setError(
        "Seu perfil nao possui permissao para excluir historico de metricas.",
      );
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar o historico para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o historico #${item.id}?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("bi", "historicoMetricas", item.id);
      await loadHistory();
      setSuccess("Historico excluido com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel excluir o historico."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "metric-history-page metric-history-page--embedded"
          : "metric-history-page"
      }
    >
      {!embedded ? (
        <header className="metric-history-page__header">
          <div>
            <span className="metric-history-page__eyebrow">BI</span>
            <h2 className="metric-history-page__title">Historico de metricas</h2>
            <p className="metric-history-page__subtitle">
              Registre a evolucao dos indicadores por periodo.
            </p>
          </div>

          <div className="metric-history-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por metrica, periodo ou valor"
              className="metric-history-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="metric-history-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo historico
            </button>
          </div>
        </header>
      ) : (
        <div className="metric-history-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por metrica, periodo ou valor"
            className="metric-history-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="metric-history-page__toolbar-actions">
            <button
              type="button"
              className="metric-history-page__ghost"
              onClick={() => void loadHistory()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="metric-history-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo historico
            </button>
          </div>
        </div>
      )}

      {error ? <div className="metric-history-page__alert">{error}</div> : null}
      {success ? (
        <div className="metric-history-page__alert metric-history-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="metric-history-page__alert metric-history-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="metric-history-page__layout">
        <MetricHistoryTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          metricOptions={metricOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <MetricHistoryForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          metricOptions={metricOptions}
          metricAccess={metricAccess}
          onChange={handleChange}
          onSave={() => void handleSave()}
          onReset={handleCreateNew}
        />
      </div>
    </div>
  );
}
