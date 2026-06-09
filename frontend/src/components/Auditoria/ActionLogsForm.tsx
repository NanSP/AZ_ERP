import type {
  ActionLogEntry,
  UserAccess,
  UserOption,
} from "../../pages/Auditoria/ActionLogsPage";

type ActionLogsFormProps = {
  value: ActionLogEntry;
  canCreate: boolean;
  saving: boolean;
  users: UserOption[];
  userAccess: UserAccess;
  onChange: (value: ActionLogEntry) => void;
  onSave: () => void;
  onReset: () => void;
};

const actionOptions = [
  { value: "", label: "Selecione" },
  { value: "insert", label: "Insert" },
  { value: "update", label: "Update" },
  { value: "delete", label: "Delete" },
  { value: "login", label: "Login" },
  { value: "logout", label: "Logout" },
];

function isSessionAction(action: string) {
  return action === "login" || action === "logout";
}

export default function ActionLogsForm({
  value,
  canCreate,
  saving,
  users,
  userAccess,
  onChange,
  onSave,
  onReset,
}: ActionLogsFormProps) {
  const sessionAction = isSessionAction(value.acao);
  const canSave =
    value.modulo.trim() !== "" &&
    value.acao.trim() !== "" &&
    value.tabela.trim() !== "" &&
    (sessionAction || value.registroId.trim() !== "");

  function update<K extends keyof ActionLogEntry>(
    field: K,
    fieldValue: ActionLogEntry[K],
  ) {
    const next = {
      ...value,
      [field]: fieldValue,
    };

    if (field === "acao" && typeof fieldValue === "string") {
      if (isSessionAction(fieldValue)) {
        next.registroId = "";
      }
    }

    onChange(next);
  }

  return (
    <aside className="action-logs-form">
      <div className="action-logs-page__panel-head">
        <div>
          <h3 className="action-logs-page__panel-title">Novo log de ação</h3>
          <p className="action-logs-page__panel-subtitle">
            Este recurso e imutável: o backend permite criar e consultar, mas
            nao alterar ou excluir.
          </p>
        </div>

        <button
          type="button"
          className="action-logs-page__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="action-logs-page__form-grid">
        <label className="action-logs-page__field">
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
            <small className="action-logs-page__hint">
              Lista indisponível. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="action-logs-page__field">
          <span>Ação</span>
          <select
            value={value.acao}
            onChange={(event) => update("acao", event.target.value)}
            disabled={!canCreate}
          >
            {actionOptions.map((option) => (
              <option key={option.value || "empty"} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="action-logs-page__field">
          <span>Módulo</span>
          <input
            value={value.modulo}
            onChange={(event) => update("modulo", event.target.value)}
            placeholder="Ex.: core, fi, rh"
            disabled={!canCreate}
          />
        </label>

        <label className="action-logs-page__field">
          <span>Tabela</span>
          <input
            value={value.tabela}
            onChange={(event) => update("tabela", event.target.value)}
            placeholder="Ex.: produtos, usuarios, pedidos"
            disabled={!canCreate}
          />
        </label>

        <label className="action-logs-page__field">
          <span>Registro ID</span>
          <input
            value={value.registroId}
            onChange={(event) =>
              update("registroId", event.target.value.replace(/\D/g, ""))
            }
            placeholder={sessionAction ? "Nao se aplica" : "ID do registro"}
            disabled={!canCreate || sessionAction}
          />
        </label>

        <label className="action-logs-page__field">
          <span>IP</span>
          <input
            value={value.ipAddress}
            onChange={(event) => update("ipAddress", event.target.value)}
            placeholder="Ex.: 127.0.0.1"
            disabled={!canCreate}
          />
        </label>

        <label className="action-logs-page__field action-logs-page__field--span-2">
          <span>User agent</span>
          <input
            value={value.userAgent}
            onChange={(event) => update("userAgent", event.target.value)}
            placeholder="Ex.: AZ ERP Frontend"
            disabled={!canCreate}
          />
        </label>

        <label className="action-logs-page__field action-logs-page__field--span-2">
          <span>Dados antigos (JSON)</span>
          <textarea
            value={value.dadosAntigos}
            onChange={(event) => update("dadosAntigos", event.target.value)}
            placeholder='{"campo":"valor anterior"}'
            disabled={!canCreate}
          />
        </label>

        <label className="action-logs-page__field action-logs-page__field--span-2">
          <span>Dados novos (JSON)</span>
          <textarea
            value={value.dadosNovos}
            onChange={(event) => update("dadosNovos", event.target.value)}
            placeholder='{"campo":"valor novo"}'
            disabled={!canCreate}
          />
        </label>
      </div>

      <button
        type="button"
        className="action-logs-page__button"
        onClick={onSave}
        disabled={saving || !canSave || !canCreate}
      >
        {saving ? "Salvando..." : "Criar log de acao"}
      </button>
    </aside>
  );
}
