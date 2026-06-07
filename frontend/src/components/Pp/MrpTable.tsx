import type { MrpItem, ProductOption } from "../../pages/Pp/MrpPage";

type MrpTableProps = {
  items: MrpItem[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  productOptions: ProductOption[];
  onSelect: (item: MrpItem) => void;
  onDelete: (item: MrpItem) => void;
};

function resolveProductLabel(id: string, options: ProductOption[]) {
  const match = options.find((option) => String(option.id) === id);
  return match ? match.label : id ? `Produto #${id}` : "-";
}

export default function MrpTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  productOptions,
  onSelect,
  onDelete,
}: MrpTableProps) {
  return (
    <section className="mrp-table">
      <div className="mrp-table__head">
        <h3 className="mrp-table__title">Registros de MRP</h3>
        <span className="mrp-table__meta">{items.length} registros</span>
      </div>

      <div className="mrp-table__wrap">
        <table className="mrp-table__table">
          <thead>
            <tr>
              <th>Produto</th>
              <th>Planejamento</th>
              <th>Resultado</th>
              <th>Acoes</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="mrp-table__empty">
                  Carregando MRP...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="mrp-table__empty">
                  Nenhum registro MRP encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.produto}-${item.periodo}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="mrp-table__details">
                      <strong>{resolveProductLabel(item.produto, productOptions)}</strong>
                      <span>Periodo: {item.periodo || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="mrp-table__details">
                      <span>Demanda: {item.demandaPrevista || "-"}</span>
                      <span>Estoque atual: {item.estoqueAtual || "-"}</span>
                      <span>Seguranca: {item.estoqueSeguranca || "-"}</span>
                      <span>Necessidade: {item.dataNecessidade || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="mrp-table__details">
                      <span>Compra: {item.necessidadeCompra || "-"}</span>
                      <span>Producao: {item.necessidadeProducao || "-"}</span>
                    </div>
                  </td>
                  <td className="mrp-table__actions">
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
                      <span className="mrp-table__empty-action">Sem acoes</span>
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
