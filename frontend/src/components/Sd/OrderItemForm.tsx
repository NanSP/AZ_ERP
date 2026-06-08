import type { Product } from "../../pages/Core/ProductsPage";
import type { OrderRecord } from "../../pages/Sd/OrdersPage";
import type { OrderItemRecord } from "../../pages/Sd/OrderItemsPage";

type OrderItemFormProps = {
  value: OrderItemRecord;
  editing: boolean;
  orders: OrderRecord[];
  products: Product[];
  canReadOrders: boolean;
  canReadProducts: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: OrderItemRecord) => void;
  onSave: () => void;
  onReset: () => void;
};

export default function OrderItemForm({
  value,
  editing,
  orders,
  products,
  canReadOrders,
  canReadProducts,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: OrderItemFormProps) {
  const quantidade = Number((value.quantidade || "").replace(",", "."));
  const valorUnitario =
    value.valorUnitario.trim() === ""
      ? 0
      : Number(value.valorUnitario.replace(",", "."));
  const desconto =
    value.desconto.trim() === ""
      ? 0
      : Number(value.desconto.replace(",", "."));
  const subtotal =
    Number.isFinite(quantidade) && Number.isFinite(valorUnitario)
      ? quantidade * valorUnitario
      : 0;
  const canSave =
    value.pedidoId.trim() !== "" &&
    value.produtoId.trim() !== "" &&
    !Number.isNaN(quantidade) &&
    quantidade > 0 &&
    !Number.isNaN(valorUnitario) &&
    valorUnitario >= 0 &&
    !Number.isNaN(desconto) &&
    desconto >= 0 &&
    desconto <= subtotal;

  function update<K extends keyof OrderItemRecord>(
    field: K,
    fieldValue: OrderItemRecord[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  function normalizeDecimalInput(nextValue: string) {
    return nextValue.replace(/[^0-9,.-]/g, "");
  }

  return (
    <aside className="order-item-form">
      <div className="order-item-form__head">
        <div>
          <h3 className="order-item-form__title">
            {editing ? "Editar item do pedido" : "Novo item do pedido"}
          </h3>
          <p className="order-item-form__subtitle">
            Associe produto e pedido, informe quantidade, desconto e acompanhe
            o valor final calculado por item.
          </p>
          {editing && value.id ? (
            <p className="order-item-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="order-item-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="order-item-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="order-item-form__grid">
        <label className="order-item-form__field order-item-form__field--span-2">
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
                  #{order.id} - {order.numeroPedido || "Sem numero"} / {order.status}
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

        <label className="order-item-form__field order-item-form__field--span-2">
          <span>Produto</span>
          {canReadProducts ? (
            <select
              value={value.produtoId}
              onChange={(event) => update("produtoId", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione um produto</option>
              {products.map((product) => (
                <option
                  key={product.id ?? product.codigo}
                  value={String(product.id ?? "")}
                >
                  {product.codigo || "Sem codigo"} - {product.nome || "Sem nome"}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.produtoId}
              onChange={(event) =>
                update("produtoId", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do produto"
              disabled={!canEditFields}
            />
          )}
        </label>

        <label className="order-item-form__field">
          <span>Quantidade</span>
          <input
            value={value.quantidade}
            onChange={(event) =>
              update("quantidade", normalizeDecimalInput(event.target.value))
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>

        <label className="order-item-form__field">
          <span>Valor unitario</span>
          <input
            value={value.valorUnitario}
            onChange={(event) =>
              update("valorUnitario", normalizeDecimalInput(event.target.value))
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>

        <label className="order-item-form__field">
          <span>Desconto</span>
          <input
            value={value.desconto}
            onChange={(event) =>
              update("desconto", normalizeDecimalInput(event.target.value))
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>

        <label className="order-item-form__field">
          <span>Valor total</span>
          <input value={value.valorTotal} disabled />
        </label>
      </div>

      <button
        type="button"
        className="order-item-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar item do pedido"}
      </button>
    </aside>
  );
}
