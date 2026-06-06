import type { AccountPayable } from "../../pages/Fi/AccountsPayablePage";

type RelatedOption = {
  id: number;
  label: string;
};

type RelatedAccess = "idle" | "loaded" | "unavailable";

type AccountsPayableFormProps = {
  value: AccountPayable;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  companyOptions: RelatedOption[];
  supplierOptions: RelatedOption[];
  costCenterOptions: RelatedOption[];
  companyAccess: RelatedAccess;
  supplierAccess: RelatedAccess;
  costCenterAccess: RelatedAccess;
  onChange: (value: AccountPayable) => void;
  onSave: () => void;
  onReset: () => void;
};

const paymentMethodOptions = [
  "boleto",
  "pix",
  "ted",
  "cartao",
  "dinheiro",
  "outro",
];

export default function AccountsPayableForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  companyOptions,
  supplierOptions,
  costCenterOptions,
  companyAccess,
  supplierAccess,
  costCenterAccess,
  onChange,
  onSave,
  onReset,
}: AccountsPayableFormProps) {
  const canSave =
    value.empresa.trim() !== "" &&
    value.fornecedor.trim() !== "" &&
    value.valorOriginal.trim() !== "";

  function update<K extends keyof AccountPayable>(
    field: K,
    fieldValue: AccountPayable[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  function renderRelatedField(
    label: string,
    currentValue: string,
    onValueChange: (next: string) => void,
    options: RelatedOption[],
    access: RelatedAccess,
    placeholder: string,
    disabled: boolean,
  ) {
    return (
      <label className="accounts-payable-form__field">
        <span>{label}</span>
        {options.length > 0 ? (
          <select
            value={currentValue}
            onChange={(event) => onValueChange(event.target.value)}
            disabled={disabled}
          >
            <option value="">{placeholder}</option>
            {options.map((option) => (
              <option key={option.id} value={String(option.id)}>
                {option.label}
              </option>
            ))}
          </select>
        ) : (
          <input
            value={currentValue}
            onChange={(event) =>
              onValueChange(event.target.value.replace(/\D/g, ""))
            }
            placeholder={`ID ${label.toLowerCase()}`}
            disabled={disabled}
          />
        )}
        {access === "unavailable" ? (
          <small className="accounts-payable-form__hint">
            Lista indisponivel. Informe o ID manualmente.
          </small>
        ) : null}
      </label>
    );
  }

  return (
    <aside className="accounts-payable-form">
      <div className="accounts-payable-form__head">
        <div>
          <h3 className="accounts-payable-form__title">
            {editing ? "Editar conta" : "Nova conta"}
          </h3>
          <p className="accounts-payable-form__subtitle">
            Configure empresa, fornecedor, valores e datas da obrigacao.
          </p>
          {editing && value.id ? (
            <p className="accounts-payable-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="accounts-payable-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="accounts-payable-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="accounts-payable-form__grid">
        {renderRelatedField(
          "Empresa",
          value.empresa,
          (next) => update("empresa", next),
          companyOptions,
          companyAccess,
          "Selecione uma empresa",
          !canEditFields,
        )}

        {renderRelatedField(
          "Fornecedor",
          value.fornecedor,
          (next) => update("fornecedor", next),
          supplierOptions,
          supplierAccess,
          "Selecione um fornecedor",
          !canEditFields,
        )}

        {renderRelatedField(
          "Centro de custo",
          value.centroCusto,
          (next) => update("centroCusto", next),
          costCenterOptions,
          costCenterAccess,
          "Selecione um centro de custo",
          !canEditFields,
        )}

        <label className="accounts-payable-form__field">
          <span>Numero do documento</span>
          <input
            value={value.numeroDocumento}
            onChange={(event) => update("numeroDocumento", event.target.value)}
            placeholder="NF-001"
            disabled={!canEditFields}
          />
        </label>

        <label className="accounts-payable-form__field accounts-payable-form__field--span-2">
          <span>Descricao</span>
          <input
            value={value.descricao}
            onChange={(event) => update("descricao", event.target.value)}
            placeholder="Descricao da obrigacao financeira"
            disabled={!canEditFields}
          />
        </label>

        <label className="accounts-payable-form__field">
          <span>Valor original</span>
          <input
            value={value.valorOriginal}
            onChange={(event) =>
              update("valorOriginal", event.target.value.replace(/[^0-9.,]/g, ""))
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        <label className="accounts-payable-form__field">
          <span>Valor pago</span>
          <input
            value={value.valorPago}
            onChange={(event) =>
              update("valorPago", event.target.value.replace(/[^0-9.,]/g, ""))
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        <label className="accounts-payable-form__field">
          <span>Data de emissao</span>
          <input
            type="date"
            value={value.dataEmissao}
            onChange={(event) => update("dataEmissao", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="accounts-payable-form__field">
          <span>Data de vencimento</span>
          <input
            type="date"
            value={value.dataVencimento}
            onChange={(event) => update("dataVencimento", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="accounts-payable-form__field">
          <span>Data de pagamento</span>
          <input
            type="date"
            value={value.dataPagamento}
            onChange={(event) => update("dataPagamento", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="accounts-payable-form__field">
          <span>Forma de pagamento</span>
          <select
            value={value.formaPagamento}
            onChange={(event) => update("formaPagamento", event.target.value)}
            disabled={!canEditFields}
          >
            <option value="">Selecione</option>
            {paymentMethodOptions.map((option) => (
              <option key={option} value={option}>
                {option}
              </option>
            ))}
          </select>
        </label>
      </div>

      <button
        type="button"
        className="accounts-payable-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar conta"}
      </button>
    </aside>
  );
}
