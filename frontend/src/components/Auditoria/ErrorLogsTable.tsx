import type {
  ErrorLogEntry,
  UserOption,
} from "../../pages/Auditoria/ErrorLogsPage";

type ErrorLogsTableProps = {
  items: ErrorLogEntry[];
  loading: boolean;
  users: UserOption[];
};

function resolveUserLabel(userId: string, users: UserOption[]) {
  const match = users.find((item) => String(item.id) === userId);
  return match ? match.label : userId ? `Usuario #${userId}` : "-";
}

export default function ErrorLogsTable({
  items,
  loading,
  users,
}: ErrorLogsTableProps) {
  return (
    <section className="error-logs-table">
      <div className="error-logs-page__panel-head">
        <h3 className="error-logs-page__panel-title">Logs de erro</h3>
        <span className="error-logs-page__panel-subtitle">
          {items.length} registros
        </span>
      </div>

      <div className="error-logs-page__table-wrap">
        <table className="error-logs-page__table">
          <thead>
            <tr>
              <th>Usuario</th>
              <th>Erro</th>
              <th>Contexto</th>
              <th>Criado em</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="error-logs-page__empty">
                  Carregando logs de erro...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="error-logs-page__empty">
                  Nenhum log de erro encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr key={item.id ?? `${item.modulo}-${item.createdAt}`}>
                  <td>{resolveUserLabel(item.usuario, users)}</td>
                  <td>
                    <div className="error-logs-page__identity">
                      <strong>{item.erroCodigo || "-"}</strong>
                      <span>{item.erroMensagem || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="error-logs-page__details">
                      <span>Modulo: {item.modulo || "-"}</span>
                      <span>URL: {item.url || "-"}</span>
                      <span>IP: {item.ipAddress || "-"}</span>
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
