import type { Dependent } from "../../pages/Rh/DependentsPage";

type EmployeeOption = {
  id: number;
  label: string;
};

type DependentTableProps = {
  items: Dependent[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  employeeOptions: EmployeeOption[];
  onSelect: (item: Dependent) => void;
  onDelete: (item: Dependent) => void;
};

function resolveEmployeeLabel(
  value: string,
  options: EmployeeOption[],
  fallback: string,
) {
  const id = Number(value);
  const match = options.find((option) => option.id === id);
  return match?.label ?? fallback;
}

export default function DependentTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  employeeOptions,
  onSelect,
  onDelete,
}: DependentTableProps) {
  return (
    <section className="dependent-table">
      <div className="dependent-table__head">
        <h3 className="dependent-table__title">Lista de dependentes</h3>
        <span className="dependent-table__meta">{items.length} registros</span>
      </div>

      <div className="dependent-table__wrap">
        <table className="dependent-table__table">
          <thead>
            <tr>
              <th>Nome</th>
              <th>Colaborador</th>
              <th>Parentesco</th>
              <th>CPF</th>
              <th>Nascimento</th>
              <th>Ações</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={6} className="dependent-table__empty">
                  Carregando dependentes...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={6} className="dependent-table__empty">
                  Nenhum dependente encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.nome}-${item.dataNascimento}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>{item.nome || "-"}</td>
                  <td>
                    {resolveEmployeeLabel(
                      item.colaborador,
                      employeeOptions,
                      `Colaborador #${item.colaborador || "-"}`,
                    )}
                  </td>
                  <td>{item.parentesco || "-"}</td>
                  <td>{item.cpf || "-"}</td>
                  <td>{item.dataNascimento || "-"}</td>
                  <td className="dependent-table__actions">
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
                      <span className="dependent-table__empty-action">
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
