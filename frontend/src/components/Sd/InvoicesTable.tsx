import type { InvoiceRecord } from "../../pages/Sd/InvoicesPage";

type InvoicesTableProps = {
  items: InvoiceRecord[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: InvoiceRecord) => void;
  onDelete: (item: InvoiceRecord) => void;
};

export default function InvoicesTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: InvoicesTableProps) {
  return (
    <section className="invoices-table">
      <div className="invoices-table__head">
        <h3 className="invoices-table__title">Faturas</h3>
        <span className="invoices-table__meta">{items.length} registros</span>
      </div>

      <div className="invoices-table__wrap">
        <table className="invoices-table__table">
          <thead>
            <tr>
              <th>Documento</th>
              <th>Calendário</th>
              <th>Financeiro</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="invoices-table__empty">
                  Carregando faturas...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="invoices-table__empty">
                  Nenhuma fatura encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.pedidoId}-${item.numeroFatura}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="invoices-table__details">
                      <strong>{item.numeroFatura || "-"}</strong>
                      <span>Pedido #{item.pedidoId || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="invoices-table__details">
                      <strong>Emissão: {item.dataEmissao || "-"}</strong>
                      <span>Vencimento: {item.dataVencimento || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="invoices-table__details">
                      <strong>Valor: {item.valorTotal || "-"}</strong>
                      <span>Status: {item.status || "-"}</span>
                    </div>
                  </td>
                  <td className="invoices-table__actions">
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
                      <span className="invoices-table__empty-action">
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
