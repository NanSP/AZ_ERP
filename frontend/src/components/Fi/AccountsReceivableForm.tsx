import type { AccountReceivable } from "../../pages/Fi/AccountsReceivablePage";

type RelatedOption = {
  id: number;
  label: string;
};

type RelatedAccess = "idle" | "loaded" | "unavailable";

type AccountsReceivableFormProps = {
  value: AccountReceivable;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  companyOptions: RelatedOption[];
  clientOptions: RelatedOption[];
  costCenterOptions: RelatedOption[];
  companyAccess: RelatedAccess;
  clientAccess: RelatedAccess;
  costCenterAccess: RelatedAccess;
  onChange: (value: AccountReceivable) => void;
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

export default function AccountsReceivableForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  companyOptions,
  clientOptions,
  costCenterOptions,
  companyAccess,
  clientAccess,
  costCenterAccess,
  onChange,
  onSave,
  onReset,
}: AccountsReceivableFormProps) {
  const canSave =
    value.empresa.trim() !== "" &&
    value.cliente.trim() !== "" &&
    value.valorOriginal.trim() !== "";

  function update<K extends keyof AccountReceivable>(
    field: K,
    fieldValue: AccountReceivable[K],
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
      <label className="accounts-receivable-form__field">
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
          <small className="accounts-receivable-form__hint">
            Lista indisponível. Informe o ID manualmente.
          </small>
        ) : null}
      </label>
    );
  }

  return (
    <aside className="accounts-receivable-form">
      <div className="accounts-receivable-form__head">
        <div>
          <h3 className="accounts-receivable-form__title">
            {editing ? "Editar conta" : "Nova conta"}
          </h3>
          <p className="accounts-receivable-form__subtitle">
            Configure empresa, cliente, valores e datas do recebimento.
          </p>
          {editing && value.id ? (
            <p className="accounts-receivable-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="accounts-receivable-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="accounts-receivable-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="accounts-receivable-form__grid">
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
          "Cliente",
          value.cliente,
          (next) => update("cliente", next),
          clientOptions,
          clientAccess,
          "Selecione um cliente",
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

        <label className="accounts-receivable-form__field">
          <span>Número do documento</span>
          <input
            value={value.numeroDocumento}
            onChange={(event) => update("numeroDocumento", event.target.value)}
            placeholder="NF-001"
            disabled={!canEditFields}
          />
        </label>

        <label className="accounts-receivable-form__field accounts-receivable-form__field--span-2">
          <span>Descrição</span>
          <input
            value={value.descricao}
            onChange={(event) => update("descricao", event.target.value)}
            placeholder="Descrição do título financeiro"
            disabled={!canEditFields}
          />
        </label>

        <label className="accounts-receivable-form__field">
          <span>Valor original</span>
          <input
            value={value.valorOriginal}
            onChange={(event) =>
              update(
                "valorOriginal",
                event.target.value.replace(/[^0-9.,]/g, ""),
              )
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        <label className="accounts-receivable-form__field">
          <span>Valor recebido</span>
          <input
            value={value.valorRecebido}
            onChange={(event) =>
              update(
                "valorRecebido",
                event.target.value.replace(/[^0-9.,]/g, ""),
              )
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        <label className="accounts-receivable-form__field">
          <span>Data de emissão</span>
          <input
            type="date"
            value={value.dataEmissao}
            onChange={(event) => update("dataEmissao", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="accounts-receivable-form__field">
          <span>Data de vencimento</span>
          <input
            type="date"
            value={value.dataVencimento}
            onChange={(event) => update("dataVencimento", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="accounts-receivable-form__field">
          <span>Data de recebimento</span>
          <input
            type="date"
            value={value.dataRecebimento}
            onChange={(event) => update("dataRecebimento", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="accounts-receivable-form__field">
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
        className="accounts-receivable-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving ? "Salvando..." : editing ? "Salvar alteracoes" : "Criar conta"}
      </button>
    </aside>
  );
}
