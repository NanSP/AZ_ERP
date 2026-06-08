import type { OrderItemRecord } from "../../pages/Sd/OrderItemsPage";

type OrderItemsTableProps = {
  items: OrderItemRecord[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: OrderItemRecord) => void;
  onDelete: (item: OrderItemRecord) => void;
};

export default function OrderItemsTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: OrderItemsTableProps) {
  return (
    <section className="order-items-table">
      <div className="order-items-table__head">
        <h3 className="order-items-table__title">Itens do pedido</h3>
        <span className="order-items-table__meta">{items.length} registros</span>
      </div>

      <div className="order-items-table__wrap">
        <table className="order-items-table__table">
          <thead>
            <tr>
              <th>Vinculos</th>
              <th>Quantidades</th>
              <th>Financeiro</th>
              <th>Acoes</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="order-items-table__empty">
                  Carregando itens do pedido...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="order-items-table__empty">
                  Nenhum item do pedido encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.pedidoId}-${item.produtoId}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="order-items-table__details">
                      <strong>Pedido #{item.pedidoId || "-"}</strong>
                      <span>Produto #{item.produtoId || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="order-items-table__details">
                      <strong>Quantidade: {item.quantidade || "-"}</strong>
                    </div>
                  </td>
                  <td>
                    <div className="order-items-table__details">
                      <strong>Unitario: {item.valorUnitario || "-"}</strong>
                      <span>Desconto: {item.desconto || "-"}</span>
                      <span>Total: {item.valorTotal || "-"}</span>
                    </div>
                  </td>
                  <td className="order-items-table__actions">
                    {canEdit ? (
                      <button type="button" onClick={() => onSelect(item)}>
                        Editar
                      </button>
                    ) : null}
                    {canDelete ? (
                      <button type="button" onClick={() => onDelete(item)}>
                        Excluir
                      </button>
                    ) : null}
                    {!canEdit && !canDelete ? (
                      <span className="order-items-table__empty-action">
                        Sem acoes
                      </span>
                    ) : null}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </section>
  );
}
