import type { BankMovement } from "../../pages/Fi/BankMovementsPage";

type BankMovementFormProps = {
  value: BankMovement;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: BankMovement) => void;
  onSave: () => void;
  onReset: () => void;
};

const movementTypeOptions = ["credito", "debito", "transferencia"];

export default function BankMovementForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: BankMovementFormProps) {
  const canSave =
    value.contaBancariaId.trim() !== "" &&
    value.tipoMovimento.trim() !== "" &&
    value.valor.trim() !== "" &&
    value.dataMovimento.trim() !== "";

  function update<K extends keyof BankMovement>(
    field: K,
    fieldValue: BankMovement[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="bank-movement-form">
      <div className="bank-movement-form__head">
        <div>
          <h3 className="bank-movement-form__title">
            {editing ? "Editar movimentacao" : "Nova movimentacao"}
          </h3>
          <p className="bank-movement-form__subtitle">
            Informe a conta, o tipo, o valor e o estado de conciliacao.
          </p>
          {editing && value.id ? (
            <p className="bank-movement-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="bank-movement-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="bank-movement-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="bank-movement-form__grid">
        <label className="bank-movement-form__field">
          <span>Conta bancaria ID</span>
          <input
            value={value.contaBancariaId}
            onChange={(event) =>
              update("contaBancariaId", event.target.value.replace(/\D/g, ""))
            }
            placeholder="1"
            disabled={!canEditFields}
          />
        </label>

        <label className="bank-movement-form__field">
          <span>Tipo de movimento</span>
          <select
            value={value.tipoMovimento}
            onChange={(event) => update("tipoMovimento", event.target.value)}
            disabled={!canEditFields}
          >
            {movementTypeOptions.map((option) => (
              <option key={option} value={option}>
                {option}
              </option>
            ))}
          </select>
        </label>

        <label className="bank-movement-form__field">
          <span>Valor</span>
          <input
            value={value.valor}
            onChange={(event) =>
              update("valor", event.target.value.replace(/[^0-9.,]/g, ""))
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        <label className="bank-movement-form__field">
          <span>Data do movimento</span>
          <input
            type="date"
            value={value.dataMovimento}
            onChange={(event) => update("dataMovimento", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="bank-movement-form__field bank-movement-form__field--span-2">
          <span>Historico</span>
          <input
            value={value.historico}
            onChange={(event) => update("historico", event.target.value)}
            placeholder="Descricao da movimentacao"
            disabled={!canEditFields}
          />
        </label>

        <label className="bank-movement-form__field">
          <span>Documento vinculado</span>
          <input
            value={value.documentoVinculado}
            onChange={(event) =>
              update("documentoVinculado", event.target.value)
            }
            placeholder="DOC-001"
            disabled={!canEditFields}
          />
        </label>

        <label className="bank-movement-form__checkbox">
          <input
            type="checkbox"
            checked={value.conciliado}
            onChange={(event) => {
              const checked = event.target.checked;
              onChange({
                ...value,
                conciliado: checked,
                dataConciliacao: checked ? value.dataConciliacao : "",
              });
            }}
            disabled={!canEditFields}
          />
          <span>Movimentacao conciliada</span>
        </label>

        <label className="bank-movement-form__field">
          <span>Data de conciliacao</span>
          <input
            type="date"
            value={value.dataConciliacao}
            onChange={(event) => update("dataConciliacao", event.target.value)}
            disabled={!canEditFields || !value.conciliado}
          />
        </label>
      </div>

      <button
        type="button"
        className="bank-movement-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar movimentacao"}
      </button>
    </aside>
  );
}
