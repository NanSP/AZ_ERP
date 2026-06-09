import type {
  ErrorLogEntry,
  UserAccess,
  UserOption,
} from "../../pages/Auditoria/ErrorLogsPage";

type ErrorLogsFormProps = {
  value: ErrorLogEntry;
  canCreate: boolean;
  saving: boolean;
  users: UserOption[];
  userAccess: UserAccess;
  onChange: (value: ErrorLogEntry) => void;
  onSave: () => void;
  onReset: () => void;
};

export default function ErrorLogsForm({
  value,
  canCreate,
  saving,
  users,
  userAccess,
  onChange,
  onSave,
  onReset,
}: ErrorLogsFormProps) {
  const canSave =
    value.erroMensagem.trim() !== "" && value.modulo.trim() !== "";

  function update<K extends keyof ErrorLogEntry>(
    field: K,
    fieldValue: ErrorLogEntry[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="error-logs-form">
      <div className="error-logs-page__panel-head">
        <div>
          <h3 className="error-logs-page__panel-title">Novo log de erro</h3>
          <p className="error-logs-page__panel-subtitle">
            Este recurso e imutável: o backend permite criar e consultar, mas
            nao alterar ou excluir.
          </p>
        </div>

        <button
          type="button"
          className="error-logs-page__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="error-logs-page__form-grid">
        <label className="error-logs-page__field">
          <span>Usuário</span>
          {users.length > 0 ? (
            <select
              value={value.usuario}
              onChange={(event) => update("usuario", event.target.value)}
              disabled={!canCreate}
            >
              <option value="">Não vincular</option>
              {users.map((option) => (
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
              placeholder="ID do usuário"
              disabled={!canCreate}
            />
          )}
          {userAccess === "unavailable" ? (
            <small className="error-logs-page__hint">
              Lista indisponível. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="error-logs-page__field">
          <span>Código do erro</span>
          <input
            value={value.erroCodigo}
            onChange={(event) =>
              update("erroCodigo", event.target.value.replace(/[^\d-]/g, ""))
            }
            placeholder="Ex.: 500"
            disabled={!canCreate}
          />
        </label>

        <label className="error-logs-page__field">
          <span>Módulo</span>
          <input
            value={value.modulo}
            onChange={(event) => update("modulo", event.target.value)}
            placeholder="Ex.: core, fi, rh"
            disabled={!canCreate}
          />
        </label>

        <label className="error-logs-page__field">
          <span>IP</span>
          <input
            value={value.ipAddress}
            onChange={(event) => update("ipAddress", event.target.value)}
            placeholder="Ex.: 127.0.0.1"
            disabled={!canCreate}
          />
        </label>

        <label className="error-logs-page__field error-logs-page__field--span-2">
          <span>URL</span>
          <input
            value={value.url}
            onChange={(event) => update("url", event.target.value)}
            placeholder="Ex.: /api/core/produtos"
            disabled={!canCreate}
          />
        </label>

        <label className="error-logs-page__field error-logs-page__field--span-2">
          <span>Mensagem do erro</span>
          <textarea
            value={value.erroMensagem}
            onChange={(event) => update("erroMensagem", event.target.value)}
            placeholder="Descreva o erro registrado"
            disabled={!canCreate}
          />
        </label>

        <label className="error-logs-page__field error-logs-page__field--span-2">
          <span>Parametros (JSON)</span>
          <textarea
            value={value.parametros}
            onChange={(event) => update("parametros", event.target.value)}
            placeholder='{"chave":"valor"}'
            disabled={!canCreate}
          />
        </label>
      </div>

      <button
        type="button"
        className="error-logs-page__button"
        onClick={onSave}
        disabled={saving || !canSave || !canCreate}
      >
        {saving ? "Salvando..." : "Criar log de erro"}
      </button>
    </aside>
  );
}
