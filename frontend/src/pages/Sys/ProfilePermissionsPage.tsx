import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import ProfilePermissionTable from "../../components/Sys/ProfilePermissionTable";
import ProfilePermissionForm from "../../components/Sys/ProfilePermissionForm";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./profile-permissions-page.css";

type RelatedOption = {
  id: number;
  label: string;
};

type RelatedAccess = "idle" | "loaded" | "unavailable";

export type ProfilePermissionAssignment = {
  id?: number;
  perfil: string;
  permissao: string;
};

type ProfilePermissionsPageProps = {
  embedded?: boolean;
};

const emptyAssignment: ProfilePermissionAssignment = {
  perfil: "",
  permissao: "",
};

function normalizeAssignment(
  data: Record<string, unknown>,
): ProfilePermissionAssignment {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    perfil: data.perfil == null ? "" : String(data.perfil),
    permissao: data.permissao == null ? "" : String(data.permissao),
  };
}

function toRequestPayload(assignment: ProfilePermissionAssignment) {
  return {
    perfil: assignment.perfil.trim() === "" ? null : Number(assignment.perfil),
    permissao:
      assignment.permissao.trim() === "" ? null : Number(assignment.permissao),
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

export default function ProfilePermissionsPage({
  embedded = false,
}: ProfilePermissionsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<ProfilePermissionAssignment[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] =
    useState<ProfilePermissionAssignment | null>(null);
  const [draft, setDraft] =
    useState<ProfilePermissionAssignment>(emptyAssignment);
  const [profileOptions, setProfileOptions] = useState<RelatedOption[]>([]);
  const [permissionOptions, setPermissionOptions] = useState<RelatedOption[]>([]);
  const [profileAccess, setProfileAccess] = useState<RelatedAccess>("idle");
  const [permissionAccess, setPermissionAccess] =
    useState<RelatedAccess>("idle");
  const isBusy = loading || saving;
  const permissionSet = useMemo(
    () => new Set(session?.permissoes ?? []),
    [session?.permissoes],
  );
  const isMasterScope = session?.scope === "master";
  const canRead =
    isMasterScope || permissionSet.has("sys:perfil_permissao:read");
  const canCreate =
    isMasterScope || permissionSet.has("sys:perfil_permissao:create");
  const canUpdate =
    isMasterScope || permissionSet.has("sys:perfil_permissao:update");
  const canDelete =
    isMasterScope || permissionSet.has("sys:perfil_permissao:delete");
  const canReadProfiles = isMasterScope || permissionSet.has("sys:perfis:read");
  const canReadPermissions =
    isMasterScope || permissionSet.has("sys:permissoes:read");
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
      const response = await listResource("sys", "perfilPermissao");
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
          "Nao foi possivel carregar os vinculos de perfil e permissao.",
        ),
      );
    } finally {
      setLoading(false);
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

  async function loadPermissions() {
    if (!canReadPermissions) {
      setPermissionOptions([]);
      setPermissionAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("sys", "permissoes");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) => item as Record<string, unknown>)
            .filter((item) => typeof item.id === "number")
            .map((item) => ({
              id: item.id as number,
              label: `${String(item.nome ?? "Permissao")} (#${String(item.id)})`,
            }))
        : [];
      setPermissionOptions(nextItems);
      setPermissionAccess("loaded");
    } catch {
      setPermissionOptions([]);
      setPermissionAccess("unavailable");
    }
  }

  useEffect(() => {
    void loadAssignments();
  }, [canRead]);

  useEffect(() => {
    void loadProfiles();
  }, [canReadProfiles]);

  useEffect(() => {
    void loadPermissions();
  }, [canReadPermissions]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.perfil, item.permissao]
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

  function handleSelect(item: ProfilePermissionAssignment) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: ProfilePermissionAssignment) {
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
        ? await updateResource("sys", "perfilPermissao", selected.id, payload)
        : await createResource("sys", "perfilPermissao", payload);

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

  async function handleDelete(item: ProfilePermissionAssignment) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir vinculos.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar o vinculo para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o vinculo do perfil #${item.perfil} com a permissao #${item.permissao}?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("sys", "perfilPermissao", item.id);
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
          ? "profile-permissions-page profile-permissions-page--embedded"
          : "profile-permissions-page"
      }
    >
      {!embedded ? (
        <header className="profile-permissions-page__header">
          <div>
            <span className="profile-permissions-page__eyebrow">SYS</span>
            <h2 className="profile-permissions-page__title">Perfil x Permissao</h2>
            <p className="profile-permissions-page__subtitle">
              Gerencie a atribuicao de permissoes aos perfis do tenant.
            </p>
          </div>

          <div className="profile-permissions-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por perfil ou permissao"
              className="profile-permissions-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="profile-permissions-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo vinculo
            </button>
          </div>
        </header>
      ) : (
        <div className="profile-permissions-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por perfil ou permissao"
            className="profile-permissions-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="profile-permissions-page__toolbar-actions">
            <button
              type="button"
              className="profile-permissions-page__ghost"
              onClick={() => void loadAssignments()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="profile-permissions-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo vinculo
            </button>
          </div>
        </div>
      )}

      {error ? (
        <div className="profile-permissions-page__alert">{error}</div>
      ) : null}
      {success ? (
        <div className="profile-permissions-page__alert profile-permissions-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="profile-permissions-page__alert profile-permissions-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" · ")}
        </div>
      ) : null}

      <div className="profile-permissions-page__layout">
        <ProfilePermissionTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          profileOptions={profileOptions}
          permissionOptions={permissionOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <ProfilePermissionForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          profileOptions={profileOptions}
          permissionOptions={permissionOptions}
          profileAccess={profileAccess}
          permissionAccess={permissionAccess}
          onChange={handleChange}
          onSave={() => void handleSave()}
          onReset={handleCreateNew}
        />
      </div>
    </div>
  );
}
