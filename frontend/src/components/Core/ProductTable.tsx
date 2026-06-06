import type { Product } from "../../pages/Core/ProductsPage";

type ProductTableProps = {
  items: Product[];
  loading: boolean;
  selectedId?: number;
  onSelect: (item: Product) => void;
  onDelete: (item: Product) => void;
};

export default function ProductTable({
  items,
  loading,
  selectedId,
  onSelect,
  onDelete,
}: ProductTableProps) {
  return (
    <section className="product-table">
      <div className="product-table__head">
        <h3 className="product-table__title">Lista de produtos</h3>
        <span className="product-table__meta">{items.length} registros</span>
      </div>

      <div className="product-table__wrap">
        <table className="product-table__table">
          <thead>
            <tr>
              <th>Codigo</th>
              <th>Nome</th>
              <th>Tipo</th>
              <th>Unidade</th>
              <th>Situacao</th>
              <th>Acoes</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={6} className="product-table__empty">
                  Carregando produtos...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={6} className="product-table__empty">
                  Nenhum produto encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.codigo}-${item.nome}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>{item.codigo || "-"}</td>
                  <td>
                    <div className="product-table__identity">
                      <strong>{item.nome || "-"}</strong>
                      <span>{item.codigoBarras || "Sem codigo de barras"}</span>
                    </div>
                  </td>
                  <td>{item.tipoItem || "-"}</td>
                  <td>{item.unidadeMedida || "-"}</td>
                  <td>
                    <span
                      className={
                        item.situacao === "ativo"
                          ? "product-table__badge product-table__badge--active"
                          : "product-table__badge"
                      }
                    >
                      {item.situacao || "-"}
                    </span>
                  </td>
                  <td className="product-table__actions">
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
