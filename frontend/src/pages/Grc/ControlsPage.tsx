import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import ControlForm from "../../components/Grc/ControlForm";
import ControlsTable from "../../components/Grc/ControlsTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import type { User } from "../Sys/UsersPage";
import "./controls-page.css";

export type ControlRecord = {
  id?: number;
  codigo: string;
  descricao: string;
  tipoControle: string;
  frequencia: string;
  responsavelId: string;
  efetivo: boolean;
  createdAt?: string;
};

type ControlsPageProps = {
  embedded?: boolean;
};

const controlsResource = {
  schema: "grc",
  entity: "controles",
  label: "Controles",
  description: "Controles internos e governanca.",
} as const;

const usersResource = {
  schema: "sys",
  entity: "usuarios",
  label: "Usuarios",
  description: "Gestao de usuarios do tenant.",
} as const;

const emptyControl: ControlRecord = {
  codigo: "",
  descricao: "",
  tipoControle: "preventivo",
  frequencia: "mensal",
  responsavelId: "",
  efetivo: false,
};

function normalizeControl(data: Record<string, unknown>): ControlRecord {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    codigo: String(data.codigo ?? ""),
    descricao: String(data.descricao ?? ""),
    tipoControle: String(data.tipoControle ?? "preventivo"),
    frequencia: String(data.frequencia ?? "mensal"),
    responsavelId: data.responsavel == null ? "" : String(data.responsavel),
    efetivo: Boolean(data.efetivo),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function normalizeUser(data: Record<string, unknown>): User {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    nome: String(data.nome ?? ""),
    email: String(data.email ?? ""),
    login: String(data.login ?? ""),
    senha: "",
    documento: String(data.documento ?? ""),
    tipoUsuario: String(data.tipoUsuario ?? "operador"),
    status: String(data.status ?? "ativo"),
    expiracaoSenha: String(data.expiracaoSenha ?? ""),
    tentativasLogin:
      data.tentativasLogin == null ? "0" : String(data.tentativasLogin),
    ultimoAcesso:
      data.ultimoAcesso == null ? undefined : String(data.ultimoAcesso),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(item: ControlRecord) {
  return {
    codigo: item.codigo.trim() || null,
    descricao: item.descricao.trim() || null,
    tipoControle: item.tipoControle.trim() || null,
    frequencia: item.frequencia.trim() || null,
    responsavel:
      item.responsavelId.trim() === "" ? null : Number(item.responsavelId),
    efetivo: item.efetivo,
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

export default function ControlsPage({ embedded = false }: ControlsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<ControlRecord[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<ControlRecord | null>(null);
  const [draft, setDraft] = useState<ControlRecord>(emptyControl);
  const canRead = canAccessResourceAction(session, controlsResource, "read");
  const canCreate = canAccessResourceAction(
    session,
    controlsResource,
    "create",
  );
  const canUpdate = canAccessResourceAction(
    session,
    controlsResource,
    "update",
  );
  const canDelete = canAccessResourceAction(
    session,
    controlsResource,
    "delete",
  );
  const canReadUsers = canAccessResourceAction(session, usersResource, "read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  const loadControls = useCallback(async () => {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyControl });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("grc", "controles");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeControl(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Não foi possível carregar os controles."));
    } finally {
      setLoading(false);
    }
  }, [canRead]);

  const loadUsers = useCallback(async () => {
    if (!canReadUsers) {
      setUsers([]);
      return;
    }

    try {
      const response = await listResource("sys", "usuarios");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeUser(item as Record<string, unknown>),
          )
        : [];
      setUsers(nextItems);
    } catch {
      setUsers([]);
    }
  }, [canReadUsers]);

  useEffect(() => {
    void loadControls();
  }, [loadControls]);

  useEffect(() => {
    void loadUsers();
  }, [loadUsers]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.codigo, item.descricao, item.tipoControle, item.frequencia]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyControl });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: ControlRecord) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: ControlRecord) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil não possui permissão para atualizar controles."
          : "Seu perfil não possui permissão para criar controles.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("grc", "controles", selected.id, payload)
        : await createResource("grc", "controles", payload);

      const saved = normalizeControl(response.data as Record<string, unknown>);
      await loadControls();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Controle atualizado com sucesso."
          : "Controle criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Não foi possível atualizar o controle."
            : "Não foi possível criar o controle.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: ControlRecord) {
    if (!canDelete) {
      setError("Seu perfil não possui permissão para excluir controles.");
      return;
    }

    if (!item.id) {
      setError("Não foi possível identificar o controle para exclusão.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o controle "${item.codigo || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("grc", "controles", item.id);
      await loadControls();
      setSuccess("Controle excluído com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Não foi possível excluir o controle."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded ? "controls-page controls-page--embedded" : "controls-page"
      }
    >
      {!embedded ? (
        <header className="controls-page__header">
          <div>
            <span className="controls-page__eyebrow">GRC</span>
            <h2 className="controls-page__title">Controles</h2>
            <p className="controls-page__subtitle">
              Formalize controles preventivos, detectivos e corretivos com
              frequência, responsável e status de efetividade.
            </p>
          </div>

          <div className="controls-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por codigo, descricao ou frequencia"
              className="controls-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="controls-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo controle
            </button>
          </div>
        </header>
      ) : (
        <div className="controls-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por codigo, descricao ou frequencia"
            className="controls-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="controls-page__toolbar-actions">
            <button
              type="button"
              className="controls-page__ghost"
              onClick={() => void loadControls()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="controls-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo controle
            </button>
          </div>
        </div>
      )}

      {error ? <div className="controls-page__alert">{error}</div> : null}
      {success ? (
        <div className="controls-page__alert controls-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="controls-page__alert controls-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="controls-page__layout">
        <ControlsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <ControlForm
          value={draft}
          editing={!!selected}
          users={users}
          canReadUsers={canReadUsers}
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
