import type {
  Notification,
  UserOption,
} from "../../pages/Portal/NotificationsPage";

type NotificationsTableProps = {
  items: Notification[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  userOptions: UserOption[];
  onSelect: (item: Notification) => void;
  onDelete: (item: Notification) => void;
};

function resolveUserLabel(userId: string, userOptions: UserOption[]) {
  const match = userOptions.find((option) => String(option.id) === userId);
  return match ? match.label : userId ? `Usuario #${userId}` : "-";
}

export default function NotificationsTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  userOptions,
  onSelect,
  onDelete,
}: NotificationsTableProps) {
  return (
    <section className="notifications-table">
      <div className="notifications-table__head">
        <h3 className="notifications-table__title">Notificacoes</h3>
        <span className="notifications-table__meta">{items.length} registros</span>
      </div>

      <div className="notifications-table__wrap">
        <table className="notifications-table__table">
          <thead>
            <tr>
              <th>Notificacao</th>
              <th>Usuario</th>
              <th>Tipo</th>
              <th>Leitura</th>
              <th>Acoes</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={5} className="notifications-table__empty">
                  Carregando notificacoes...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={5} className="notifications-table__empty">
                  Nenhuma notificacao encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? item.titulo}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="notifications-table__identity">
                      <strong>{item.titulo || "-"}</strong>
                      <span>{item.mensagem || "-"}</span>
                    </div>
                  </td>
                  <td>{resolveUserLabel(item.usuario, userOptions)}</td>
                  <td>
                    <span className="notifications-table__badge">
                      {item.tipo || "-"}
                    </span>
                  </td>
                  <td>
                    <div className="notifications-table__details">
                      <span>{item.lida ? "Lida" : "Nao lida"}</span>
                      <span>{item.dataLeitura || "-"}</span>
                    </div>
                  </td>
                  <td className="notifications-table__actions">
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
                      <span className="notifications-table__empty-action">
                        Sem acoes
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
