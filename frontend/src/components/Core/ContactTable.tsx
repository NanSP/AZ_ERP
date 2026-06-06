import type { Contact } from "../../pages/Core/ContactsPage";

type ContactTableProps = {
  items: Contact[];
  loading: boolean;
  selectedId?: number;
  onSelect: (item: Contact) => void;
  onDelete: (item: Contact) => void;
};

export default function ContactTable({
  items,
  loading,
  selectedId,
  onSelect,
  onDelete,
}: ContactTableProps) {
  return (
    <section className="contact-table">
      <div className="contact-table__head">
        <h3 className="contact-table__title">Lista de contatos</h3>
        <span className="contact-table__meta">{items.length} registros</span>
      </div>

      <div className="contact-table__wrap">
        <table className="contact-table__table">
          <thead>
            <tr>
              <th>Entidade</th>
              <th>Tipo</th>
              <th>Valor</th>
              <th>Principal</th>
              <th>Acoes</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={5} className="contact-table__empty">
                  Carregando contatos...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={5} className="contact-table__empty">
                  Nenhum contato encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.entidadeTipo}-${item.valor}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="contact-table__identity">
                      <strong>{item.entidadeTipo || "-"}</strong>
                      <span>#{item.entidadeId || "-"}</span>
                    </div>
                  </td>
                  <td>{item.tipoContato || "-"}</td>
                  <td>{item.valor || "-"}</td>
                  <td>
                    <span
                      className={
                        item.principal
                          ? "contact-table__badge contact-table__badge--active"
                          : "contact-table__badge"
                      }
                    >
                      {item.principal ? "Principal" : "Secundario"}
                    </span>
                  </td>
                  <td className="contact-table__actions">
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
