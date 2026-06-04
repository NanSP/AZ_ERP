import { Link } from "react-router-dom";
import { modules } from "../../services/resourceService";
import "./home.css";

export default function Home() {
  return (
    <div className="page">
      <h1>AZ ERP - Sistema Empresarial</h1>
      <p className="page-subtitle">
        Gerencie os modulos do ERP por esta interface web. Selecione um modulo
        abaixo para visualizar, criar, editar ou excluir registros.
      </p>

      <div className="page-section">
        <h2>Modulos Disponiveis</h2>
      </div>

      <ul className="module-list">
        {modules.map((m) => (
          <li key={`${m.schema}-${m.entity}`}>
            <Link to={`/app/module/${m.schema}/${m.entity}`}>
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
        <h3>Como usar</h3>
        <ul className="instructions-list">
          <li>Clique em qualquer modulo para acessar o CRUD completo</li>
          <li>Use o editor JSON para criar ou editar registros</li>
          <li>Os dados sao salvos diretamente no backend Spring Boot</li>
          <li>
            Configure a URL do backend em <code>.env</code> se necessario
          </li>
        </ul>
      </div>
    </div>
  );
}

function getModuleIcon(entity: string): string {
  const icons: { [key: string]: string } = {
    empresas: "EMP",
    parceiros: "PAR",
    contasPagar: "CP",
    contasReceber: "CR",
    planoContas: "PC",
    compras: "COM",
    compraItens: "ITM",
    estoques: "EST",
    movimentacoes: "MOV",
    colaboradores: "COL",
    folhaDePagamento: "FP",
    projetos: "PRO",
    tarefas: "TAR",
    ordemProducao: "OP",
    inspecoes: "INS",
    auditorias: "AUD",
    notificacoes: "NOT",
    logAcoes: "LOG",
    logErros: "ERR",
    documentos: "DOC",
    relatorios: "REL",
  };
  return icons[entity] || "MOD";
}
