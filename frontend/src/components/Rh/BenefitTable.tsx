import type { Benefit, EmployeeOption } from "../../pages/Rh/BenefitsPage";

type BenefitTableProps = {
  items: Benefit[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  employeeOptions: EmployeeOption[];
  onSelect: (item: Benefit) => void;
  onDelete: (item: Benefit) => void;
};

const benefitLabels: Record<string, string> = {
  vale_transporte: "Vale-transporte",
  vale_refeicao: "Vale-refeicao",
  plano_saude: "Plano de saude",
  plano_odontologico: "Plano odontologico",
};

function resolveEmployeeLabel(
  employeeId: string,
  employeeOptions: EmployeeOption[],
) {
  const match = employeeOptions.find(
    (option) => String(option.id) === employeeId,
  );
  return match ? match.label : `Colaborador #${employeeId || "-"}`;
}

export default function BenefitTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  employeeOptions,
  onSelect,
  onDelete,
}: BenefitTableProps) {
  return (
    <section className="benefit-table">
      <div className="benefit-table__head">
        <h3 className="benefit-table__title">Lista de benefícios</h3>
        <span className="benefit-table__meta">{items.length} registros</span>
      </div>

      <div className="benefit-table__wrap">
        <table className="benefit-table__table">
          <thead>
            <tr>
              <th>Colaborador</th>
              <th>Tipo</th>
              <th>Valor</th>
              <th>Período</th>
              <th>Status</th>
              <th>Ações</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={6} className="benefit-table__empty">
                  Carregando benefícios...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={6} className="benefit-table__empty">
                  Nenhum benefício encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.colaborador}-${item.tipoBeneficio}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="benefit-table__identity">
                      <strong>
                        {resolveEmployeeLabel(
                          item.colaborador,
                          employeeOptions,
                        )}
                      </strong>
                      <span>ID #{item.colaborador || "-"}</span>
                    </div>
                  </td>
                  <td>
                    {benefitLabels[item.tipoBeneficio] ??
                      item.tipoBeneficio ??
                      "-"}
                  </td>
                  <td>{item.valor || "-"}</td>
                  <td>
                    <div className="benefit-table__period">
                      <span>Inicio: {item.dataInicio || "-"}</span>
                      <span>Fim: {item.dataFim || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <span
                      className={
                        item.ativo
                          ? "benefit-table__badge benefit-table__badge--active"
                          : "benefit-table__badge benefit-table__badge--inactive"
                      }
                    >
                      {item.ativo ? "Ativo" : "Inativo"}
                    </span>
                  </td>
                  <td className="benefit-table__actions">
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
                      <span className="benefit-table__empty-action">
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
