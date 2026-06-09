import type {
  Device,
  UserAccess,
  UserOption,
} from "../../pages/Portal/DevicesPage";

type DeviceFormProps = {
  value: Device;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  userOptions: UserOption[];
  userAccess: UserAccess;
  onChange: (value: Device) => void;
  onSave: () => void;
  onReset: () => void;
};

const platformOptions = [
  { value: "web", label: "Web" },
  { value: "android", label: "Android" },
  { value: "ios", label: "iOS" },
];

export default function DeviceForm({
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
}: DeviceFormProps) {
  const canSave =
    value.usuario.trim() !== "" &&
    value.deviceId.trim() !== "" &&
    value.devicePlatform.trim() !== "";

  function update<K extends keyof Device>(field: K, fieldValue: Device[K]) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="device-form">
      <div className="device-form__head">
        <div>
          <h3 className="device-form__title">
            {editing ? "Editar dispositivo" : "Novo dispositivo"}
          </h3>
          <p className="device-form__subtitle">
            Cadastre o contexto tecnico do dispositivo e o estado de atividade.
          </p>
          {editing && value.id ? (
            <p className="device-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="device-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="device-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="device-form__grid">
        <label className="device-form__field">
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
            <small className="device-form__hint">
              Lista indisponível. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="device-form__field">
          <span>Device ID</span>
          <input
            value={value.deviceId}
            onChange={(event) => update("deviceId", event.target.value)}
            placeholder="device-id"
            disabled={!canEditFields}
          />
        </label>

        <label className="device-form__field">
          <span>Modelo</span>
          <input
            value={value.deviceModel}
            onChange={(event) => update("deviceModel", event.target.value)}
            placeholder="Modelo do dispositivo"
            disabled={!canEditFields}
          />
        </label>

        <label className="device-form__field">
          <span>Plataforma</span>
          <select
            value={value.devicePlatform}
            onChange={(event) => update("devicePlatform", event.target.value)}
            disabled={!canEditFields}
          >
            {platformOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="device-form__field device-form__field--span-2">
          <span>Push token</span>
          <input
            value={value.pushToken}
            onChange={(event) => update("pushToken", event.target.value)}
            placeholder="push-token"
            disabled={!canEditFields}
          />
        </label>

        <label className="device-form__field">
          <span>Último acesso</span>
          <input
            type="datetime-local"
            value={value.ultimoAcesso}
            onChange={(event) => update("ultimoAcesso", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="device-form__checkbox">
          <input
            type="checkbox"
            checked={value.ativo}
            onChange={(event) => update("ativo", event.target.checked)}
            disabled={!canEditFields}
          />
          <span>Dispositivo ativo</span>
        </label>
      </div>

      <button
        type="button"
        className="device-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alterações"
            : "Criar dispositivo"}
      </button>
    </aside>
  );
}
