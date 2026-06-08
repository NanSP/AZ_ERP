import type { OrderRecord } from "../../pages/Sd/OrdersPage";

type OrdersTableProps = {
  items: OrderRecord[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: OrderRecord) => void;
  onDelete: (item: OrderRecord) => void;
};

export default function OrdersTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: OrdersTableProps) {
  return (
    <section className="orders-table">
      <div className="orders-table__head">
        <h3 className="orders-table__title">Pedidos</h3>
        <span className="orders-table__meta">{items.length} registros</span>
      </div>

      <div className="orders-table__wrap">
        <table className="orders-table__table">
          <thead>
            <tr>
              <th>Comercial</th>
              <th>Calendario</th>
              <th>Financeiro</th>
              <th>Acoes</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="orders-table__empty">
                  Carregando pedidos...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="orders-table__empty">
                  Nenhum pedido encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.clienteId}-${item.numeroPedido}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="orders-table__details">
                      <strong>{item.numeroPedido || "Sem numero"}</strong>
                      <span>Cliente parceiro #{item.clienteId || "-"}</span>
                      <span>Status: {item.status || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="orders-table__details">
                      <strong>Pedido: {item.dataPedido || "-"}</strong>
                      <span>Entrega: {item.dataEntrega || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="orders-table__details">
                      <strong>Total: {item.valorTotal || "0"}</strong>
                      <span>Desconto: {item.descontoTotal || "0"}</span>
                      <span>{item.condicoesPagamento || "-"}</span>
                    </div>
                  </td>
                  <td className="orders-table__actions">
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
                      <span className="orders-table__empty-action">
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
