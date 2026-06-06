import type { Address } from "../../pages/Core/AddressesPage";

type AddressTableProps = {
  items: Address[];
  loading: boolean;
  selectedId?: number;
  onSelect: (item: Address) => void;
  onDelete: (item: Address) => void;
};

export default function AddressTable({
  items,
  loading,
  selectedId,
  onSelect,
  onDelete,
}: AddressTableProps) {
  return (
    <section className="address-table">
      <div className="address-table__head">
        <h3 className="address-table__title">Lista de enderecos</h3>
        <span className="address-table__meta">{items.length} registros</span>
      </div>

      <div className="address-table__wrap">
        <table className="address-table__table">
          <thead>
            <tr>
              <th>Entidade</th>
              <th>Tipo</th>
              <th>Local</th>
              <th>Principal</th>
              <th>Acoes</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={5} className="address-table__empty">
                  Carregando enderecos...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={5} className="address-table__empty">
                  Nenhum endereco encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.entidadeTipo}-${item.cep}-${item.numero}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="address-table__identity">
                      <strong>{item.entidadeTipo || "-"}</strong>
                      <span>#{item.entidadeId || "-"}</span>
                    </div>
                  </td>
                  <td>{item.tipoEndereco || "-"}</td>
                  <td>
                    <div className="address-table__identity">
                      <strong>{item.logradouro || "Sem logradouro"}</strong>
                      <span>
                        {[item.cidade, item.uf, item.cep]
                          .filter(Boolean)
                          .join(" - ") || "Sem cidade/UF/CEP"}
                      </span>
                    </div>
                  </td>
                  <td>
                    <span
                      className={
                        item.principal
                          ? "address-table__badge address-table__badge--active"
                          : "address-table__badge"
                      }
                    >
                      {item.principal ? "Principal" : "Secundario"}
                    </span>
                  </td>
                  <td className="address-table__actions">
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
