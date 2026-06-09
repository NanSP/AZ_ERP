import type { Partner } from "../../pages/Core/PartnersPage";
import type { PurchaseRecord } from "../../pages/Mm/PurchasesPage";

type PurchaseFormProps = {
  value: PurchaseRecord;
  editing: boolean;
  suppliers: Partner[];
  canReadSuppliers: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: PurchaseRecord) => void;
  onSave: () => void;
  onReset: () => void;
};

const statusOptions = [
  { value: "aberto", label: "Aberto" },
  { value: "parcial", label: "Parcial" },
  { value: "recebido", label: "Recebido" },
  { value: "cancelado", label: "Cancelado" },
];

export default function PurchaseForm({
  value,
  editing,
  suppliers,
  canReadSuppliers,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: PurchaseFormProps) {
  const valorTotal =
    value.valorTotal.trim() === ""
      ? 0
      : Number(value.valorTotal.replace(",", "."));
  const canSave =
    value.fornecedorId.trim() !== "" &&
    value.dataPedido.trim() !== "" &&
    value.status.trim() !== "" &&
    !Number.isNaN(valorTotal) &&
    valorTotal >= 0 &&
    (value.dataPrevistaEntrega.trim() === "" ||
      value.dataPrevistaEntrega >= value.dataPedido) &&
    (value.dataEntrega.trim() === "" ||
      value.dataEntrega >= value.dataPedido) &&
    (value.status !== "recebido" || value.dataEntrega.trim() !== "");

  function update<K extends keyof PurchaseRecord>(
    field: K,
    fieldValue: PurchaseRecord[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="purchase-form">
      <div className="purchase-form__head">
        <div>
          <h3 className="purchase-form__title">
            {editing ? "Editar compra" : "Nova compra"}
          </h3>
          <p className="purchase-form__subtitle">
            Defina fornecedor, datas, status comercial e contexto do pedido.
          </p>
          {editing && value.id ? (
            <p className="purchase-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="purchase-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="purchase-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="purchase-form__grid">
        <label className="purchase-form__field purchase-form__field--span-2">
          <span>Fornecedor</span>
          {canReadSuppliers ? (
            <select
              value={value.fornecedorId}
              onChange={(event) => update("fornecedorId", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione um fornecedor</option>
              {suppliers.map((supplier) => (
                <option
                  key={supplier.id ?? supplier.codigo}
                  value={String(supplier.id ?? "")}
                >
                  {supplier.codigo || "Sem codigo"} -{" "}
                  {supplier.nome || "Sem nome"}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.fornecedorId}
              onChange={(event) =>
                update("fornecedorId", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do fornecedor"
              disabled={!canEditFields}
            />
          )}
        </label>

        <label className="purchase-form__field">
          <span>Data do pedido</span>
          <input
            type="date"
            value={value.dataPedido}
            onChange={(event) => update("dataPedido", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="purchase-form__field">
          <span>Data prevista de entrega</span>
          <input
            type="date"
            value={value.dataPrevistaEntrega}
            onChange={(event) =>
              update("dataPrevistaEntrega", event.target.value)
            }
            disabled={!canEditFields}
          />
        </label>

        <label className="purchase-form__field">
          <span>Data de entrega</span>
          <input
            type="date"
            value={value.dataEntrega}
            onChange={(event) => update("dataEntrega", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="purchase-form__field">
          <span>Status</span>
          <select
            value={value.status}
            onChange={(event) => update("status", event.target.value)}
            disabled={!canEditFields}
          >
            {statusOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="purchase-form__field">
          <span>Valor total</span>
          <input
            value={value.valorTotal}
            onChange={(event) =>
              update("valorTotal", event.target.value.replace(/[^0-9,.-]/g, ""))
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>

        <label className="purchase-form__field">
          <span>Condições de pagamento</span>
          <input
            value={value.condicoesPagamento}
            onChange={(event) =>
              update("condicoesPagamento", event.target.value)
            }
            placeholder="30 dias, boleto, 3x sem juros"
            disabled={!canEditFields}
          />
        </label>

        <label className="purchase-form__field purchase-form__field--span-2">
          <span>Observações</span>
          <textarea
            value={value.observacoes}
            onChange={(event) => update("observacoes", event.target.value)}
            placeholder="Regras comerciais, condicoes de entrega e observacoes internas"
            disabled={!canEditFields}
            rows={5}
          />
        </label>
      </div>

      <button
        type="button"
        className="purchase-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar compra"}
      </button>
    </aside>
  );
}
