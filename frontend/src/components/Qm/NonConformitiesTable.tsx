import type {
  EmployeeOption,
  InspectionOption,
  NonConformityEntry,
} from "../../pages/Qm/NonConformitiesPage";

type NonConformitiesTableProps = {
  items: NonConformityEntry[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  inspectionOptions: InspectionOption[];
  employeeOptions: EmployeeOption[];
  onSelect: (item: NonConformityEntry) => void;
  onDelete: (item: NonConformityEntry) => void;
};

function resolveInspectionLabel(
  inspectionId: string,
  inspectionOptions: InspectionOption[],
) {
  const match = inspectionOptions.find(
    (option) => String(option.id) === inspectionId,
  );
  return match ? match.label : inspectionId ? `Inspecao #${inspectionId}` : "-";
}

function resolveEmployeeLabel(
  employeeId: string,
  employeeOptions: EmployeeOption[],
) {
  const match = employeeOptions.find(
    (option) => String(option.id) === employeeId,
  );
  return match ? match.label : employeeId ? `Responsavel #${employeeId}` : "-";
}

export default function NonConformitiesTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  inspectionOptions,
  employeeOptions,
  onSelect,
  onDelete,
}: NonConformitiesTableProps) {
  return (
    <section className="non-conformities-table">
      <div className="non-conformities-page__panel-head">
        <h3 className="non-conformities-page__panel-title">
          Não conformidades
        </h3>
        <span className="non-conformities-page__panel-subtitle">
          {items.length} registros
        </span>
      </div>

      <div className="non-conformities-page__table-wrap">
        <table className="non-conformities-page__table">
          <thead>
            <tr>
              <th>Inspeção</th>
              <th>Tipo</th>
              <th>Status</th>
              <th>Responsável</th>
              <th>Datas</th>
              <th>Ações</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={6} className="non-conformities-page__empty">
                  Carregando não conformidades...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={6} className="non-conformities-page__empty">
                  Nenhuma não conformidade encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.inspecao}-${item.dataIdentificacao}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="non-conformities-page__identity">
                      <strong>
                        {resolveInspectionLabel(
                          item.inspecao,
                          inspectionOptions,
                        )}
                      </strong>
                      <span>{item.descricao || "-"}</span>
                    </div>
                  </td>
                  <td>{item.tipoNaoConformidade || "-"}</td>
                  <td>{item.status || "aberta"}</td>
                  <td>
                    {resolveEmployeeLabel(item.responsavel, employeeOptions)}
                  </td>
                  <td>
                    <div className="non-conformities-page__details">
                      <span>Ident.: {item.dataIdentificacao || "-"}</span>
                      <span>Resol.: {item.dataResolucao || "-"}</span>
                    </div>
                  </td>
                  <td className="non-conformities-page__actions">
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
                      <span className="non-conformities-page__empty-action">
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
