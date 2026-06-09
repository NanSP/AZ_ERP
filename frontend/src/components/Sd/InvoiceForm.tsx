import type { OrderRecord } from "../../pages/Sd/OrdersPage";
import type { InvoiceRecord } from "../../pages/Sd/InvoicesPage";

type InvoiceFormProps = {
  value: InvoiceRecord;
  editing: boolean;
  orders: OrderRecord[];
  canReadOrders: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: InvoiceRecord) => void;
  onSave: () => void;
  onReset: () => void;
};

const statusOptions = [
  { value: "emitida", label: "Emitida" },
  { value: "paga", label: "Paga" },
  { value: "vencida", label: "Vencida" },
  { value: "cancelada", label: "Cancelada" },
];

export default function InvoiceForm({
  value,
  editing,
  orders,
  canReadOrders,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: InvoiceFormProps) {
  const valorTotal =
    value.valorTotal.trim() === ""
      ? 0
      : Number(value.valorTotal.replace(",", "."));
  const needsDueDate = value.status === "paga" || value.status === "vencida";
  const canSave =
    value.pedidoId.trim() !== "" &&
    value.numeroFatura.trim() !== "" &&
    value.dataEmissao.trim() !== "" &&
    value.status.trim() !== "" &&
    !Number.isNaN(valorTotal) &&
    valorTotal > 0 &&
    (!needsDueDate || value.dataVencimento.trim() !== "") &&
    (!value.dataVencimento || value.dataVencimento >= value.dataEmissao);

  function update<K extends keyof InvoiceRecord>(
    field: K,
    fieldValue: InvoiceRecord[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="invoice-form">
      <div className="invoice-form__head">
        <div>
          <h3 className="invoice-form__title">
            {editing ? "Editar fatura" : "Nova fatura"}
          </h3>
          <p className="invoice-form__subtitle">
            Emita a cobrança do pedido com número fiscal, valor total,
            vencimento e status financeiro.
          </p>
          {editing && value.id ? (
            <p className="invoice-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="invoice-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="invoice-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="invoice-form__grid">
        <label className="invoice-form__field invoice-form__field--span-2">
          <span>Pedido</span>
          {canReadOrders ? (
            <select
              value={value.pedidoId}
              onChange={(event) => update("pedidoId", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione um pedido</option>
              {orders.map((order) => (
                <option
                  key={order.id ?? `${order.clienteId}-${order.numeroPedido}`}
                  value={String(order.id ?? "")}
                >
                  #{order.id} - {order.numeroPedido || "Sem numero"} /{" "}
                  {order.status}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.pedidoId}
              onChange={(event) =>
                update("pedidoId", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do pedido"
              disabled={!canEditFields}
            />
          )}
        </label>

        <label className="invoice-form__field">
          <span>Número da fatura</span>
          <input
            value={value.numeroFatura}
            onChange={(event) => update("numeroFatura", event.target.value)}
            placeholder="FAT-2026-001"
            disabled={!canEditFields}
          />
        </label>

        <label className="invoice-form__field">
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

        <label className="invoice-form__field">
          <span>Data de emissão</span>
          <input
            type="date"
            value={value.dataEmissao}
            onChange={(event) => update("dataEmissao", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="invoice-form__field">
          <span>Data de vencimento</span>
          <input
            type="date"
            value={value.dataVencimento}
            onChange={(event) => update("dataVencimento", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="invoice-form__field invoice-form__field--span-2">
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
      </div>

      <button
        type="button"
        className="invoice-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar fatura"}
      </button>
    </aside>
  );
}
