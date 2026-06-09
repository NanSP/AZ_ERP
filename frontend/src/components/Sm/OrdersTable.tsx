import type {
  EmployeeOption,
  Order,
  PartnerOption,
  ProductOption,
} from "../../pages/Sm/OrdersPage";

type OrdersTableProps = {
  items: Order[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  partnerOptions: PartnerOption[];
  productOptions: ProductOption[];
  employeeOptions: EmployeeOption[];
  onSelect: (item: Order) => void;
  onDelete: (item: Order) => void;
};

function resolveLabel(
  id: string,
  options: Array<{ id: number; label: string }>,
  fallback: string,
) {
  const match = options.find((option) => String(option.id) === id);
  return match ? match.label : id ? `${fallback} #${id}` : "-";
}

export default function OrdersTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  partnerOptions,
  productOptions,
  employeeOptions,
  onSelect,
  onDelete,
}: OrdersTableProps) {
  return (
    <section className="orders-table">
      <div className="orders-table__head">
        <h3 className="orders-table__title">Ordens de serviço</h3>
        <span className="orders-table__meta">{items.length} registros</span>
      </div>

      <div className="orders-table__wrap">
        <table className="orders-table__table">
          <thead>
            <tr>
              <th>OS</th>
              <th>Relacionamentos</th>
              <th>Agenda</th>
              <th>Status</th>
              <th>Ações</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={5} className="orders-table__empty">
                  Carregando ordens de serviço...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={5} className="orders-table__empty">
                  Nenhuma ordem de serviço encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? item.numeroOs}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="orders-table__identity">
                      <strong>{item.numeroOs || "Sem numero"}</strong>
                      <span>{item.tipoServico || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="orders-table__details">
                      <span>
                        {resolveLabel(item.cliente, partnerOptions, "Cliente")}
                      </span>
                      <span>
                        {resolveLabel(item.produto, productOptions, "Produto")}
                      </span>
                      <span>
                        {resolveLabel(item.tecnico, employeeOptions, "Tecnico")}
                      </span>
                    </div>
                  </td>
                  <td>
                    <div className="orders-table__details">
                      <span>Abertura: {item.dataAbertura || "-"}</span>
                      <span>Agenda: {item.dataAgendamento || "-"}</span>
                      <span>Fim: {item.dataFim || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <span
                      className={
                        item.status === "concluida"
                          ? "orders-table__badge orders-table__badge--done"
                          : item.status === "em_andamento"
                            ? "orders-table__badge orders-table__badge--progress"
                            : item.status === "cancelada"
                              ? "orders-table__badge orders-table__badge--cancelled"
                              : "orders-table__badge"
                      }
                    >
                      {item.status || "-"}
                    </span>
                  </td>
                  <td className="orders-table__actions">
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
                      <span className="orders-table__empty-action">
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
