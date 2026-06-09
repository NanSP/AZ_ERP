import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import DashboardForm from "../../components/Bi/DashboardForm";
import DashboardsTable from "../../components/Bi/DashboardsTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./dashboards-page.css";

export type DashboardRecord = {
  id?: number;
  nome: string;
  descricao: string;
  layout: string;
  configuracoes: string;
  createdAt?: string;
};

type DashboardsPageProps = {
  embedded?: boolean;
};

const dashboardsResource = {
  schema: "bi",
  entity: "dashboards",
  label: "Dashboards",
  description: "Dashboards e paineis executivos.",
} as const;

const emptyDashboard: DashboardRecord = {
  nome: "",
  descricao: "",
  layout: '{\n  "widgets": []\n}',
  configuracoes: '{\n  "tema": "corporativo"\n}',
};

function stringifyObject(value: unknown) {
  if (value == null) {
    return "";
  }

  try {
    return JSON.stringify(value, null, 2);
  } catch {
    return "";
  }
}

function normalizeDashboard(data: Record<string, unknown>): DashboardRecord {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    nome: String(data.nome ?? ""),
    descricao: String(data.descricao ?? ""),
    layout: stringifyObject(data.layout),
    configuracoes: stringifyObject(data.configuracoes),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function parseJsonOrNull(value: string, fieldLabel: string) {
  const trimmed = value.trim();

  if (!trimmed) {
    return null;
  }

  try {
    return JSON.parse(trimmed) as Record<string, unknown>;
  } catch {
    throw new Error(`${fieldLabel} precisa conter um JSON valido.`);
  }
}

function toRequestPayload(item: DashboardRecord) {
  return {
    nome: item.nome.trim() || null,
    descricao: item.descricao.trim() || null,
    layout: parseJsonOrNull(item.layout, "Layout"),
    configuracoes: parseJsonOrNull(item.configuracoes, "Configuracoes"),
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

  if (error instanceof Error && error.message.trim()) {
    return error.message;
  }

  return fallback;
}

export default function DashboardsPage({
  embedded = false,
}: DashboardsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<DashboardRecord[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<DashboardRecord | null>(null);
  const [draft, setDraft] = useState<DashboardRecord>(emptyDashboard);
  const canRead = canAccessResourceAction(session, dashboardsResource, "read");
  const canCreate = canAccessResourceAction(
    session,
    dashboardsResource,
    "create",
  );
  const canUpdate = canAccessResourceAction(
    session,
    dashboardsResource,
    "update",
  );
  const canDelete = canAccessResourceAction(
    session,
    dashboardsResource,
    "delete",
  );
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  const loadDashboards = useCallback(async () => {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyDashboard });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("bi", "dashboards");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeDashboard(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel carregar os dashboards."),
      );
    } finally {
      setLoading(false);
    }
  }, [canRead]);

  useEffect(() => {
    void loadDashboards();
  }, [loadDashboards]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.nome, item.descricao, item.layout, item.configuracoes]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyDashboard });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: DashboardRecord) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: DashboardRecord) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissão para atualizar dashboards."
          : "Seu perfil nao possui permissão para criar dashboards.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("bi", "dashboards", selected.id, payload)
        : await createResource("bi", "dashboards", payload);

      const saved = normalizeDashboard(
        response.data as Record<string, unknown>,
      );
      await loadDashboards();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Dashboard atualizado com sucesso."
          : "Dashboard criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Não foi possivel atualizar o dashboard."
            : "Não foi possivel criar o dashboard.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: DashboardRecord) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissão para excluir dashboards.");
      return;
    }

    if (!item.id) {
      setError("Não foi possível identificar o dashboard para exclusão.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o dashboard "${item.nome || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("bi", "dashboards", item.id);
      await loadDashboards();
      setSuccess("Dashboard excluido com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Não foi possivel excluir o dashboard."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "dashboards-page dashboards-page--embedded"
          : "dashboards-page"
      }
    >
      {!embedded ? (
        <header className="dashboards-page__header">
          <div>
            <span className="dashboards-page__eyebrow">BI</span>
            <h2 className="dashboards-page__title">Dashboards</h2>
            <p className="dashboards-page__subtitle">
              Organize paineis executivos com layout e configurações
              customizadas.
            </p>
          </div>

          <div className="dashboards-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por nome, descricao ou JSON"
              className="dashboards-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="dashboards-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo dashboard
            </button>
          </div>
        </header>
      ) : (
        <div className="dashboards-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por nome, descricao ou JSON"
            className="dashboards-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="dashboards-page__toolbar-actions">
            <button
              type="button"
              className="dashboards-page__ghost"
              onClick={() => void loadDashboards()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="dashboards-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo dashboard
            </button>
          </div>
        </div>
      )}

      {error ? <div className="dashboards-page__alert">{error}</div> : null}
      {success ? (
        <div className="dashboards-page__alert dashboards-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="dashboards-page__alert dashboards-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="dashboards-page__layout">
        <DashboardsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <DashboardForm
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
