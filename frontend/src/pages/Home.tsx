import { Link } from "react-router-dom";
import { modules } from "../services/resourceService";
import "./pages.css";

export default function Home() {
  return (
    <div className="page">
      <h1>🏢 AZ ERP - Sistema Empresarial</h1>
      <p className="page-subtitle">
        Gerencie todos os módulos do seu ERP através desta interface web.
        Selecione um módulo abaixo para visualizar, criar, editar ou excluir
        registros.
      </p>

      <div className="page-section">
        <h2>📋 Módulos Disponíveis</h2>
      </div>

      <ul className="module-list">
        {modules.map((m) => (
          <li key={`${m.schema}-${m.entity}`}>
            <Link to={`/module/${m.schema}/${m.entity}`}>
              <div className="module-card-content">
                <span className="module-icon">{getModuleIcon(m.entity)}</span>
                <div>
                  <strong>{m.label}</strong>
                  <br />
                  <small className="module-metadata">
                    {m.schema}/{m.entity}
                  </small>
                </div>
              </div>
            </Link>
          </li>
        ))}
      </ul>

      <div className="section-card">
        <h3>💡 Como usar</h3>
        <ul className="instructions-list">
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
