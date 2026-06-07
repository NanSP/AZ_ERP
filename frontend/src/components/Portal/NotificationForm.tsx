import type {
  Notification,
  UserAccess,
  UserOption,
} from "../../pages/Portal/NotificationsPage";

type NotificationFormProps = {
  value: Notification;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  canSaveReadState: boolean;
  canToggleReadState: boolean;
  readStateHint: string | null;
  saving: boolean;
  userOptions: UserOption[];
  userAccess: UserAccess;
  onChange: (value: Notification) => void;
  onSave: () => void;
  onReset: () => void;
};

const typeOptions = [
  { value: "info", label: "Info" },
  { value: "sucesso", label: "Sucesso" },
  { value: "alerta", label: "Alerta" },
  { value: "erro", label: "Erro" },
];

export default function NotificationForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  canSaveReadState,
  canToggleReadState,
  readStateHint,
  saving,
  userOptions,
  userAccess,
  onChange,
  onSave,
  onReset,
}: NotificationFormProps) {
  const canSave =
    value.usuario.trim() !== "" &&
    value.titulo.trim() !== "" &&
    value.mensagem.trim() !== "" &&
    canSaveReadState;

  function update<K extends keyof Notification>(
    field: K,
    fieldValue: Notification[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="notification-form">
      <div className="notification-form__head">
        <div>
          <h3 className="notification-form__title">
            {editing ? "Editar notificacao" : "Nova notificacao"}
          </h3>
          <p className="notification-form__subtitle">
            Cadastre a mensagem com usuario, tipo e estado de leitura.
          </p>
          {editing && value.id ? (
            <p className="notification-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="notification-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="notification-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="notification-form__grid">
        <label className="notification-form__field">
          <span>Usuario</span>
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
            <small className="notification-form__hint">
              Lista indisponivel. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="notification-form__field">
          <span>Tipo</span>
          <select
            value={value.tipo}
            onChange={(event) => update("tipo", event.target.value)}
            disabled={!canEditFields}
          >
            {typeOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="notification-form__field notification-form__field--span-2">
          <span>Titulo</span>
          <input
            value={value.titulo}
            onChange={(event) => update("titulo", event.target.value)}
            placeholder="Titulo da notificacao"
            disabled={!canEditFields}
          />
        </label>

        <label className="notification-form__field notification-form__field--span-2">
          <span>Mensagem</span>
          <input
            value={value.mensagem}
            onChange={(event) => update("mensagem", event.target.value)}
            placeholder="Conteudo da notificacao"
            disabled={!canEditFields}
          />
        </label>

        <label className="notification-form__checkbox">
          <input
            type="checkbox"
            checked={value.lida}
            onChange={(event) => {
              const lida = event.target.checked;
              onChange({
                ...value,
                lida,
                dataLeitura: lida ? value.dataLeitura : "",
              });
            }}
            disabled={!canEditFields || (!value.lida && !canToggleReadState)}
          />
          <span>Notificacao lida</span>
        </label>

        <label className="notification-form__field">
          <span>Data de leitura</span>
          <input
            type="datetime-local"
            value={value.dataLeitura}
            onChange={(event) => update("dataLeitura", event.target.value)}
            disabled={!canEditFields || !value.lida}
          />
          {readStateHint ? (
            <small className="notification-form__hint">{readStateHint}</small>
          ) : null}
        </label>
      </div>

      <button
        type="button"
        className="notification-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar notificacao"}
      </button>
    </aside>
  );
}
