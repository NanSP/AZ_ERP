import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import NotificationForm from "../../components/Portal/NotificationForm";
import NotificationsTable from "../../components/Portal/NotificationsTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./notifications-page.css";

export type UserOption = {
  id: number;
  label: string;
};

export type UserAccess = "idle" | "loaded" | "unavailable";

export type Notification = {
  id?: number;
  usuario: string;
  titulo: string;
  mensagem: string;
  tipo: string;
  lida: boolean;
  dataLeitura: string;
  createdAt?: string;
};

type NotificationsPageProps = {
  embedded?: boolean;
};

const notificationsResource = {
  schema: "portal",
  entity: "notificacoes",
  label: "Notificacoes",
  description: "Mensagens e notificacoes internas.",
} as const;

const usersResource = {
  schema: "sys",
  entity: "usuarios",
  label: "Usuarios",
  description: "Usuarios do tenant.",
} as const;

const emptyNotification: Notification = {
  usuario: "",
  titulo: "",
  mensagem: "",
  tipo: "info",
  lida: false,
  dataLeitura: "",
};

function normalizeDateTime(value: unknown) {
  if (value == null) {
    return "";
  }

  const raw = String(value);
  return raw.length >= 16 ? raw.slice(0, 16) : raw;
}

function normalizeNotification(data: Record<string, unknown>): Notification {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    usuario: data.usuario == null ? "" : String(data.usuario),
    titulo: String(data.titulo ?? ""),
    mensagem: String(data.mensagem ?? ""),
    tipo: String(data.tipo ?? "info"),
    lida: Boolean(data.lida),
    dataLeitura: normalizeDateTime(data.dataLeitura),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(notification: Notification) {
  return {
    usuario:
      notification.usuario.trim() === "" ? null : Number(notification.usuario),
    titulo: notification.titulo.trim() || null,
    mensagem: notification.mensagem.trim() || null,
    tipo: notification.tipo.trim() || null,
    lida: notification.lida,
    dataLeitura:
      notification.lida && notification.dataLeitura.trim() !== ""
        ? notification.dataLeitura
        : null,
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

export default function NotificationsPage({
  embedded = false,
}: NotificationsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<Notification | null>(null);
  const [draft, setDraft] = useState<Notification>(emptyNotification);
  const [userOptions, setUserOptions] = useState<UserOption[]>([]);
  const [userAccess, setUserAccess] = useState<UserAccess>("idle");
  const canRead = canAccessResourceAction(
    session,
    notificationsResource,
    "read",
  );
  const canCreate = canAccessResourceAction(
    session,
    notificationsResource,
    "create",
  );
  const canUpdate = canAccessResourceAction(
    session,
    notificationsResource,
    "update",
  );
  const canDelete = canAccessResourceAction(
    session,
    notificationsResource,
    "delete",
  );
  const canReadUsers = canAccessResourceAction(session, usersResource, "read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadNotifications() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyNotification });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("portal", "notificacoes");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeNotification(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel carregar as notificacoes."),
      );
    } finally {
      setLoading(false);
    }
  }

  async function loadUsers() {
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
  }

  useEffect(() => {
    void loadNotifications();
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
      [item.titulo, item.mensagem, item.tipo]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyNotification });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: Notification) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: Notification) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar notificacoes."
          : "Seu perfil nao possui permissao para criar notificacoes.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("portal", "notificacoes", selected.id, payload)
        : await createResource("portal", "notificacoes", payload);

      const saved = normalizeNotification(response.data as Record<string, unknown>);
      await loadNotifications();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Notificacao atualizada com sucesso."
          : "Notificacao criada com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar a notificacao."
            : "Nao foi possivel criar a notificacao.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: Notification) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir notificacoes.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar a notificacao para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir a notificacao "${item.titulo || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("portal", "notificacoes", item.id);
      await loadNotifications();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyNotification });
      }

      setSuccess("Notificacao excluida com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel excluir a notificacao."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "notifications-page notifications-page--embedded"
          : "notifications-page"
      }
    >
      {!embedded ? (
        <header className="notifications-page__header">
          <div>
            <span className="notifications-page__eyebrow">PORTAL</span>
            <h2 className="notifications-page__title">Notificacoes</h2>
            <p className="notifications-page__subtitle">
              Gerencie mensagens internas, estados de leitura e contexto de notificacao por usuario.
            </p>
          </div>

          <div className="notifications-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por titulo, mensagem ou tipo"
              className="notifications-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="notifications-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova notificacao
            </button>
          </div>
        </header>
      ) : (
        <div className="notifications-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por titulo, mensagem ou tipo"
            className="notifications-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="notifications-page__toolbar-actions">
            <button
              type="button"
              className="notifications-page__ghost"
              onClick={() => void loadNotifications()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="notifications-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova notificacao
            </button>
          </div>
        </div>
      )}

      {error ? <div className="notifications-page__alert">{error}</div> : null}
      {success ? (
        <div className="notifications-page__alert notifications-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="notifications-page__alert notifications-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="notifications-page__layout">
        <NotificationsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          userOptions={userOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <NotificationForm
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
