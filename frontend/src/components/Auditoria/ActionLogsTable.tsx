import type {
  ActionLogEntry,
  UserOption,
} from "../../pages/Auditoria/ActionLogsPage";

type ActionLogsTableProps = {
  items: ActionLogEntry[];
  loading: boolean;
  users: UserOption[];
};

function resolveUserLabel(userId: string, users: UserOption[]) {
  const match = users.find((item) => String(item.id) === userId);
  return match ? match.label : userId ? `Usuario #${userId}` : "-";
}

export default function ActionLogsTable({
  items,
  loading,
  users,
}: ActionLogsTableProps) {
  return (
    <section className="action-logs-table">
      <div className="action-logs-page__panel-head">
        <h3 className="action-logs-page__panel-title">Logs de ação</h3>
        <span className="action-logs-page__panel-subtitle">
          {items.length} registros
        </span>
      </div>

      <div className="action-logs-page__table-wrap">
        <table className="action-logs-page__table">
          <thead>
            <tr>
              <th>Usuário</th>
              <th>Contexto</th>
              <th>Rastreabilidade</th>
              <th>Criado em</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="action-logs-page__empty">
                  Carregando logs de ação...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="action-logs-page__empty">
                  Nenhum log de ação encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr key={item.id ?? `${item.modulo}-${item.createdAt}`}>
                  <td>{resolveUserLabel(item.usuario, users)}</td>
                  <td>
                    <div className="action-logs-page__identity">
                      <strong>{item.acao || "-"}</strong>
                      <span>
                        {item.modulo || "-"} / {item.tabela || "-"}
                      </span>
                      <span>Registro: {item.registroId || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="action-logs-page__details">
                      <span>IP: {item.ipAddress || "-"}</span>
                      <span>User agent: {item.userAgent || "-"}</span>
                    </div>
                  </td>
                  <td>{item.createdAt || "-"}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </section>
  );
}
