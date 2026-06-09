import type { ProfilePermissionAssignment } from "../../pages/Sys/ProfilePermissionsPage";

type RelatedOption = {
  id: number;
  label: string;
};

type RelatedAccess = "idle" | "loaded" | "unavailable";

type ProfilePermissionFormProps = {
  value: ProfilePermissionAssignment;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  profileOptions: RelatedOption[];
  permissionOptions: RelatedOption[];
  profileAccess: RelatedAccess;
  permissionAccess: RelatedAccess;
  onChange: (value: ProfilePermissionAssignment) => void;
  onSave: () => void;
  onReset: () => void;
};

export default function ProfilePermissionForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  profileOptions,
  permissionOptions,
  profileAccess,
  permissionAccess,
  onChange,
  onSave,
  onReset,
}: ProfilePermissionFormProps) {
  const canSave = value.perfil.trim() !== "" && value.permissao.trim() !== "";

  function update<K extends keyof ProfilePermissionAssignment>(
    field: K,
    fieldValue: ProfilePermissionAssignment[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="profile-permission-form">
      <div className="profile-permission-form__head">
        <div>
          <h3 className="profile-permission-form__title">
            {editing ? "Editar vinculo" : "Novo vinculo"}
          </h3>
          <p className="profile-permission-form__subtitle">
            Associe permissões existentes a perfis do tenant.
          </p>
          {editing && value.id ? (
            <p className="profile-permission-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="profile-permission-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="profile-permission-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="profile-permission-form__grid">
        <label className="profile-permission-form__field">
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
            <small className="profile-permission-form__hint">
              Lista de perfis indisponível. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="profile-permission-form__field">
          <span>Permissão</span>
          {permissionOptions.length > 0 ? (
            <select
              value={value.permissao}
              onChange={(event) => update("permissao", event.target.value)}
              disabled={!canEditFields || editing}
            >
              <option value="">Selecione uma permissão</option>
              {permissionOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.permissao}
              onChange={(event) =>
                update("permissao", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID da permissão"
              disabled={!canEditFields || editing}
            />
          )}
          {permissionAccess === "unavailable" ? (
            <small className="profile-permission-form__hint">
              Lista de permissões indisponível. Informe o ID manualmente.
            </small>
          ) : null}
        </label>
      </div>

      <button
        type="button"
        className="profile-permission-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alterações"
            : "Criar vinculo"}
      </button>
    </aside>
  );
}
