import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import UserProfileTable from "../../components/Sys/UserProfileTable";
import UserProfileForm from "../../components/Sys/UserProfileForm";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./user-profiles-page.css";

type RelatedOption = {
  id: number;
  label: string;
};

type RelatedAccess = "idle" | "loaded" | "unavailable";

export type UserProfileAssignment = {
  id?: number;
  usuario: string;
  perfil: string;
  dataAtribuicao?: string;
};

type UserProfilesPageProps = {
  embedded?: boolean;
};

const emptyAssignment: UserProfileAssignment = {
  usuario: "",
  perfil: "",
};

function normalizeAssignment(
  data: Record<string, unknown>,
): UserProfileAssignment {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    usuario: data.usuario == null ? "" : String(data.usuario),
    perfil: data.perfil == null ? "" : String(data.perfil),
    dataAtribuicao:
      data.dataAtribuicao == null ? undefined : String(data.dataAtribuicao),
  };
}

function toRequestPayload(assignment: UserProfileAssignment) {
  return {
    usuario:
      assignment.usuario.trim() === "" ? null : Number(assignment.usuario),
    perfil: assignment.perfil.trim() === "" ? null : Number(assignment.perfil),
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

export default function UserProfilesPage({
  embedded = false,
}: UserProfilesPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<UserProfileAssignment[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<UserProfileAssignment | null>(null);
  const [draft, setDraft] = useState<UserProfileAssignment>(emptyAssignment);
  const [userOptions, setUserOptions] = useState<RelatedOption[]>([]);
  const [profileOptions, setProfileOptions] = useState<RelatedOption[]>([]);
  const [userAccess, setUserAccess] = useState<RelatedAccess>("idle");
  const [profileAccess, setProfileAccess] = useState<RelatedAccess>("idle");
  const isBusy = loading || saving;
  const permissionSet = useMemo(
    () => new Set(session?.permissoes ?? []),
    [session?.permissoes],
  );
  const isMasterScope = session?.scope === "master";
  const canRead =
    isMasterScope || permissionSet.has("sys:usuario_perfil:read");
  const canCreate =
    isMasterScope || permissionSet.has("sys:usuario_perfil:create");
  const canUpdate =
    isMasterScope || permissionSet.has("sys:usuario_perfil:update");
  const canDelete =
    isMasterScope || permissionSet.has("sys:usuario_perfil:delete");
  const canReadUsers = isMasterScope || permissionSet.has("sys:usuarios:read");
  const canReadProfiles =
    isMasterScope || permissionSet.has("sys:perfis:read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;

  async function loadAssignments() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyAssignment });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("sys", "usuarioPerfil");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeAssignment(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          "Nao foi possivel carregar os vinculos de usuario e perfil.",
        ),
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
            .map((item) => item as Record<string, unknown>)
            .filter((item) => typeof item.id === "number")
            .map((item) => ({
              id: item.id as number,
              label: `${String(item.nome ?? "Usuario")} (#${String(item.id)})`,
            }))
        : [];
      setUserOptions(nextItems);
      setUserAccess("loaded");
    } catch {
      setUserOptions([]);
      setUserAccess("unavailable");
    }
  }

  async function loadProfiles() {
    if (!canReadProfiles) {
      setProfileOptions([]);
      setProfileAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("sys", "perfis");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) => item as Record<string, unknown>)
            .filter((item) => typeof item.id === "number")
            .map((item) => ({
              id: item.id as number,
              label: `${String(item.nome ?? "Perfil")} (#${String(item.id)})`,
            }))
        : [];
      setProfileOptions(nextItems);
      setProfileAccess("loaded");
    } catch {
      setProfileOptions([]);
      setProfileAccess("unavailable");
    }
  }

  useEffect(() => {
    void loadAssignments();
  }, [canRead]);

  useEffect(() => {
    void loadUsers();
  }, [canReadUsers]);

  useEffect(() => {
    void loadProfiles();
  }, [canReadProfiles]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.usuario, item.perfil, item.dataAtribuicao]
        .filter(Boolean)
        .some((value) => String(value).toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyAssignment });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: UserProfileAssignment) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: UserProfileAssignment) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar vinculos."
          : "Seu perfil nao possui permissao para criar vinculos.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("sys", "usuarioPerfil", selected.id, payload)
        : await createResource("sys", "usuarioPerfil", payload);

      const saved = normalizeAssignment(response.data as Record<string, unknown>);
      await loadAssignments();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Vinculo atualizado com sucesso."
          : "Vinculo criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o vinculo."
            : "Nao foi possivel criar o vinculo.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: UserProfileAssignment) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir vinculos.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar o vinculo para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o vinculo do usuario #${item.usuario} com o perfil #${item.perfil}?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("sys", "usuarioPerfil", item.id);
      await loadAssignments();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyAssignment });
      }

      setSuccess("Vinculo excluido com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel excluir o vinculo."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "user-profiles-page user-profiles-page--embedded"
          : "user-profiles-page"
      }
    >
      {!embedded ? (
        <header className="user-profiles-page__header">
          <div>
            <span className="user-profiles-page__eyebrow">SYS</span>
            <h2 className="user-profiles-page__title">Usuario x Perfil</h2>
            <p className="user-profiles-page__subtitle">
              Gerencie atribuicoes de perfis aos usuarios do tenant.
            </p>
          </div>

          <div className="user-profiles-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por usuario, perfil ou data"
              className="user-profiles-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="user-profiles-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo vinculo
            </button>
          </div>
        </header>
      ) : (
        <div className="user-profiles-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por usuario, perfil ou data"
            className="user-profiles-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="user-profiles-page__toolbar-actions">
            <button
              type="button"
              className="user-profiles-page__ghost"
              onClick={() => void loadAssignments()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="user-profiles-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo vinculo
            </button>
          </div>
        </div>
      )}

      {error ? <div className="user-profiles-page__alert">{error}</div> : null}
      {success ? (
        <div className="user-profiles-page__alert user-profiles-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="user-profiles-page__alert user-profiles-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" · ")}
        </div>
      ) : null}

      <div className="user-profiles-page__layout">
        <UserProfileTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          userOptions={userOptions}
          profileOptions={profileOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <UserProfileForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          userOptions={userOptions}
          profileOptions={profileOptions}
          userAccess={userAccess}
          profileAccess={profileAccess}
          onChange={handleChange}
          onSave={() => void handleSave()}
          onReset={handleCreateNew}
        />
      </div>
    </div>
  );
}
