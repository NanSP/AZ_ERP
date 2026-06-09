import type {
  Asset,
  EmployeeOption,
  PartnerOption,
} from "../../pages/Am/AssetsPage";

type AssetsTableProps = {
  items: Asset[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  partnerOptions: PartnerOption[];
  employeeOptions: EmployeeOption[];
  onSelect: (item: Asset) => void;
  onDelete: (item: Asset) => void;
};

function resolvePartnerLabel(
  partnerId: string,
  partnerOptions: PartnerOption[],
) {
  const match = partnerOptions.find(
    (option) => String(option.id) === partnerId,
  );
  return match ? match.label : partnerId ? `Fornecedor #${partnerId}` : "-";
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

export default function AssetsTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  partnerOptions,
  employeeOptions,
  onSelect,
  onDelete,
}: AssetsTableProps) {
  return (
    <section className="assets-table">
      <div className="assets-table__head">
        <h3 className="assets-table__title">Bens patrimoniais</h3>
        <span className="assets-table__meta">{items.length} registros</span>
      </div>

      <div className="assets-table__wrap">
        <table className="assets-table__table">
          <thead>
            <tr>
              <th>Bem</th>
              <th>Tipo</th>
              <th>Valores</th>
              <th>Vinculos</th>
              <th>Status</th>
              <th>Ações</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={6} className="assets-table__empty">
                  Carregando bens patrimoniais...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={6} className="assets-table__empty">
                  Nenhum bem patrimonial encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? item.codigoPatrimonio}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="assets-table__identity">
                      <strong>{item.nome || "-"}</strong>
                      <span>{item.codigoPatrimonio || "Sem codigo"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="assets-table__details">
                      <span>{item.tipoAtivo || "-"}</span>
                      <span>{item.localizacao || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="assets-table__details">
                      <span>Aquisição: {item.valorAquisicao || "0"}</span>
                      <span>Atual: {item.valorAtual || "0"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="assets-table__details">
                      <span>
                        {resolvePartnerLabel(item.fornecedor, partnerOptions)}
                      </span>
                      <span>
                        {resolveEmployeeLabel(
                          item.responsavel,
                          employeeOptions,
                        )}
                      </span>
                    </div>
                  </td>
                  <td>
                    <span
                      className={
                        item.status === "ativo"
                          ? "assets-table__badge assets-table__badge--active"
                          : item.status === "manutencao"
                            ? "assets-table__badge assets-table__badge--maintenance"
                            : "assets-table__badge"
                      }
                    >
                      {item.status || "-"}
                    </span>
                  </td>
                  <td className="assets-table__actions">
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
                      <span className="assets-table__empty-action">
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
