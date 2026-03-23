import { Link } from "react-router-dom";
import { modules } from "../services/resourceService";
import "./pages.css";

export default function Home() {
  return (
    <div className="page">
      <h1>🏢 AZ_ERP - Sistema Empresarial</h1>
      <p style={{ fontSize: "1.1rem", marginBottom: "2rem", color: "#64748b" }}>
        Gerencie todos os módulos do seu ERP através desta interface web.
        Selecione um módulo abaixo para visualizar, criar, editar ou excluir
        registros.
      </p>

      <div style={{ marginBottom: "2rem" }}>
        <h2
          style={{
            color: "#334155",
            borderBottom: "2px solid #e2e8f0",
            paddingBottom: "0.5rem",
          }}
        >
          📋 Módulos Disponíveis
        </h2>
      </div>

      <ul className="module-list">
        {modules.map((m) => (
          <li key={`${m.schema}-${m.entity}`}>
            <Link to={`/module/${m.schema}/${m.entity}`}>
              <div
                style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}
              >
                <span style={{ fontSize: "1.2rem" }}>
                  {getModuleIcon(m.entity)}
                </span>
                <div>
                  <strong>{m.label}</strong>
                  <br />
                  <small style={{ color: "#64748b" }}>
                    {m.schema}/{m.entity}
                  </small>
                </div>
              </div>
            </Link>
          </li>
        ))}
      </ul>

      <div
        style={{
          marginTop: "3rem",
          padding: "1.5rem",
          background: "linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%)",
          borderRadius: "8px",
          border: "1px solid #e2e8f0",
        }}
      >
        <h3 style={{ marginTop: 0, color: "#334155" }}>💡 Como usar</h3>
        <ul style={{ margin: 0, paddingLeft: "1.5rem", color: "#64748b" }}>
          <li>Clique em qualquer módulo para acessar o CRUD completo</li>
          <li>Use o editor JSON para criar ou editar registros</li>
          <li>Os dados são salvos diretamente no backend Spring Boot</li>
          <li>
            Configure a URL do backend em <code>.env</code> se necessário
          </li>
        </ul>
      </div>
    </div>
  );
}

function getModuleIcon(entity: string): string {
  const icons: { [key: string]: string } = {
    empresas: "🏢",
    parceiros: "🤝",
    contasPagar: "💰",
    contasReceber: "💸",
    planoContas: "📊",
    compras: "🛒",
    compraItens: "📦",
    estoques: "📦",
    movimentacoes: "🔄",
    colaboradores: "👥",
    folhaDePagamento: "💼",
    projetos: "📋",
    tarefas: "✅",
    ordemProducao: "🏭",
    inspecoes: "🔍",
    auditorias: "📋",
    notificacoes: "🔔",
    logAcoes: "📝",
    logErros: "⚠️",
    documentos: "📄",
    relatorios: "📈",
  };
  return icons[entity] || "📁";
}
