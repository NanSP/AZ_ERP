import type { PurchaseRecord } from "../../pages/Mm/PurchasesPage";

type PurchasesTableProps = {
  items: PurchaseRecord[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: PurchaseRecord) => void;
  onDelete: (item: PurchaseRecord) => void;
};

export default function PurchasesTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: PurchasesTableProps) {
  return (
    <section className="purchases-table">
      <div className="purchases-table__head">
        <h3 className="purchases-table__title">Compras</h3>
        <span className="purchases-table__meta">{items.length} registros</span>
      </div>

      <div className="purchases-table__wrap">
        <table className="purchases-table__table">
          <thead>
            <tr>
              <th>Fornecedor</th>
              <th>Prazo</th>
              <th>Financeiro</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="purchases-table__empty">
                  Carregando compras...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="purchases-table__empty">
                  Nenhuma compra encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.fornecedorId}-${item.dataPedido}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="purchases-table__details">
                      <strong>Fornecedor #{item.fornecedorId || "-"}</strong>
                      <span>Status: {item.status || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="purchases-table__details">
                      <strong>Pedido: {item.dataPedido || "-"}</strong>
                      <span>Prevista: {item.dataPrevistaEntrega || "-"}</span>
                      <span>Entrega: {item.dataEntrega || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="purchases-table__details">
                      <strong>Valor: {item.valorTotal || "-"}</strong>
                      <span>{item.condicoesPagamento || "-"}</span>
                    </div>
                  </td>
                  <td className="purchases-table__actions">
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
                      <span className="purchases-table__empty-action">
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
