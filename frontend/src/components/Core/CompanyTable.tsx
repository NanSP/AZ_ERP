import type { Company } from "../../pages/Core/CompaniesPage";

type CompanyTableProps = {
  items: Company[];
  loading: boolean;
  selectedId?: number;
  onSelect: (item: Company) => void;
  onDelete: (item: Company) => void;
};

export default function CompanyTable({
  items,
  loading,
  selectedId,
  onSelect,
  onDelete,
}: CompanyTableProps) {
  return (
    <section className="company-table">
      <div className="company-table__head">
        <h3 className="company-table__title">Lista de empresas</h3>
        <span className="company-table__meta">{items.length} registros</span>
      </div>

      <div className="company-table__wrap">
        <table className="company-table__table">
          <thead>
            <tr>
              <th>Código</th>
              <th>Razão social</th>
              <th>CNPJ</th>
              <th>Regime</th>
              <th>Situação</th>
              <th>Ações</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={6} className="company-table__empty">
                  Carregando empresas...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={6} className="company-table__empty">
                  Nenhuma empresa encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.cnpj}-${item.razaoSocial}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>{item.codigo || "-"}</td>
                  <td>
                    <div className="company-table__identity">
                      <strong>{item.razaoSocial || "-"}</strong>
                      <span>{item.nomeFantasia || "Sem nome fantasia"}</span>
                    </div>
                  </td>
                  <td>{item.cnpj || "-"}</td>
                  <td>{item.regimeTributario || "-"}</td>
                  <td>
                    <span
                      className={
                        item.situacao === "ativo"
                          ? "company-table__badge company-table__badge--active"
                          : "company-table__badge"
                      }
                    >
                      {item.situacao || "-"}
                    </span>
                  </td>
                  <td className="company-table__actions">
                    <button type="button" onClick={() => onSelect(item)}>
                      Editar
                    </button>
                    <button type="button" onClick={() => onDelete(item)}>
                      Excluir
                    </button>
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
