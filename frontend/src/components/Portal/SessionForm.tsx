import type {
  SessionEntry,
  UserAccess,
  UserOption,
} from "../../pages/Portal/SessionsPage";

type SessionFormProps = {
  value: SessionEntry;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  userOptions: UserOption[];
  userAccess: UserAccess;
  onChange: (value: SessionEntry) => void;
  onSave: () => void;
  onReset: () => void;
};

export default function SessionForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  userOptions,
  userAccess,
  onChange,
  onSave,
  onReset,
}: SessionFormProps) {
  const canSave =
    value.usuario.trim() !== "" &&
    value.tokenSessao.trim() !== "" &&
    value.ipAddress.trim() !== "" &&
    value.userAgent.trim() !== "" &&
    value.expiracao.trim() !== "";

  function update<K extends keyof SessionEntry>(
    field: K,
    fieldValue: SessionEntry[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="session-form">
      <div className="session-form__head">
        <div>
          <h3 className="session-form__title">
            {editing ? "Editar sessao" : "Nova sessao"}
          </h3>
          <p className="session-form__subtitle">
            Cadastre o contexto técnico e o ciclo de vida da sessao.
          </p>
          {editing && value.id ? (
            <p className="session-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="session-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="session-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="session-form__grid">
        <label className="session-form__field">
          <span>Usuário</span>
          {userOptions.length > 0 ? (
            <select
              value={value.usuario}
              onChange={(event) => update("usuario", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione</option>
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
              disabled={!canEditFields}
            />
          )}
          {userAccess === "unavailable" ? (
            <small className="session-form__hint">
              Lista indisponível. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="session-form__field">
          <span>Token da sessão</span>
          <input
            value={value.tokenSessao}
            onChange={(event) => update("tokenSessao", event.target.value)}
            placeholder="token-sessao"
            disabled={!canEditFields}
          />
        </label>

        <label className="session-form__field">
          <span>IP</span>
          <input
            value={value.ipAddress}
            onChange={(event) => update("ipAddress", event.target.value)}
            placeholder="127.0.0.1"
            disabled={!canEditFields}
          />
        </label>

        <label className="session-form__field">
          <span>User-Agent</span>
          <input
            value={value.userAgent}
            onChange={(event) => update("userAgent", event.target.value)}
            placeholder="Mozilla/5.0..."
            disabled={!canEditFields}
          />
        </label>

        <label className="session-form__field">
          <span>Data de login</span>
          <input
            type="datetime-local"
            value={value.dataLogin}
            onChange={(event) => update("dataLogin", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="session-form__field">
          <span>Data de logout</span>
          <input
            type="datetime-local"
            value={value.dataLogout}
            onChange={(event) => update("dataLogout", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="session-form__field session-form__field--span-2">
          <span>Expiração</span>
          <input
            type="datetime-local"
            value={value.expiracao}
            onChange={(event) => update("expiracao", event.target.value)}
            disabled={!canEditFields}
          />
        </label>
      </div>

      <button
        type="button"
        className="session-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alterações"
            : "Criar sessão"}
      </button>
    </aside>
  );
}
