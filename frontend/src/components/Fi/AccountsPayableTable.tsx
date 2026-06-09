import type { AccountPayable } from "../../pages/Fi/AccountsPayablePage";

type RelatedOption = {
  id: number;
  label: string;
};

type AccountsPayableTableProps = {
  items: AccountPayable[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  companyOptions: RelatedOption[];
  supplierOptions: RelatedOption[];
  onSelect: (item: AccountPayable) => void;
  onDelete: (item: AccountPayable) => void;
};

function resolveLabel(
  value: string,
  options: RelatedOption[],
  fallback: string,
) {
  const id = Number(value);
  const match = options.find((option) => option.id === id);
  return match?.label ?? fallback;
}

export default function AccountsPayableTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  companyOptions,
  supplierOptions,
  onSelect,
  onDelete,
}: AccountsPayableTableProps) {
  return (
    <section className="accounts-payable-table">
      <div className="accounts-payable-table__head">
        <h3 className="accounts-payable-table__title">Lista de contas</h3>
        <span className="accounts-payable-table__meta">
          {items.length} registros
        </span>
      </div>

      <div className="accounts-payable-table__wrap">
        <table className="accounts-payable-table__table">
          <thead>
            <tr>
              <th>Documento</th>
              <th>Empresa</th>
              <th>Fornecedor</th>
              <th>Valor</th>
              <th>Status</th>
              <th>Vencimento</th>
              <th>Ações</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={7} className="accounts-payable-table__empty">
                  Carregando contas a pagar...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={7} className="accounts-payable-table__empty">
                  Nenhuma conta a pagar encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.numeroDocumento}-${item.descricao}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="accounts-payable-table__identity">
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
                      item.fornecedor,
                      supplierOptions,
                      `Fornecedor #${item.fornecedor || "-"}`,
                    )}
                  </td>
                  <td>{item.valorOriginal || "-"}</td>
                  <td>
                    <span
                      className={
                        item.status === "pago"
                          ? "accounts-payable-table__badge accounts-payable-table__badge--paid"
                          : item.status === "parcial"
                            ? "accounts-payable-table__badge accounts-payable-table__badge--partial"
                            : "accounts-payable-table__badge"
                      }
                    >
                      {item.status || "-"}
                    </span>
                  </td>
                  <td>{item.dataVencimento || "-"}</td>
                  <td className="accounts-payable-table__actions">
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
                      <span className="accounts-payable-table__empty-action">
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
