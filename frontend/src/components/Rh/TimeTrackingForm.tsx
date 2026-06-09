import type {
  EmployeeAccess,
  EmployeeOption,
  TimeTrackingEntry,
} from "../../pages/Rh/TimeTrackingPage";

type TimeTrackingFormProps = {
  value: TimeTrackingEntry;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  employeeOptions: EmployeeOption[];
  employeeAccess: EmployeeAccess;
  onChange: (value: TimeTrackingEntry) => void;
  onSave: () => void;
  onReset: () => void;
};

export default function TimeTrackingForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  employeeOptions,
  employeeAccess,
  onChange,
  onSave,
  onReset,
}: TimeTrackingFormProps) {
  const canSave =
    value.colaborador.trim() !== "" &&
    value.data.trim() !== "" &&
    value.horaEntrada.trim() !== "";

  function update<K extends keyof TimeTrackingEntry>(
    field: K,
    fieldValue: TimeTrackingEntry[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="time-tracking-form">
      <div className="time-tracking-form__head">
        <div>
          <h3 className="time-tracking-form__title">
            {editing ? "Editar ponto" : "Novo ponto"}
          </h3>
          <p className="time-tracking-form__subtitle">
            Registre a jornada do colaborador e acompanhe horas e atrasos
            calculados.
          </p>
          {editing && value.id ? (
            <p className="time-tracking-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="time-tracking-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="time-tracking-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="time-tracking-form__grid">
        <label className="time-tracking-form__field">
          <span>Colaborador</span>
          {employeeOptions.length > 0 ? (
            <select
              value={value.colaborador}
              onChange={(event) => update("colaborador", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione um colaborador</option>
              {employeeOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.colaborador}
              onChange={(event) =>
                update("colaborador", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do colaborador"
              disabled={!canEditFields}
            />
          )}
          {employeeAccess === "unavailable" ? (
            <small className="time-tracking-form__hint">
              Lista indisponível. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="time-tracking-form__field">
          <span>Data</span>
          <input
            type="date"
            value={value.data}
            onChange={(event) => update("data", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="time-tracking-form__field">
          <span>Hora de entrada</span>
          <input
            type="time"
            value={value.horaEntrada}
            onChange={(event) => update("horaEntrada", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="time-tracking-form__field">
          <span>Saída para almoço</span>
          <input
            type="time"
            value={value.horaSaidaAlmoco}
            onChange={(event) => update("horaSaidaAlmoco", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="time-tracking-form__field">
          <span>Retorno do almoço</span>
          <input
            type="time"
            value={value.horaRetornoAlmoco}
            onChange={(event) =>
              update("horaRetornoAlmoco", event.target.value)
            }
            disabled={!canEditFields}
          />
        </label>

        <label className="time-tracking-form__field">
          <span>Hora de saída</span>
          <input
            type="time"
            value={value.horaSaida}
            onChange={(event) => update("horaSaida", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <div className="time-tracking-form__summary">
          <span>Horas trabalhadas: {value.horasTrabalhadas || "0"}</span>
          <span>Horas extras: {value.horasExtras || "0"}</span>
          <span>Atrasos: {value.atrasos ?? 0} min</span>
        </div>
      </div>

      <button
        type="button"
        className="time-tracking-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving ? "Salvando..." : editing ? "Salvar alterações" : "Criar ponto"}
      </button>
    </aside>
  );
}
