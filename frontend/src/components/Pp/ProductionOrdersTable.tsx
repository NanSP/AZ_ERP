import type {
  ProductOption,
  ProductionOrder,
} from "../../pages/Pp/ProductionOrdersPage";

type ProductionOrdersTableProps = {
  items: ProductionOrder[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  productOptions: ProductOption[];
  onSelect: (item: ProductionOrder) => void;
  onDelete: (item: ProductionOrder) => void;
};

function resolveProductLabel(id: string, options: ProductOption[]) {
  const match = options.find((option) => String(option.id) === id);
  return match ? match.label : id ? `Produto #${id}` : "-";
}

export default function ProductionOrdersTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  productOptions,
  onSelect,
  onDelete,
}: ProductionOrdersTableProps) {
  return (
    <section className="production-orders-table">
      <div className="production-orders-table__head">
        <h3 className="production-orders-table__title">Ordens de produção</h3>
        <span className="production-orders-table__meta">
          {items.length} registros
        </span>
      </div>

      <div className="production-orders-table__wrap">
        <table className="production-orders-table__table">
          <thead>
            <tr>
              <th>OP</th>
              <th>Produto</th>
              <th>Quantidades</th>
              <th>Datas</th>
              <th>Status</th>
              <th>Ações</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={6} className="production-orders-table__empty">
                  Carregando ordens de produção...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={6} className="production-orders-table__empty">
                  Nenhuma ordem de produção encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? item.numeroOp}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="production-orders-table__identity">
                      <strong>{item.numeroOp || "Sem numero"}</strong>
                      <span>Prioridade {item.prioridade || "-"}</span>
                    </div>
                  </td>
                  <td>{resolveProductLabel(item.produto, productOptions)}</td>
                  <td>
                    <div className="production-orders-table__details">
                      <span>Planejada: {item.quantidadePlanejada || "-"}</span>
                      <span>Produzida: {item.quantidadeProduzida || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="production-orders-table__details">
                      <span>Emissão: {item.dataEmissao || "-"}</span>
                      <span>Prevista: {item.dataPrevista || "-"}</span>
                      <span>Fim: {item.dataFim || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <span
                      className={
                        item.status === "concluida"
                          ? "production-orders-table__badge production-orders-table__badge--done"
                          : item.status === "em_producao"
                            ? "production-orders-table__badge production-orders-table__badge--progress"
                            : item.status === "cancelada"
                              ? "production-orders-table__badge production-orders-table__badge--cancelled"
                              : "production-orders-table__badge"
                      }
                    >
                      {item.status || "-"}
                    </span>
                  </td>
                  <td className="production-orders-table__actions">
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
                      <span className="production-orders-table__empty-action">
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
