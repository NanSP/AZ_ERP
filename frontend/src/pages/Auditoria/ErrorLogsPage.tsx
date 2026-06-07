import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import ErrorLogsForm from "../../components/Auditoria/ErrorLogsForm";
import ErrorLogsTable from "../../components/Auditoria/ErrorLogsTable";
import { createResource, listResource } from "../../services/resourceService";
import "./error-logs-page.css";

export type UserOption = {
  id: number;
  label: string;
};

export type UserAccess = "idle" | "loaded" | "unavailable";

export type ErrorLogEntry = {
  id?: number;
  erroCodigo: string;
  erroMensagem: string;
  modulo: string;
  usuario: string;
  url: string;
  parametros: string;
  ipAddress: string;
  createdAt?: string;
};

type ErrorLogsPageProps = {
  embedded?: boolean;
};

const emptyEntry: ErrorLogEntry = {
  erroCodigo: "",
  erroMensagem: "",
  modulo: "",
  usuario: "",
  url: "",
  parametros: "",
  ipAddress: "",
};

function normalizeEntry(data: Record<string, unknown>): ErrorLogEntry {
  return {
    id:
      typeof data.id === "number"
        ? data.id
        : typeof data.id === "bigint"
          ? Number(data.id)
          : undefined,
    erroCodigo: data.erroCodigo == null ? "" : String(data.erroCodigo),
    erroMensagem: String(data.erroMensagem ?? ""),
    modulo: String(data.modulo ?? ""),
    usuario: data.usuario == null ? "" : String(data.usuario),
    url: String(data.url ?? ""),
    parametros:
      data.parametros == null ? "" : JSON.stringify(data.parametros, null, 2),
    ipAddress: String(data.ipAddress ?? ""),
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

function toRequestPayload(entry: ErrorLogEntry) {
  return {
    erroCodigo:
      entry.erroCodigo.trim() === "" ? null : Number(entry.erroCodigo),
    erroMensagem: entry.erroMensagem.trim() || null,
    modulo: entry.modulo.trim() || null,
    usuario: entry.usuario.trim() === "" ? null : Number(entry.usuario),
    url: entry.url.trim() || null,
    parametros: parseJsonMap(entry.parametros),
    ipAddress: entry.ipAddress.trim() || null,
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

export default function ErrorLogsPage({
  embedded = false,
}: ErrorLogsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<ErrorLogEntry[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [draft, setDraft] = useState<ErrorLogEntry>(emptyEntry);
  const [users, setUsers] = useState<UserOption[]>([]);
  const [userAccess, setUserAccess] = useState<UserAccess>("idle");
  const permissionSet = useMemo(
    () => new Set(session?.permissoes ?? []),
    [session?.permissoes],
  );
  const isMasterScope = session?.scope === "master";
  const canRead = isMasterScope || permissionSet.has("auditoria:log_erros:read");
  const canCreate =
    isMasterScope || permissionSet.has("auditoria:log_erros:create");
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
      const response = await listResource("auditoria", "logErros");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) => normalizeEntry(item as Record<string, unknown>))
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel carregar os logs de erro."));
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
      [item.erroMensagem, item.modulo, item.url, item.ipAddress, item.erroCodigo]
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

  function handleChange(next: ErrorLogEntry) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canCreate) {
      setError("Seu perfil nao possui permissao para criar logs de erro.");
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      await createResource("auditoria", "logErros", payload);
      await loadLogs();
      setDraft({ ...emptyEntry });
      setSuccess("Log de erro criado com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel criar o log de erro."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded ? "error-logs-page error-logs-page--embedded" : "error-logs-page"
      }
    >
      {!embedded ? (
        <header className="error-logs-page__header">
          <div>
            <span className="error-logs-page__eyebrow">AUDITORIA</span>
            <h2 className="error-logs-page__title">Log de erros</h2>
            <p className="error-logs-page__subtitle">
              Registre erros de aplicacao com contexto tecnico, URL e rastreabilidade operacional.
            </p>
          </div>

          <div className="error-logs-page__toolbar-actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por mensagem, modulo, URL, IP ou codigo"
              className="error-logs-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="error-logs-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate}
            >
              Novo log
            </button>
          </div>
        </header>
      ) : (
        <div className="error-logs-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por mensagem, modulo, URL, IP ou codigo"
            className="error-logs-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="error-logs-page__toolbar-actions">
            <button
              type="button"
              className="error-logs-page__ghost"
              onClick={() => void loadLogs()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="error-logs-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate}
            >
              Novo log
            </button>
          </div>
        </div>
      )}

      {error ? <div className="error-logs-page__alert">{error}</div> : null}
      {success ? (
        <div className="error-logs-page__alert error-logs-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate ? (
        <div className="error-logs-page__alert error-logs-page__alert--info">
          {[canRead ? null : "leitura desabilitada", canCreate ? null : "criacao desabilitada"]
            .filter(Boolean)
            .join(" · ")}
        </div>
      ) : null}

      <div className="error-logs-page__layout">
        <ErrorLogsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          users={users}
        />

        <ErrorLogsForm
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
