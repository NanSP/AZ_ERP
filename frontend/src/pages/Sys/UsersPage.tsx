import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import UserTable from "../../components/Sys/UserTable";
import UserForm from "../../components/Sys/UserForm";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./users-page.css";

export type User = {
  id?: number;
  nome: string;
  email: string;
  login: string;
  senha: string;
  documento: string;
  tipoUsuario: string;
  status: string;
  expiracaoSenha: string;
  tentativasLogin: string;
  ultimoAcesso?: string;
  createdAt?: string;
};

type UsersPageProps = {
  embedded?: boolean;
};

const emptyUser: User = {
  nome: "",
  email: "",
  login: "",
  senha: "",
  documento: "",
  tipoUsuario: "operador",
  status: "ativo",
  expiracaoSenha: "",
  tentativasLogin: "0",
};

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
    ultimoAcesso: data.ultimoAcesso == null ? undefined : String(data.ultimoAcesso),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(user: User, editing: boolean) {
  return {
    nome: user.nome.trim(),
    email: user.email.trim(),
    login: user.login.trim(),
    senha: user.senha.trim() === "" && editing ? null : user.senha.trim(),
    documento: user.documento.trim() || null,
    tipoUsuario: user.tipoUsuario.trim() || null,
    status: user.status.trim() || null,
    ultimoAcesso: null,
    expiracaoSenha: user.expiracaoSenha.trim() || null,
    tentativasLogin:
      user.tentativasLogin.trim() === "" ? null : Number(user.tentativasLogin),
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

export default function UsersPage({ embedded = false }: UsersPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<User | null>(null);
  const [draft, setDraft] = useState<User>(emptyUser);
  const isBusy = loading || saving;
  const permissionSet = useMemo(
    () => new Set(session?.permissoes ?? []),
    [session?.permissoes],
  );
  const isMasterScope = session?.scope === "master";
  const canCreate = isMasterScope || permissionSet.has("sys:usuarios:create");
  const canUpdate = isMasterScope || permissionSet.has("sys:usuarios:update");
  const canDelete = isMasterScope || permissionSet.has("sys:usuarios:delete");
  const canSubmitCurrent = selected ? canUpdate : canCreate;

  async function loadUsers() {
    setLoading(true);
    setError(null);

    try {
      const response = await listResource("sys", "usuarios");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) => normalizeUser(item as Record<string, unknown>))
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel carregar os usuarios."));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadUsers();
  }, []);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.nome, item.email, item.login, item.documento, item.tipoUsuario]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyUser });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: User) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item, senha: "" });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: User) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar usuarios."
          : "Seu perfil nao possui permissao para criar usuarios.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft, !!selected);
      const response = selected?.id
        ? await updateResource("sys", "usuarios", selected.id, payload)
        : await createResource("sys", "usuarios", payload);

      const saved = normalizeUser(response.data as Record<string, unknown>);
      await loadUsers();
      setSelected(saved);
      setDraft({ ...saved, senha: "" });
      setSuccess(
        selected?.id
          ? "Usuario atualizado com sucesso."
          : "Usuario criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o usuario."
            : "Nao foi possivel criar o usuario.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: User) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir usuarios.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar o usuario para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o usuario "${item.nome}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("sys", "usuarios", item.id);
      await loadUsers();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyUser });
      }

      setSuccess("Usuario excluido com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel excluir o usuario."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className={embedded ? "users-page users-page--embedded" : "users-page"}>
      {!embedded ? (
        <header className="users-page__header">
          <div>
            <span className="users-page__eyebrow">SYS</span>
            <h2 className="users-page__title">Usuarios</h2>
            <p className="users-page__subtitle">
              Gerencie acessos operacionais, status de conta e dados de autenticacao.
            </p>
          </div>

          <div className="users-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por nome, email, login, documento ou tipo"
              className="users-page__search"
              disabled={isBusy}
            />
            <button
              type="button"
              className="users-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate}
            >
              Novo usuario
            </button>
          </div>
        </header>
      ) : (
        <div className="users-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por nome, email, login, documento ou tipo"
            className="users-page__search"
            disabled={isBusy}
          />
          <div className="users-page__toolbar-actions">
            <button
              type="button"
              className="users-page__ghost"
              onClick={() => void loadUsers()}
              disabled={isBusy}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="users-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate}
            >
              Novo usuario
            </button>
          </div>
        </div>
      )}

      {error ? <div className="users-page__alert">{error}</div> : null}
      {success ? (
        <div className="users-page__alert users-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canCreate || !canDelete ? (
        <div className="users-page__alert users-page__alert--info">
          {[
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" · ")}
        </div>
      ) : null}

      <div className="users-page__layout">
        <UserTable
          items={filteredItems}
          loading={loading}
          selectedId={selected?.id}
          canEdit={canUpdate}
          canDelete={canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <UserForm
          value={draft}
          editing={!!selected}
          canEditFields={selected ? canUpdate : canCreate}
          canSubmit={canSubmitCurrent}
          saving={saving}
          onChange={handleChange}
          onSave={() => void handleSave()}
          onReset={handleCreateNew}
        />
      </div>
    </div>
  );
}
