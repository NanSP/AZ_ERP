import type {
  EmployeeOption,
  ProductionEntry,
  ProductionOrderOption,
  RelatedAccess,
} from "../../pages/Pp/ProductionEntriesPage";

type ProductionEntryFormProps = {
  value: ProductionEntry;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  orderOptions: ProductionOrderOption[];
  orderAccess: RelatedAccess;
  employeeOptions: EmployeeOption[];
  employeeAccess: RelatedAccess;
  onChange: (value: ProductionEntry) => void;
  onSave: () => void;
  onReset: () => void;
};

export default function ProductionEntryForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  orderOptions,
  orderAccess,
  employeeOptions,
  employeeAccess,
  onChange,
  onSave,
  onReset,
}: ProductionEntryFormProps) {
  const produced =
    value.quantidadeProduzida.trim() === ""
      ? 0
      : Number(value.quantidadeProduzida.replace(",", "."));
  const scrap =
    value.quantidadeRefugo.trim() === ""
      ? 0
      : Number(value.quantidadeRefugo.replace(",", "."));
  const downtime =
    value.tempoParado.trim() === ""
      ? 0
      : Number(value.tempoParado.replace(",", "."));

  const canSave =
    value.op.trim() !== "" &&
    value.operador.trim() !== "" &&
    (!value.dataHoraInicio || !value.dataHoraFim || value.dataHoraFim >= value.dataHoraInicio) &&
    !Number.isNaN(produced) &&
    produced >= 0 &&
    !Number.isNaN(scrap) &&
    scrap >= 0 &&
    !Number.isNaN(downtime) &&
    downtime >= 0;

  function update<K extends keyof ProductionEntry>(
    field: K,
    fieldValue: ProductionEntry[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="production-entry-form">
      <div className="production-entry-form__head">
        <div>
          <h3 className="production-entry-form__title">
            {editing ? "Editar apontamento" : "Novo apontamento"}
          </h3>
          <p className="production-entry-form__subtitle">
            Registre operador, janela de trabalho, producao, refugo e paradas.
          </p>
          {editing && value.id ? (
            <p className="production-entry-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="production-entry-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="production-entry-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="production-entry-form__grid">
        <label className="production-entry-form__field">
          <span>Ordem de producao</span>
          {orderOptions.length > 0 ? (
            <select
              value={value.op}
              onChange={(event) => update("op", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione</option>
              {orderOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.op}
              onChange={(event) =>
                update("op", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID da OP"
              disabled={!canEditFields}
            />
          )}
          {orderAccess === "unavailable" ? (
            <small className="production-entry-form__hint">
              Lista indisponivel. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="production-entry-form__field">
          <span>Operador</span>
          {employeeOptions.length > 0 ? (
            <select
              value={value.operador}
              onChange={(event) => update("operador", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione</option>
              {employeeOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.operador}
              onChange={(event) =>
                update("operador", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do operador"
              disabled={!canEditFields}
            />
          )}
          {employeeAccess === "unavailable" ? (
            <small className="production-entry-form__hint">
              Lista indisponivel. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="production-entry-form__field">
          <span>Maquina</span>
          <input
            value={value.maquinaId}
            onChange={(event) =>
              update("maquinaId", event.target.value.replace(/\D/g, ""))
            }
            placeholder="ID da maquina"
            disabled={!canEditFields}
          />
        </label>

        <label className="production-entry-form__field">
          <span>Tempo parado</span>
          <input
            value={value.tempoParado}
            onChange={(event) =>
              update(
                "tempoParado",
                event.target.value.replace(/[^0-9,.-]/g, ""),
              )
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>

        <label className="production-entry-form__field">
          <span>Data e hora inicio</span>
          <input
            type="datetime-local"
            value={value.dataHoraInicio}
            onChange={(event) => update("dataHoraInicio", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="production-entry-form__field">
          <span>Data e hora fim</span>
          <input
            type="datetime-local"
            value={value.dataHoraFim}
            onChange={(event) => update("dataHoraFim", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="production-entry-form__field">
          <span>Quantidade produzida</span>
          <input
            value={value.quantidadeProduzida}
            onChange={(event) =>
              update(
                "quantidadeProduzida",
                event.target.value.replace(/[^0-9,.-]/g, ""),
              )
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>

        <label className="production-entry-form__field">
          <span>Quantidade refugo</span>
          <input
            value={value.quantidadeRefugo}
            onChange={(event) =>
              update(
                "quantidadeRefugo",
                event.target.value.replace(/[^0-9,.-]/g, ""),
              )
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>

        <label className="production-entry-form__field production-entry-form__field--span-2">
          <span>Observacoes</span>
          <textarea
            value={value.observacoes}
            onChange={(event) => update("observacoes", event.target.value)}
            placeholder="Paradas, setup, ajuste ou observacoes operacionais"
            disabled={!canEditFields}
            rows={4}
          />
        </label>
      </div>

      <button
        type="button"
        className="production-entry-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar apontamento"}
      </button>
    </aside>
  );
}
