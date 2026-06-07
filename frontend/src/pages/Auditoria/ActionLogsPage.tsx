import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import ActionLogsForm from "../../components/Auditoria/ActionLogsForm";
import ActionLogsTable from "../../components/Auditoria/ActionLogsTable";
import { createResource, listResource } from "../../services/resourceService";
import "./action-logs-page.css";

export type UserOption = {
  id: number;
  label: string;
};

export type UserAccess = "idle" | "loaded" | "unavailable";

export type ActionLogEntry = {
  id?: number;
  usuario: string;
  modulo: string;
  acao: string;
  tabela: string;
  registroId: string;
  dadosAntigos: string;
  dadosNovos: string;
  ipAddress: string;
  userAgent: string;
  createdAt?: string;
};

type ActionLogsPageProps = {
  embedded?: boolean;
};

const emptyEntry: ActionLogEntry = {
  usuario: "",
  modulo: "",
  acao: "",
  tabela: "",
  registroId: "",
  dadosAntigos: "",
  dadosNovos: "",
  ipAddress: "",
  userAgent: "",
};

function normalizeEntry(data: Record<string, unknown>): ActionLogEntry {
  return {
    id:
      typeof data.id === "number"
        ? data.id
        : typeof data.id === "bigint"
          ? Number(data.id)
          : undefined,
    usuario: data.usuario == null ? "" : String(data.usuario),
    modulo: String(data.modulo ?? ""),
    acao: String(data.acao ?? ""),
    tabela: String(data.tabela ?? ""),
    registroId: data.registroId == null ? "" : String(data.registroId),
    dadosAntigos:
      data.dadosAntigos == null ? "" : JSON.stringify(data.dadosAntigos, null, 2),
    dadosNovos:
      data.dadosNovos == null ? "" : JSON.stringify(data.dadosNovos, null, 2),
    ipAddress: String(data.ipAddress ?? ""),
    userAgent: String(data.userAgent ?? ""),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function parseJsonMap(value: string) {
  const trimmed = value.trim();

  if (!trimmed) {
    return null;
  }

  const parsed = JSON.parse(trimmed) as unknown;

  if (parsed == null || Array.isArray(parsed) || typeof parsed !== "object") {
    throw new Error("JSON precisa ser um objeto.");
  }

  return parsed as Record<string, unknown>;
}

function toRequestPayload(entry: ActionLogEntry) {
  return {
    usuario: entry.usuario.trim() === "" ? null : Number(entry.usuario),
    modulo: entry.modulo.trim() || null,
    acao: entry.acao.trim() || null,
    tabela: entry.tabela.trim() || null,
    registroId: entry.registroId.trim() === "" ? null : Number(entry.registroId),
    dadosAntigos: parseJsonMap(entry.dadosAntigos),
    dadosNovos: parseJsonMap(entry.dadosNovos),
    ipAddress: entry.ipAddress.trim() || null,
    userAgent: entry.userAgent.trim() || null,
  };
}

function getErrorMessage(error: unknown, fallback: string) {
  if (error instanceof AxiosError) {
    const message = error.response?.data?.message;
    if (typeof message === "string" && message.trim()) {
      return message;
    }
  }

  if (error instanceof Error && error.message.trim()) {
    return error.message;
  }

  return fallback;
}

export default function ActionLogsPage({
  embedded = false,
}: ActionLogsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<ActionLogEntry[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [draft, setDraft] = useState<ActionLogEntry>(emptyEntry);
  const [users, setUsers] = useState<UserOption[]>([]);
  const [userAccess, setUserAccess] = useState<UserAccess>("idle");
  const permissionSet = useMemo(
    () => new Set(session?.permissoes ?? []),
    [session?.permissoes],
  );
  const isMasterScope = session?.scope === "master";
  const canRead = isMasterScope || permissionSet.has("auditoria:log_acoes:read");
  const canCreate =
    isMasterScope || permissionSet.has("auditoria:log_acoes:create");
  const canReadUsers =
    isMasterScope || permissionSet.has("sys:usuarios:read");
  const isBusy = loading || saving;

  async function loadLogs() {
    if (!canRead) {
      setItems([]);
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("auditoria", "logAcoes");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) => normalizeEntry(item as Record<string, unknown>))
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel carregar os logs de acao."));
    } finally {
      setLoading(false);
    }
  }

  async function loadUsers() {
    if (!canReadUsers) {
      setUsers([]);
      setUserAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("sys", "usuarios");
      const nextUsers = Array.isArray(response.data)
        ? response.data
            .map((item) => item as Record<string, unknown>)
            .filter((item) => typeof item.id === "number")
            .map((item) => ({
              id: item.id as number,
              label: `${String(item.nome ?? "Usuario")} (#${String(item.id)})`,
            }))
        : [];
      setUsers(nextUsers);
      setUserAccess("loaded");
    } catch {
      setUsers([]);
      setUserAccess("unavailable");
    }
  }

  useEffect(() => {
    void loadLogs();
  }, [canRead]);

  useEffect(() => {
    void loadUsers();
  }, [canReadUsers]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.modulo, item.acao, item.tabela, item.userAgent, item.ipAddress]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setDraft({ ...emptyEntry });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: ActionLogEntry) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canCreate) {
      setError("Seu perfil nao possui permissao para criar logs de acao.");
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      await createResource("auditoria", "logAcoes", payload);
      await loadLogs();
      setDraft({ ...emptyEntry });
      setSuccess("Log de acao criado com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel criar o log de acao."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded ? "action-logs-page action-logs-page--embedded" : "action-logs-page"
      }
    >
      {!embedded ? (
        <header className="action-logs-page__header">
          <div>
            <span className="action-logs-page__eyebrow">AUDITORIA</span>
            <h2 className="action-logs-page__title">Log de acoes</h2>
            <p className="action-logs-page__subtitle">
              Registre eventos auditaveis com contexto, rastreabilidade e evidencias de alteracao.
            </p>
          </div>

          <div className="action-logs-page__toolbar-actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por modulo, acao, tabela, IP ou user agent"
              className="action-logs-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="action-logs-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate}
            >
              Novo log
            </button>
          </div>
        </header>
      ) : (
        <div className="action-logs-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por modulo, acao, tabela, IP ou user agent"
            className="action-logs-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="action-logs-page__toolbar-actions">
            <button
              type="button"
              className="action-logs-page__ghost"
              onClick={() => void loadLogs()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="action-logs-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate}
            >
              Novo log
            </button>
          </div>
        </div>
      )}

      {error ? <div className="action-logs-page__alert">{error}</div> : null}
      {success ? (
        <div className="action-logs-page__alert action-logs-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate ? (
        <div className="action-logs-page__alert action-logs-page__alert--info">
          {[canRead ? null : "leitura desabilitada", canCreate ? null : "criacao desabilitada"]
            .filter(Boolean)
            .join(" · ")}
        </div>
      ) : null}

      <div className="action-logs-page__layout">
        <ActionLogsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          users={users}
        />

        <ActionLogsForm
          value={draft}
          canCreate={canCreate}
          saving={saving}
          users={users}
          userAccess={userAccess}
          onChange={handleChange}
          onSave={() => void handleSave()}
          onReset={handleCreateNew}
        />
      </div>
    </div>
  );
}
