import type { CashFlow } from "../../pages/Fi/CashFlowPage";

type CashFlowFormProps = {
  value: CashFlow;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: CashFlow) => void;
  onSave: () => void;
  onReset: () => void;
};

export default function CashFlowForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: CashFlowFormProps) {
  const canSave = value.dataReferencia.trim() !== "";

  function update<K extends keyof CashFlow>(field: K, fieldValue: CashFlow[K]) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  function renderMoneyField(
    label: string,
    field: keyof CashFlow,
    currentValue: string,
  ) {
    return (
      <label className="cash-flow-form__field">
        <span>{label}</span>
        <input
          value={currentValue}
          onChange={(event) =>
            update(field, event.target.value.replace(/[^0-9.,]/g, ""))
          }
          placeholder="0.00"
          disabled={!canEditFields}
        />
      </label>
    );
  }

  return (
    <aside className="cash-flow-form">
      <div className="cash-flow-form__head">
        <div>
          <h3 className="cash-flow-form__title">
            {editing ? "Editar fluxo" : "Novo fluxo"}
          </h3>
          <p className="cash-flow-form__subtitle">
            Informe valores previstos e realizados para a data de referencia.
          </p>
          {editing && value.id ? (
            <p className="cash-flow-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="cash-flow-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="cash-flow-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="cash-flow-form__grid">
        <label className="cash-flow-form__field">
          <span>Data de referencia</span>
          <input
            type="date"
            value={value.dataReferencia}
            onChange={(event) => update("dataReferencia", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        {renderMoneyField("Saldo inicial", "saldoInicial", value.saldoInicial)}
        {renderMoneyField(
          "Entradas previstas",
          "entradasPrevistas",
          value.entradasPrevistas,
        )}
        {renderMoneyField(
          "Saidas previstas",
          "saidasPrevistas",
          value.saidasPrevistas,
        )}
        {renderMoneyField(
          "Entradas realizadas",
          "entradasRealizadas",
          value.entradasRealizadas,
        )}
        {renderMoneyField(
          "Saidas realizadas",
          "saidasRealizadas",
          value.saidasRealizadas,
        )}

        <label className="cash-flow-form__field cash-flow-form__field--readonly">
          <span>Saldo final previsto</span>
          <input value={value.saldoFinalPrevisto} disabled />
        </label>

        <label className="cash-flow-form__field cash-flow-form__field--readonly">
          <span>Saldo final real</span>
          <input value={value.saldoFinalReal} disabled />
        </label>
      </div>

      <button
        type="button"
        className="cash-flow-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar fluxo"}
      </button>
    </aside>
  );
}
