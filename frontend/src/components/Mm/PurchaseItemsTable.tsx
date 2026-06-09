import type { PurchaseItemRecord } from "../../pages/Mm/PurchaseItemsPage";

type PurchaseItemsTableProps = {
  items: PurchaseItemRecord[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: PurchaseItemRecord) => void;
  onDelete: (item: PurchaseItemRecord) => void;
};

export default function PurchaseItemsTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: PurchaseItemsTableProps) {
  return (
    <section className="purchase-items-table">
      <div className="purchase-items-table__head">
        <h3 className="purchase-items-table__title">Itens de compra</h3>
        <span className="purchase-items-table__meta">
          {items.length} registros
        </span>
      </div>

      <div className="purchase-items-table__wrap">
        <table className="purchase-items-table__table">
          <thead>
            <tr>
              <th>Vinculos</th>
              <th>Quantidades</th>
              <th>Valores</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="purchase-items-table__empty">
                  Carregando itens de compra...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="purchase-items-table__empty">
                  Nenhum item de compra encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.compraId}-${item.produtoId}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="purchase-items-table__details">
                      <strong>Compra #{item.compraId || "-"}</strong>
                      <span>Produto #{item.produtoId || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="purchase-items-table__details">
                      <strong>Comprada: {item.quantidade || "-"}</strong>
                      <span>Recebida: {item.quantidadeRecebida || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="purchase-items-table__details">
                      <strong>Unitário: {item.valorUnitario || "-"}</strong>
                      <span>Total: {item.valorTotal || "-"}</span>
                    </div>
                  </td>
                  <td className="purchase-items-table__actions">
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
                      <span className="purchase-items-table__empty-action">
                        Sem ações
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
