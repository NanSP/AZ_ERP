import type { BomItem, ProductOption } from "../../pages/Pp/BomPage";

type BomTableProps = {
  items: BomItem[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  productOptions: ProductOption[];
  onSelect: (item: BomItem) => void;
  onDelete: (item: BomItem) => void;
};

function resolveProductLabel(id: string, options: ProductOption[]) {
  const match = options.find((option) => String(option.id) === id);
  return match ? match.label : id ? `Produto #${id}` : "-";
}

export default function BomTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  productOptions,
  onSelect,
  onDelete,
}: BomTableProps) {
  return (
    <section className="bom-table">
      <div className="bom-table__head">
        <h3 className="bom-table__title">Composicoes BOM</h3>
        <span className="bom-table__meta">{items.length} registros</span>
      </div>

      <div className="bom-table__wrap">
        <table className="bom-table__table">
          <thead>
            <tr>
              <th>Estrutura</th>
              <th>Consumo</th>
              <th>Operação</th>
              <th>Ações</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="bom-table__empty">
                  Carregando BOM...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="bom-table__empty">
                  Nenhuma composição BOM encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.produtoPai}-${item.componente}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="bom-table__details">
                      <strong>
                        {resolveProductLabel(item.produtoPai, productOptions)}
                      </strong>
                      <span>
                        Componente:{" "}
                        {resolveProductLabel(item.componente, productOptions)}
                      </span>
                    </div>
                  </td>
                  <td>
                    <div className="bom-table__details">
                      <span>Quantidade: {item.quantidade || "-"}</span>
                      <span>Unidade: {item.unidadeMedida || "-"}</span>
                      <span>Nível: {item.nivel || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="bom-table__details">
                      <span>Prep: {item.tempoPreparacao || "-"}</span>
                      <span>Prod: {item.tempoProducao || "-"}</span>
                      <span>Roteiro: {item.roteiro || "-"}</span>
                    </div>
                  </td>
                  <td className="bom-table__actions">
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
                      <span className="bom-table__empty-action">Sem acoes</span>
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
