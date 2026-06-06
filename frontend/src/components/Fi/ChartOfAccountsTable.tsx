import type { ChartOfAccount } from "../../pages/Fi/ChartOfAccountsPage";

type ParentOption = {
  id: number;
  label: string;
};

type ChartOfAccountsTableProps = {
  items: ChartOfAccount[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  parentOptions: ParentOption[];
  onSelect: (item: ChartOfAccount) => void;
  onDelete: (item: ChartOfAccount) => void;
};

function resolveParentLabel(
  value: string,
  options: ParentOption[],
  fallback: string,
) {
  const id = Number(value);
  const match = options.find((option) => option.id === id);
  return match?.label ?? fallback;
}

export default function ChartOfAccountsTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  parentOptions,
  onSelect,
  onDelete,
}: ChartOfAccountsTableProps) {
  return (
    <section className="chart-of-accounts-table">
      <div className="chart-of-accounts-table__head">
        <h3 className="chart-of-accounts-table__title">Lista de contas</h3>
        <span className="chart-of-accounts-table__meta">
          {items.length} registros
        </span>
      </div>

      <div className="chart-of-accounts-table__wrap">
        <table className="chart-of-accounts-table__table">
          <thead>
            <tr>
              <th>Codigo</th>
              <th>Nome</th>
              <th>Tipo</th>
              <th>Natureza</th>
              <th>Conta pai</th>
              <th>Situacao</th>
              <th>Acoes</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={7} className="chart-of-accounts-table__empty">
                  Carregando plano de contas...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={7} className="chart-of-accounts-table__empty">
                  Nenhuma conta encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.codigo}-${item.nome}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>{item.codigo || "-"}</td>
                  <td>{item.nome || "-"}</td>
                  <td>{item.tipoConta || "-"}</td>
                  <td>{item.natureza || "-"}</td>
                  <td>
                    {item.contaPai
                      ? resolveParentLabel(
                          item.contaPai,
                          parentOptions,
                          `Conta #${item.contaPai}`,
                        )
                      : "-"}
                  </td>
                  <td>
                    <span
                      className={
                        item.situacao === "ativo"
                          ? "chart-of-accounts-table__badge chart-of-accounts-table__badge--active"
                          : "chart-of-accounts-table__badge"
                      }
                    >
                      {item.situacao || "-"}
                    </span>
                  </td>
                  <td className="chart-of-accounts-table__actions">
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
                      <span className="chart-of-accounts-table__empty-action">
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
