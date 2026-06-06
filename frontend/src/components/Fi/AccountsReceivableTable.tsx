import type { AccountReceivable } from "../../pages/Fi/AccountsReceivablePage";

type RelatedOption = {
  id: number;
  label: string;
};

type AccountsReceivableTableProps = {
  items: AccountReceivable[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  companyOptions: RelatedOption[];
  clientOptions: RelatedOption[];
  onSelect: (item: AccountReceivable) => void;
  onDelete: (item: AccountReceivable) => void;
};

function resolveLabel(value: string, options: RelatedOption[], fallback: string) {
  const id = Number(value);
  const match = options.find((option) => option.id === id);
  return match?.label ?? fallback;
}

export default function AccountsReceivableTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  companyOptions,
  clientOptions,
  onSelect,
  onDelete,
}: AccountsReceivableTableProps) {
  return (
    <section className="accounts-receivable-table">
      <div className="accounts-receivable-table__head">
        <h3 className="accounts-receivable-table__title">Lista de contas</h3>
        <span className="accounts-receivable-table__meta">
          {items.length} registros
        </span>
      </div>

      <div className="accounts-receivable-table__wrap">
        <table className="accounts-receivable-table__table">
          <thead>
            <tr>
              <th>Documento</th>
              <th>Empresa</th>
              <th>Cliente</th>
              <th>Valor</th>
              <th>Status</th>
              <th>Vencimento</th>
              <th>Acoes</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={7} className="accounts-receivable-table__empty">
                  Carregando contas a receber...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={7} className="accounts-receivable-table__empty">
                  Nenhuma conta a receber encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.numeroDocumento}-${item.descricao}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="accounts-receivable-table__identity">
                      <strong>{item.numeroDocumento || "Sem numero"}</strong>
                      <span>{item.descricao || "Sem descricao"}</span>
                    </div>
                  </td>
                  <td>
                    {resolveLabel(
                      item.empresa,
                      companyOptions,
                      `Empresa #${item.empresa || "-"}`,
                    )}
                  </td>
                  <td>
                    {resolveLabel(
                      item.cliente,
                      clientOptions,
                      `Cliente #${item.cliente || "-"}`,
                    )}
                  </td>
                  <td>{item.valorOriginal || "-"}</td>
                  <td>
                    <span
                      className={
                        item.status === "pago"
                          ? "accounts-receivable-table__badge accounts-receivable-table__badge--paid"
                          : item.status === "parcial"
                            ? "accounts-receivable-table__badge accounts-receivable-table__badge--partial"
                            : "accounts-receivable-table__badge"
                      }
                    >
                      {item.status || "-"}
                    </span>
                  </td>
                  <td>{item.dataVencimento || "-"}</td>
                  <td className="accounts-receivable-table__actions">
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
                      <span className="accounts-receivable-table__empty-action">
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
