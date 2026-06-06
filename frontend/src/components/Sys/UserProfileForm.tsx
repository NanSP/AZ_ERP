import type {
  UserProfileAssignment,
} from "../../pages/Sys/UserProfilesPage";

type RelatedOption = {
  id: number;
  label: string;
};

type RelatedAccess = "idle" | "loaded" | "unavailable";

type UserProfileFormProps = {
  value: UserProfileAssignment;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  userOptions: RelatedOption[];
  profileOptions: RelatedOption[];
  userAccess: RelatedAccess;
  profileAccess: RelatedAccess;
  onChange: (value: UserProfileAssignment) => void;
  onSave: () => void;
  onReset: () => void;
};

export default function UserProfileForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  userOptions,
  profileOptions,
  userAccess,
  profileAccess,
  onChange,
  onSave,
  onReset,
}: UserProfileFormProps) {
  const canSave = value.usuario.trim() !== "" && value.perfil.trim() !== "";

  function update<K extends keyof UserProfileAssignment>(
    field: K,
    fieldValue: UserProfileAssignment[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="user-profile-form">
      <div className="user-profile-form__head">
        <div>
          <h3 className="user-profile-form__title">
            {editing ? "Editar vinculo" : "Novo vinculo"}
          </h3>
          <p className="user-profile-form__subtitle">
            Associe um perfil existente a um usuario ativo do tenant.
          </p>
          {editing && value.id ? (
            <p className="user-profile-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="user-profile-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="user-profile-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="user-profile-form__grid">
        <label className="user-profile-form__field">
          <span>Usuario</span>
          {userOptions.length > 0 ? (
            <select
              value={value.usuario}
              onChange={(event) => update("usuario", event.target.value)}
              disabled={!canEditFields || editing}
            >
              <option value="">Selecione um usuario</option>
              {userOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.usuario}
              onChange={(event) =>
                update("usuario", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do usuario"
              disabled={!canEditFields || editing}
            />
          )}
          {userAccess === "unavailable" ? (
            <small className="user-profile-form__hint">
              Lista de usuarios indisponivel. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="user-profile-form__field">
          <span>Perfil</span>
          {profileOptions.length > 0 ? (
            <select
              value={value.perfil}
              onChange={(event) => update("perfil", event.target.value)}
              disabled={!canEditFields || editing}
            >
              <option value="">Selecione um perfil</option>
              {profileOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.perfil}
              onChange={(event) =>
                update("perfil", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do perfil"
              disabled={!canEditFields || editing}
            />
          )}
          {profileAccess === "unavailable" ? (
            <small className="user-profile-form__hint">
              Lista de perfis indisponivel. Informe o ID manualmente.
            </small>
          ) : null}
        </label>
      </div>

      <button
        type="button"
        className="user-profile-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar vinculo"}
      </button>
    </aside>
  );
}
