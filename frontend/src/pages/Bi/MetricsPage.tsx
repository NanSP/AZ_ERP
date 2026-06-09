import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import MetricForm from "../../components/Bi/MetricForm";
import MetricsTable from "../../components/Bi/MetricsTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./metrics-page.css";

export type MetricRecord = {
  id?: number;
  nome: string;
  descricao: string;
  categoria: string;
  formula: string;
  unidadeMedida: string;
  meta: string;
  createdAt?: string;
};

type MetricsPageProps = {
  embedded?: boolean;
};

const metricsResource = {
  schema: "bi",
  entity: "metricas",
  label: "Metricas",
  description: "Metricas e indicadores.",
} as const;

const emptyMetric: MetricRecord = {
  nome: "",
  descricao: "",
  categoria: "estrategica",
  formula: "",
  unidadeMedida: "",
  meta: "",
};

function normalizeMetric(data: Record<string, unknown>): MetricRecord {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    nome: String(data.nome ?? ""),
    descricao: String(data.descricao ?? ""),
    categoria: String(data.categoria ?? "estrategica"),
    formula: String(data.formula ?? ""),
    unidadeMedida: String(data.unidadeMedida ?? ""),
    meta: data.meta == null ? "" : String(data.meta),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(item: MetricRecord) {
  return {
    nome: item.nome.trim() || null,
    descricao: item.descricao.trim() || null,
    categoria: item.categoria.trim() || null,
    formula: item.formula.trim() || null,
    unidadeMedida: item.unidadeMedida.trim() || null,
    meta: item.meta.trim() === "" ? null : Number(item.meta.replace(",", ".")),
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

export default function MetricsPage({ embedded = false }: MetricsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<MetricRecord[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<MetricRecord | null>(null);
  const [draft, setDraft] = useState<MetricRecord>(emptyMetric);
  const canRead = canAccessResourceAction(session, metricsResource, "read");
  const canCreate = canAccessResourceAction(session, metricsResource, "create");
  const canUpdate = canAccessResourceAction(session, metricsResource, "update");
  const canDelete = canAccessResourceAction(session, metricsResource, "delete");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  const loadMetrics = useCallback(async () => {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyMetric });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("bi", "metricas");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeMetric(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Não foi possível carregar as métricas."));
    } finally {
      setLoading(false);
    }
  }, [canRead]);

  useEffect(() => {
    void loadMetrics();
  }, [loadMetrics]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.nome, item.descricao, item.categoria, item.unidadeMedida]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyMetric });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: MetricRecord) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: MetricRecord) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil não possui permissão para atualizar métricas."
          : "Seu perfil não possui permissão para criar métricas.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("bi", "metricas", selected.id, payload)
        : await createResource("bi", "metricas", payload);

      const saved = normalizeMetric(response.data as Record<string, unknown>);
      await loadMetrics();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Métrica atualizada com sucesso."
          : "Métrica criada com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Não foi possível atualizar a métrica."
            : "Não foi possível criar a métrica.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: MetricRecord) {
    if (!canDelete) {
      setError("Seu perfil não possui permissão para excluir métricas.");
      return;
    }

    if (!item.id) {
      setError("Não foi possível identificar a métrica para exclusão.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir a métrica "${item.nome || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("bi", "metricas", item.id);
      await loadMetrics();
      setSuccess("Métrica excluída com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Não foi possível excluir a métrica."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded ? "metrics-page metrics-page--embedded" : "metrics-page"
      }
    >
      {!embedded ? (
        <header className="metrics-page__header">
          <div>
            <span className="metrics-page__eyebrow">BI</span>
            <h2 className="metrics-page__title">Métricas</h2>
            <p className="metrics-page__subtitle">
              Defina indicadores, categoria analítica, fórmula, unidade e meta.
            </p>
          </div>

          <div className="metrics-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por nome, categoria ou unidade"
              className="metrics-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="metrics-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova métrica
            </button>
          </div>
        </header>
      ) : (
        <div className="metrics-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por nome, categoria ou unidade"
            className="metrics-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="metrics-page__toolbar-actions">
            <button
              type="button"
              className="metrics-page__ghost"
              onClick={() => void loadMetrics()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="metrics-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova métrica
            </button>
          </div>
        </div>
      )}

      {error ? <div className="metrics-page__alert">{error}</div> : null}
      {success ? (
        <div className="metrics-page__alert metrics-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="metrics-page__alert metrics-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criação desabilitada",
            canDelete ? null : "exclusão desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="metrics-page__layout">
        <MetricsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <MetricForm
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
