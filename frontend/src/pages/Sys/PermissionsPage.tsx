import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import PermissionTable from "../../components/Sys/PermissionTable";
import PermissionForm from "../../components/Sys/PermissionForm";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./permissions-page.css";

export type Permission = {
  id?: number;
  nome: string;
  descricao: string;
  modulo: string;
  recurso: string;
  acao: string;
  createdAt?: string;
};

type PermissionsPageProps = {
  embedded?: boolean;
};

const emptyPermission: Permission = {
  nome: "",
  descricao: "",
  modulo: "core",
  recurso: "",
  acao: "read",
};

function normalizePermission(data: Record<string, unknown>): Permission {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    nome: String(data.nome ?? ""),
    descricao: String(data.descricao ?? ""),
    modulo: String(data.modulo ?? "core"),
    recurso: String(data.recurso ?? ""),
    acao: String(data.acao ?? "read"),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(permission: Permission) {
  return {
    nome: permission.nome.trim(),
    descricao: permission.descricao.trim() || null,
    modulo: permission.modulo.trim().toLowerCase(),
    recurso: permission.recurso.trim().toLowerCase(),
    acao: permission.acao.trim().toLowerCase(),
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

export default function PermissionsPage({
  embedded = false,
}: PermissionsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<Permission[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<Permission | null>(null);
  const [draft, setDraft] = useState<Permission>(emptyPermission);
  const isBusy = loading || saving;
  const permissionSet = useMemo(
    () => new Set(session?.permissoes ?? []),
    [session?.permissoes],
  );
  const isMasterScope = session?.scope === "master";
  const canRead = isMasterScope || permissionSet.has("sys:permissoes:read");
  const canCreate = isMasterScope || permissionSet.has("sys:permissoes:create");
  const canUpdate = isMasterScope || permissionSet.has("sys:permissoes:update");
  const canDelete = isMasterScope || permissionSet.has("sys:permissoes:delete");
  const canSubmitCurrent = selected ? canUpdate : canCreate;

  const loadPermissions = useCallback(async () => {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyPermission });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("sys", "permissoes");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizePermission(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Não foi possivel carregar as permissões."),
      );
    } finally {
      setLoading(false);
    }
  }, [canRead]);

  useEffect(() => {
    void loadPermissions();
  }, [loadPermissions]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.nome, item.descricao, item.modulo, item.recurso, item.acao]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyPermission });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: Permission) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: Permission) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil não possui permissão para atualizar permissões."
          : "Seu perfil não possui permissão para criar permissões.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("sys", "permissoes", selected.id, payload)
        : await createResource("sys", "permissoes", payload);

      const saved = normalizePermission(
        response.data as Record<string, unknown>,
      );
      await loadPermissions();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Permissão atualizada com sucesso."
          : "Permissão criada com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Não foi possivel atualizar a permissão."
            : "Não foi possivel criar a permissão.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: Permission) {
    if (!canDelete) {
      setError("Seu perfil não possui permissão para excluir permissões.");
      return;
    }

    if (!item.id) {
      setError("Não foi possivel identificar a permissão para exclusão.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir a permissão "${item.nome}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("sys", "permissoes", item.id);
      await loadPermissions();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyPermission });
      }

      setSuccess("Permissão excluída com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Não foi possivel excluir a permissão."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "permissions-page permissions-page--embedded"
          : "permissions-page"
      }
    >
      {!embedded ? (
        <header className="permissions-page__header">
          <div>
            <span className="permissions-page__eyebrow">SYS</span>
            <h2 className="permissions-page__title">Permissões</h2>
            <p className="permissions-page__subtitle">
              Gerencie a matriz de autorizações por módulo, recurso e ação.
            </p>
          </div>

          <div className="permissions-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por nome, modulo, recurso ou acao"
              className="permissions-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="permissions-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova permissão
            </button>
          </div>
        </header>
      ) : (
        <div className="permissions-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por nome, modulo, recurso ou acao"
            className="permissions-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="permissions-page__toolbar-actions">
            <button
              type="button"
              className="permissions-page__ghost"
              onClick={() => void loadPermissions()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="permissions-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova permissão
            </button>
          </div>
        </div>
      )}

      {error ? <div className="permissions-page__alert">{error}</div> : null}
      {success ? (
        <div className="permissions-page__alert permissions-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="permissions-page__alert permissions-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criação desabilitada",
            canDelete ? null : "exclusão desabilitada",
          ]
            .filter(Boolean)
            .join(" · ")}
        </div>
      ) : null}

      <div className="permissions-page__layout">
        <PermissionTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <PermissionForm
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
