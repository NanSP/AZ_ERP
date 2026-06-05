import type { Partner } from "../../pages/Core/PartnersPage";

type PartnerTableProps = {
  items: Partner[];
  loading: boolean;
  selectedId?: number;
  onSelect: (item: Partner) => void;
  onDelete: (item: Partner) => void;
};

export default function PartnerTable({
  items,
  loading,
  selectedId,
  onSelect,
  onDelete,
}: PartnerTableProps) {
  return (
    <section className="partner-table">
      <div className="partner-table__head">
        <h3 className="partner-table__title">Lista de parceiros</h3>
        <span className="partner-table__meta">{items.length} registros</span>
      </div>

      <div className="partner-table__wrap">
        <table className="partner-table__table">
          <thead>
            <tr>
              <th>Codigo</th>
              <th>Nome</th>
              <th>Tipo</th>
              <th>Documento</th>
              <th>Situacao</th>
              <th>Acoes</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={6} className="partner-table__empty">
                  Carregando parceiros...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={6} className="partner-table__empty">
                  Nenhum parceiro encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.documento}-${item.nome}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>{item.codigo || "-"}</td>
                  <td>
                    <div className="partner-table__identity">
                      <strong>{item.nome || "-"}</strong>
                      <span>{item.nomeFantasia || "Sem nome fantasia"}</span>
                    </div>
                  </td>
                  <td>{item.tipoParceiro || "-"}</td>
                  <td>{item.documento || "-"}</td>
                  <td>
                    <span
                      className={
                        item.situacao === "ativo"
                          ? "partner-table__badge partner-table__badge--active"
                          : "partner-table__badge"
                      }
                    >
                      {item.situacao || "-"}
                    </span>
                  </td>
                  <td className="partner-table__actions">
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
