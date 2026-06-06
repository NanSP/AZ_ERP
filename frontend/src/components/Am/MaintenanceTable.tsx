import type {
  AssetOption,
  EmployeeOption,
  MaintenanceEntry,
} from "../../pages/Am/MaintenancesPage";

type MaintenanceTableProps = {
  items: MaintenanceEntry[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  assetOptions: AssetOption[];
  employeeOptions: EmployeeOption[];
  onSelect: (item: MaintenanceEntry) => void;
  onDelete: (item: MaintenanceEntry) => void;
};

function resolveAssetLabel(assetId: string, assetOptions: AssetOption[]) {
  const match = assetOptions.find((option) => String(option.id) === assetId);
  return match ? match.label : assetId ? `Ativo #${assetId}` : "-";
}

function resolveEmployeeLabel(
  employeeId: string,
  employeeOptions: EmployeeOption[],
) {
  const match = employeeOptions.find((option) => String(option.id) === employeeId);
  return match ? match.label : employeeId ? `Tecnico #${employeeId}` : "-";
}

export default function MaintenanceTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  assetOptions,
  employeeOptions,
  onSelect,
  onDelete,
}: MaintenanceTableProps) {
  return (
    <section className="maintenance-table">
      <div className="maintenance-table__head">
        <h3 className="maintenance-table__title">Manutencoes</h3>
        <span className="maintenance-table__meta">{items.length} registros</span>
      </div>

      <div className="maintenance-table__wrap">
        <table className="maintenance-table__table">
          <thead>
            <tr>
              <th>Ativo</th>
              <th>Tipo</th>
              <th>Datas</th>
              <th>Custos</th>
              <th>Tecnico</th>
              <th>Acoes</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={6} className="maintenance-table__empty">
                  Carregando manutencoes...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={6} className="maintenance-table__empty">
                  Nenhuma manutencao encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.ativo}-${item.dataSolicitacao}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="maintenance-table__identity">
                      <strong>{resolveAssetLabel(item.ativo, assetOptions)}</strong>
                      <span>ID #{item.ativo || "-"}</span>
                    </div>
                  </td>
                  <td>{item.tipoManutencao || "-"}</td>
                  <td>
                    <div className="maintenance-table__details">
                      <span>Solicitacao: {item.dataSolicitacao || "-"}</span>
                      <span>Execucao: {item.dataExecucao || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="maintenance-table__details">
                      <span>Mao de obra: {item.custoMaoObra || "0"}</span>
                      <span>Material: {item.custoMaterial || "0"}</span>
                      <span>Total: {item.custoTotal || "0"}</span>
                    </div>
                  </td>
                  <td>{resolveEmployeeLabel(item.tecnico, employeeOptions)}</td>
                  <td className="maintenance-table__actions">
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
                      <span className="maintenance-table__empty-action">
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
