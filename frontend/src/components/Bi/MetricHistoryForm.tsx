import type {
  MetricAccess,
  MetricHistoryRecord,
  MetricOption,
} from "../../pages/Bi/MetricHistoryPage";

type MetricHistoryFormProps = {
  value: MetricHistoryRecord;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  metricOptions: MetricOption[];
  metricAccess: MetricAccess;
  onChange: (value: MetricHistoryRecord) => void;
  onSave: () => void;
  onReset: () => void;
};

export default function MetricHistoryForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  metricOptions,
  metricAccess,
  onChange,
  onSave,
  onReset,
}: MetricHistoryFormProps) {
  const valueNumber =
    value.valorApurado.trim() === ""
      ? NaN
      : Number(value.valorApurado.replace(",", "."));
  const canSave =
    value.metrica.trim() !== "" &&
    value.periodo.trim() !== "" &&
    !Number.isNaN(valueNumber) &&
    valueNumber >= 0;

  function update<K extends keyof MetricHistoryRecord>(
    field: K,
    fieldValue: MetricHistoryRecord[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="metric-history-form">
      <div className="metric-history-form__head">
        <div>
          <h3 className="metric-history-form__title">
            {editing ? "Editar historico" : "Novo historico"}
          </h3>
          <p className="metric-history-form__subtitle">
            Informe a metrica, o periodo e o valor apurado do indicador.
          </p>
          {editing && value.id ? (
            <p className="metric-history-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="metric-history-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="metric-history-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="metric-history-form__grid">
        <label className="metric-history-form__field">
          <span>Metrica</span>
          {metricOptions.length > 0 ? (
            <select
              value={value.metrica}
              onChange={(event) => update("metrica", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione</option>
              {metricOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.metrica}
              onChange={(event) =>
                update("metrica", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID da metrica"
              disabled={!canEditFields}
            />
          )}
          {metricAccess === "unavailable" ? (
            <small className="metric-history-form__hint">
              Lista indisponivel. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="metric-history-form__field">
          <span>Periodo</span>
          <input
            type="date"
            value={value.periodo}
            onChange={(event) => update("periodo", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="metric-history-form__field metric-history-form__field--span-2">
          <span>Valor apurado</span>
          <input
            value={value.valorApurado}
            onChange={(event) =>
              update(
                "valorApurado",
                event.target.value.replace(/[^0-9,.-]/g, ""),
              )
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>
      </div>

      <button
        type="button"
        className="metric-history-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar historico"}
      </button>
    </aside>
  );
}
