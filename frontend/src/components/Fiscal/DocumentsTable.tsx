import type {
  ClientOption,
  DocumentEntry,
} from "../../pages/Fiscal/DocumentsPage";

type DocumentsTableProps = {
  items: DocumentEntry[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  clients: ClientOption[];
  onSelect: (item: DocumentEntry) => void;
  onDelete: (item: DocumentEntry) => void;
};

function resolveClientLabel(clientId: string, clients: ClientOption[]) {
  const match = clients.find((option) => String(option.id) === clientId);
  return match ? match.label : clientId ? `Cliente #${clientId}` : "-";
}

export default function DocumentsTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  clients,
  onSelect,
  onDelete,
}: DocumentsTableProps) {
  return (
    <section className="documents-table">
      <div className="documents-page__panel-head">
        <h3 className="documents-page__panel-title">Documentos</h3>
        <span className="documents-page__panel-subtitle">
          {items.length} registros
        </span>
      </div>

      <div className="documents-page__table-wrap">
        <table className="documents-page__table">
          <thead>
            <tr>
              <th>Documento</th>
              <th>Relacionamentos</th>
              <th>Valor</th>
              <th>Status</th>
              <th>Ações</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={5} className="documents-page__empty">
                  Carregando documentos...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={5} className="documents-page__empty">
                  Nenhum documento encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.tipoDocumento}-${item.numero}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="documents-page__identity">
                      <strong>
                        {item.tipoDocumento || "-"} {item.numero || "-"}
                      </strong>
                      <span>Série: {item.serie || "-"}</span>
                      <span>Emissão: {item.dataEmissao || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="documents-page__details">
                      <span>Pedido: {item.pedido || "-"}</span>
                      <span>
                        Cliente: {resolveClientLabel(item.cliente, clients)}
                      </span>
                    </div>
                  </td>
                  <td>{item.valorTotal || "-"}</td>
                  <td>
                    <div className="documents-page__details">
                      <span>{item.status || "-"}</span>
                      <span>Chave: {item.chaveAcesso || "-"}</span>
                    </div>
                  </td>
                  <td className="documents-page__actions">
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
                      <span className="documents-page__empty-action">
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
