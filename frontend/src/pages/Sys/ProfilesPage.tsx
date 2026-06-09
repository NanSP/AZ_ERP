import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import ProfileTable from "../../components/Sys/ProfileTable";
import ProfileForm from "../../components/Sys/ProfileForm";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./profiles-page.css";

export type Profile = {
  id?: number;
  nome: string;
  descricao: string;
  nivelAcesso: string;
  createdAt?: string;
};

type ProfilesPageProps = {
  embedded?: boolean;
};

const emptyProfile: Profile = {
  nome: "",
  descricao: "",
  nivelAcesso: "1",
};

function normalizeProfile(data: Record<string, unknown>): Profile {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    nome: String(data.nome ?? ""),
    descricao: String(data.descricao ?? ""),
    nivelAcesso: data.nivelAcesso == null ? "1" : String(data.nivelAcesso),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(profile: Profile) {
  return {
    nome: profile.nome.trim(),
    descricao: profile.descricao.trim() || null,
    nivelAcesso:
      profile.nivelAcesso.trim() === "" ? null : Number(profile.nivelAcesso),
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

export default function ProfilesPage({ embedded = false }: ProfilesPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<Profile[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<Profile | null>(null);
  const [draft, setDraft] = useState<Profile>(emptyProfile);
  const isBusy = loading || saving;
  const permissionSet = useMemo(
    () => new Set(session?.permissoes ?? []),
    [session?.permissoes],
  );
  const isMasterScope = session?.scope === "master";
  const canRead = isMasterScope || permissionSet.has("sys:perfis:read");
  const canCreate = isMasterScope || permissionSet.has("sys:perfis:create");
  const canUpdate = isMasterScope || permissionSet.has("sys:perfis:update");
  const canDelete = isMasterScope || permissionSet.has("sys:perfis:delete");
  const canSubmitCurrent = selected ? canUpdate : canCreate;

  const loadProfiles = useCallback(async () => {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyProfile });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("sys", "perfis");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeProfile(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Não foi possivel carregar os perfis."));
    } finally {
      setLoading(false);
    }
  }, [canRead]);

  useEffect(() => {
    void loadProfiles();
  }, [loadProfiles]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.nome, item.descricao, item.nivelAcesso]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyProfile });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: Profile) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: Profile) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil não possui permissão para atualizar perfis."
          : "Seu perfil não possui permissão para criar perfis.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("sys", "perfis", selected.id, payload)
        : await createResource("sys", "perfis", payload);

      const saved = normalizeProfile(response.data as Record<string, unknown>);
      await loadProfiles();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Perfil atualizado com sucesso."
          : "Perfil criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Não foi possivel atualizar o perfil."
            : "Não foi possivel criar o perfil.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: Profile) {
    if (!canDelete) {
      setError("Seu perfil não possui permissão para excluir perfis.");
      return;
    }

    if (!item.id) {
      setError("Não foi possivel identificar o perfil para exclusão.");
      return;
    }

    const confirmed = window.confirm(`Deseja excluir o perfil "${item.nome}"?`);

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("sys", "perfis", item.id);
      await loadProfiles();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyProfile });
      }

      setSuccess("Perfil excluído com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Não foi possivel excluir o perfil."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded ? "profiles-page profiles-page--embedded" : "profiles-page"
      }
    >
      {!embedded ? (
        <header className="profiles-page__header">
          <div>
            <span className="profiles-page__eyebrow">SYS</span>
            <h2 className="profiles-page__title">Perfis</h2>
            <p className="profiles-page__subtitle">
              Gerencie perfis de acesso, niveis operacionais e regras de
              atribuição.
            </p>
          </div>

          <div className="profiles-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por nome, descricao ou nivel"
              className="profiles-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="profiles-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo perfil
            </button>
          </div>
        </header>
      ) : (
        <div className="profiles-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por nome, descricao ou nivel"
            className="profiles-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="profiles-page__toolbar-actions">
            <button
              type="button"
              className="profiles-page__ghost"
              onClick={() => void loadProfiles()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="profiles-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo perfil
            </button>
          </div>
        </div>
      )}

      {error ? <div className="profiles-page__alert">{error}</div> : null}
      {success ? (
        <div className="profiles-page__alert profiles-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="profiles-page__alert profiles-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criação desabilitada",
            canDelete ? null : "exclusão desabilitada",
          ]
            .filter(Boolean)
            .join(" · ")}
        </div>
      ) : null}

      <div className="profiles-page__layout">
        <ProfileTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <ProfileForm
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
