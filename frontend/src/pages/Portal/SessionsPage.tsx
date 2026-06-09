import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import SessionForm from "../../components/Portal/SessionForm";
import SessionsTable from "../../components/Portal/SessionsTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./sessions-page.css";

export type UserOption = {
  id: number;
  label: string;
};

export type UserAccess = "idle" | "loaded" | "unavailable";

export type SessionEntry = {
  id?: number;
  usuario: string;
  tokenSessao: string;
  ipAddress: string;
  userAgent: string;
  dataLogin: string;
  dataLogout: string;
  expiracao: string;
};

type SessionsPageProps = {
  embedded?: boolean;
};

const sessionsResource = {
  schema: "portal",
  entity: "sessoes",
  label: "Sessoes",
  description: "Controle de sessões e atividade.",
} as const;

const usersResource = {
  schema: "sys",
  entity: "usuarios",
  label: "Usuarios",
  description: "Usuarios do tenant.",
} as const;

const emptySession: SessionEntry = {
  usuario: "",
  tokenSessao: "",
  ipAddress: "",
  userAgent: "",
  dataLogin: "",
  dataLogout: "",
  expiracao: "",
};

function normalizeDateTime(value: unknown) {
  if (value == null) {
    return "";
  }

  const raw = String(value);
  return raw.length >= 16 ? raw.slice(0, 16) : raw;
}

function normalizeSession(data: Record<string, unknown>): SessionEntry {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    usuario: data.usuario == null ? "" : String(data.usuario),
    tokenSessao: String(data.tokenSessao ?? ""),
    ipAddress: String(data.ipAddress ?? ""),
    userAgent: String(data.userAgent ?? ""),
    dataLogin: normalizeDateTime(data.dataLogin),
    dataLogout: normalizeDateTime(data.dataLogout),
    expiracao: normalizeDateTime(data.expiracao),
  };
}

function toRequestPayload(session: SessionEntry) {
  return {
    usuario: session.usuario.trim() === "" ? null : Number(session.usuario),
    tokenSessao: session.tokenSessao.trim() || null,
    ipAddress: session.ipAddress.trim() || null,
    userAgent: session.userAgent.trim() || null,
    dataLogin: session.dataLogin.trim() || null,
    dataLogout: session.dataLogout.trim() || null,
    expiracao: session.expiracao.trim() || null,
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

function normalizeUserOption(data: Record<string, unknown>): UserOption | null {
  if (typeof data.id !== "number") {
    return null;
  }

  return {
    id: data.id,
    label: `${String(data.nome ?? data.login ?? "Usuario")} (#${String(data.id)})`,
  };
}

export default function SessionsPage({ embedded = false }: SessionsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<SessionEntry[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<SessionEntry | null>(null);
  const [draft, setDraft] = useState<SessionEntry>(emptySession);
  const [userOptions, setUserOptions] = useState<UserOption[]>([]);
  const [userAccess, setUserAccess] = useState<UserAccess>("idle");
  const canRead = canAccessResourceAction(session, sessionsResource, "read");
  const canCreate = canAccessResourceAction(
    session,
    sessionsResource,
    "create",
  );
  const canUpdate = canAccessResourceAction(
    session,
    sessionsResource,
    "update",
  );
  const canReadUsers = canAccessResourceAction(session, usersResource, "read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  const loadSessions = useCallback(async () => {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptySession });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("portal", "sessoes");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeSession(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Não foi possível carregar as sessões."));
    } finally {
      setLoading(false);
    }
  }, [canRead]);

  const loadUsers = useCallback(async () => {
    if (!canReadUsers) {
      setUserOptions([]);
      setUserAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("sys", "usuarios");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) => normalizeUserOption(item as Record<string, unknown>))
            .filter((item): item is UserOption => item !== null)
        : [];
      setUserOptions(nextItems);
      setUserAccess("loaded");
    } catch {
      setUserOptions([]);
      setUserAccess("unavailable");
    }
  }, [canReadUsers]);

  useEffect(() => {
    void loadSessions();
  }, [loadSessions]);

  useEffect(() => {
    void loadUsers();
  }, [loadUsers]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.tokenSessao, item.ipAddress, item.userAgent]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptySession });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: SessionEntry) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: SessionEntry) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil não possui permissão para atualizar sessões."
          : "Seu perfil não possui permissão para criar sessões.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("portal", "sessoes", selected.id, payload)
        : await createResource("portal", "sessoes", payload);

      const saved = normalizeSession(response.data as Record<string, unknown>);
      await loadSessions();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Sessão atualizada com sucesso."
          : "Sessão criada com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Não foi possível atualizar a sessão."
            : "Não foi possível criar a sessão.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded ? "sessions-page sessions-page--embedded" : "sessions-page"
      }
    >
      {!embedded ? (
        <header className="sessions-page__header">
          <div>
            <span className="sessions-page__eyebrow">PORTAL</span>
            <h2 className="sessions-page__title">Sessões</h2>
            <p className="sessions-page__subtitle">
              Gerencie token, IP, user-agent, expiracao e encerramento das
              sessões do usuario.
            </p>
          </div>

          <div className="sessions-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por token, IP ou user-agent"
              className="sessions-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="sessions-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova sessao
            </button>
          </div>
        </header>
      ) : (
        <div className="sessions-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por token, IP ou user-agent"
            className="sessions-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="sessions-page__toolbar-actions">
            <button
              type="button"
              className="sessions-page__ghost"
              onClick={() => void loadSessions()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="sessions-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova sessao
            </button>
          </div>
        </div>
      )}

      {error ? <div className="sessions-page__alert">{error}</div> : null}
      {success ? (
        <div className="sessions-page__alert sessions-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate ? (
        <div className="sessions-page__alert sessions-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}
      <div className="sessions-page__alert sessions-page__alert--info">
        Exclusão de sessões nao e suportada pelo backend deste recurso.
      </div>

      <div className="sessions-page__layout">
        <SessionsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          userOptions={userOptions}
          onSelect={handleSelect}
        />

        <SessionForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          userOptions={userOptions}
          userAccess={userAccess}
          onChange={handleChange}
          onSave={() => void handleSave()}
          onReset={handleCreateNew}
        />
      </div>
    </div>
  );
}
