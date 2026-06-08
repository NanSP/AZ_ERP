import type { MovementRecord } from "../../pages/Mm/MovementsPage";

type MovementsTableProps = {
  items: MovementRecord[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: MovementRecord) => void;
  onDelete: (item: MovementRecord) => void;
};

export default function MovementsTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: MovementsTableProps) {
  return (
    <section className="movements-table">
      <div className="movements-table__head">
        <h3 className="movements-table__title">Movimentacoes</h3>
        <span className="movements-table__meta">{items.length} registros</span>
      </div>

      <div className="movements-table__wrap">
        <table className="movements-table__table">
          <thead>
            <tr>
              <th>Operacao</th>
              <th>Financeiro</th>
              <th>Contexto</th>
              <th>Acoes</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="movements-table__empty">
                  Carregando movimentacoes...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="movements-table__empty">
                  Nenhuma movimentacao encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.estoqueId}-${item.createdAt}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="movements-table__details">
                      <strong>{item.tipoMovimento || "-"}</strong>
                      <span>Estoque #{item.estoqueId || "-"}</span>
                      <span>Quantidade: {item.quantidade || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="movements-table__details">
                      <strong>Unit.: {item.valorUnitario || "-"}</strong>
                      <span>Total: {item.valorTotal || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="movements-table__details">
                      <strong>{item.documentoReferencia || "-"}</strong>
                      <span>{item.motivo || "-"}</span>
                      <span>Usuario #{item.usuarioId || "-"}</span>
                    </div>
                  </td>
                  <td className="movements-table__actions">
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
                      <span className="movements-table__empty-action">
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
