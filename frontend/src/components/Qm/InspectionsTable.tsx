import type {
  EmployeeOption,
  InspectionEntry,
  ProductOption,
} from "../../pages/Qm/InspectionsPage";

type InspectionsTableProps = {
  items: InspectionEntry[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  productOptions: ProductOption[];
  employeeOptions: EmployeeOption[];
  onSelect: (item: InspectionEntry) => void;
  onDelete: (item: InspectionEntry) => void;
};

function resolveProductLabel(productId: string, productOptions: ProductOption[]) {
  const match = productOptions.find((option) => String(option.id) === productId);
  return match ? match.label : productId ? `Produto #${productId}` : "-";
}

function resolveEmployeeLabel(
  employeeId: string,
  employeeOptions: EmployeeOption[],
) {
  const match = employeeOptions.find((option) => String(option.id) === employeeId);
  return match ? match.label : employeeId ? `Inspetor #${employeeId}` : "-";
}

export default function InspectionsTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  productOptions,
  employeeOptions,
  onSelect,
  onDelete,
}: InspectionsTableProps) {
  return (
    <section className="inspections-table">
      <div className="inspections-table__head">
        <h3 className="inspections-table__title">Inspecoes</h3>
        <span className="inspections-table__meta">{items.length} registros</span>
      </div>

      <div className="inspections-table__wrap">
        <table className="inspections-table__table">
          <thead>
            <tr>
              <th>Produto</th>
              <th>Tipo</th>
              <th>Quantidades</th>
              <th>Resultado</th>
              <th>Inspetor</th>
              <th>Acoes</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={6} className="inspections-table__empty">
                  Carregando inspecoes...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={6} className="inspections-table__empty">
                  Nenhuma inspecao encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.produto}-${item.dataInspecao}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="inspections-table__identity">
                      <strong>{resolveProductLabel(item.produto, productOptions)}</strong>
                      <span>Lote: {item.lote || "-"}</span>
                    </div>
                  </td>
                  <td>{item.tipoInspecao || "-"}</td>
                  <td>
                    <div className="inspections-table__details">
                      <span>Inspec.: {item.quantidadeInspecionada || "0"}</span>
                      <span>Aprov.: {item.quantidadeAprovada || "0"}</span>
                      <span>Reprov.: {item.quantidadeReprovada || "0"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="inspections-table__details">
                      <span>{item.resultado || "-"}</span>
                      <span>{item.dataInspecao || "-"}</span>
                    </div>
                  </td>
                  <td>{resolveEmployeeLabel(item.inspetor, employeeOptions)}</td>
                  <td className="inspections-table__actions">
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
                      <span className="inspections-table__empty-action">
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
