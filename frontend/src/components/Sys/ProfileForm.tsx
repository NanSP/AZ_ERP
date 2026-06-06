import type { Profile } from "../../pages/Sys/ProfilesPage";

type ProfileFormProps = {
  value: Profile;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: Profile) => void;
  onSave: () => void;
  onReset: () => void;
};

export default function ProfileForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: ProfileFormProps) {
  const canSave = value.nome.trim() !== "" && value.nivelAcesso.trim() !== "";

  function update<K extends keyof Profile>(field: K, fieldValue: Profile[K]) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="profile-form">
      <div className="profile-form__head">
        <div>
          <h3 className="profile-form__title">
            {editing ? "Editar perfil" : "Novo perfil"}
          </h3>
          <p className="profile-form__subtitle">
            Configure o perfil, sua descricao funcional e o nivel de acesso.
          </p>
          {editing && value.id ? (
            <p className="profile-form__meta">Registro selecionado: #{value.id}</p>
          ) : !canEditFields ? (
            <p className="profile-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="profile-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="profile-form__grid">
        <label className="profile-form__field profile-form__field--span-2">
          <span>Nome</span>
          <input
            value={value.nome}
            onChange={(event) => update("nome", event.target.value)}
            placeholder="Ex.: ANALISTA_FINANCEIRO"
            disabled={!canEditFields}
          />
        </label>

        <label className="profile-form__field profile-form__field--span-2">
          <span>Descricao</span>
          <textarea
            value={value.descricao}
            onChange={(event) => update("descricao", event.target.value)}
            placeholder="Explique a finalidade operacional do perfil"
            maxLength={255}
            disabled={!canEditFields}
          />
        </label>

        <label className="profile-form__field">
          <span>Nivel de acesso</span>
          <input
            value={value.nivelAcesso}
            onChange={(event) =>
              update("nivelAcesso", event.target.value.replace(/\D/g, ""))
            }
            placeholder="1"
            disabled={!canEditFields}
          />
        </label>
      </div>

      <button
        type="button"
        className="profile-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar perfil"}
      </button>
    </aside>
  );
}
