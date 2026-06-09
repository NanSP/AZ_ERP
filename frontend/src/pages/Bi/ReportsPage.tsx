import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import ReportForm from "../../components/Bi/ReportForm";
import ReportsTable from "../../components/Bi/ReportsTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./reports-page.css";

export type ReportRecord = {
  id?: number;
  nome: string;
  descricao: string;
  tipoRelatorio: string;
  querySql: string;
  parametros: string;
  createdAt?: string;
};

type ReportsPageProps = {
  embedded?: boolean;
};

const reportsResource = {
  schema: "bi",
  entity: "relatorios",
  label: "Relatorios",
  description: "Relatorios analiticos e gerenciais.",
} as const;

const emptyReport: ReportRecord = {
  nome: "",
  descricao: "",
  tipoRelatorio: "tabela",
  querySql: "SELECT * FROM bi.metricas LIMIT 100",
  parametros: '{\n  "periodo": "2026-06"\n}',
};

function stringifyObject(value: unknown) {
  if (value == null) {
    return "";
  }

  try {
    return JSON.stringify(value, null, 2);
  } catch {
    return "";
  }
}

function normalizeReport(data: Record<string, unknown>): ReportRecord {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    nome: String(data.nome ?? ""),
    descricao: String(data.descricao ?? ""),
    tipoRelatorio: String(data.tipoRelatorio ?? "tabela"),
    querySql: String(data.querySql ?? ""),
    parametros: stringifyObject(data.parametros),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function parseJsonOrNull(value: string, fieldLabel: string) {
  const trimmed = value.trim();

  if (!trimmed) {
    return null;
  }

  try {
    return JSON.parse(trimmed) as Record<string, unknown>;
  } catch {
    throw new Error(`${fieldLabel} precisa conter um JSON valido.`);
  }
}

function toRequestPayload(item: ReportRecord) {
  return {
    nome: item.nome.trim() || null,
    descricao: item.descricao.trim() || null,
    tipoRelatorio: item.tipoRelatorio.trim() || null,
    querySql: item.querySql.trim() || null,
    parametros: parseJsonOrNull(item.parametros, "Parametros"),
  };
}

function getErrorMessage(error: unknown, fallback: string) {
  if (error instanceof AxiosError) {
    const message =
      error.response?.data?.message ?? error.response?.data?.error;
    if (typeof message === "string" && message.trim()) {
      return message;
    }
  }

  if (error instanceof Error && error.message.trim()) {
    return error.message;
  }

  return fallback;
}

function escapeCsv(value: string) {
  const normalized = value.replace(/"/g, '""');
  return `"${normalized}"`;
}

function downloadTextFile(content: string, fileName: string, type: string) {
  const blob = new Blob([content], { type });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = fileName;
  link.click();
  URL.revokeObjectURL(url);
}

function exportReportsCsv(items: ReportRecord[]) {
  const header = [
    "ID",
    "Nome",
    "Descricao",
    "Tipo",
    "QuerySql",
    "Parametros",
    "CreatedAt",
  ];

  const rows = items.map((item) => [
    String(item.id ?? ""),
    item.nome,
    item.descricao,
    item.tipoRelatorio,
    item.querySql,
    item.parametros,
    item.createdAt ?? "",
  ]);

  const csv = [header, ...rows]
    .map((row) => row.map((cell) => escapeCsv(cell)).join(","))
    .join("\n");

  downloadTextFile(csv, "relatorios-bi.csv", "text/csv;charset=utf-8;");
}

function escapeHtml(value: string) {
  return value
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#39;");
}

function exportReportsPdf(items: ReportRecord[]) {
  const popup = window.open("", "_blank", "width=1200,height=900");

  if (!popup) {
    window.alert("Não foi possível abrir a janela de impressão do PDF.");
    return;
  }

  const rows = items
    .map(
      (item) => `
        <tr>
          <td>${escapeHtml(String(item.id ?? "-"))}</td>
          <td>${escapeHtml(item.nome || "-")}</td>
          <td>${escapeHtml(item.tipoRelatorio || "-")}</td>
          <td>${escapeHtml(item.descricao || "-")}</td>
          <td><pre>${escapeHtml(item.querySql || "-")}</pre></td>
          <td><pre>${escapeHtml(item.parametros || "-")}</pre></td>
        </tr>`,
    )
    .join("");

  popup.document.write(`<!DOCTYPE html>
  <html lang="pt-BR">
    <head>
      <meta charset="utf-8" />
      <title>Relatorios BI</title>
      <style>
        body { font-family: Arial, sans-serif; padding: 24px; color: #111; }
        h1 { margin-bottom: 8px; }
        p { color: #555; margin-top: 0; }
        table { width: 100%; border-collapse: collapse; margin-top: 24px; }
        th, td { border: 1px solid #dcdcdc; padding: 10px; vertical-align: top; text-align: left; }
        th { background: #f3f3f3; }
        pre { margin: 0; white-space: pre-wrap; word-break: break-word; font-family: Consolas, monospace; font-size: 11px; }
      </style>
    </head>
    <body>
      <h1>Relatorios BI</h1>
      <p>Exportado em ${new Date().toLocaleString("pt-BR")}</p>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Tipo</th>
            <th>Descricao</th>
            <th>Query SQL</th>
            <th>Parametros</th>
          </tr>
        </thead>
        <tbody>${rows}</tbody>
      </table>
    </body>
  </html>`);
  popup.document.close();
  popup.focus();
  popup.print();
}

export default function ReportsPage({ embedded = false }: ReportsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<ReportRecord[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<ReportRecord | null>(null);
  const [draft, setDraft] = useState<ReportRecord>(emptyReport);
  const canRead = canAccessResourceAction(session, reportsResource, "read");
  const canCreate = canAccessResourceAction(session, reportsResource, "create");
  const canUpdate = canAccessResourceAction(session, reportsResource, "update");
  const canDelete = canAccessResourceAction(session, reportsResource, "delete");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  const loadReports = useCallback(async () => {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyReport });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("bi", "relatorios");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeReport(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Não foi possível carregar os relatórios."),
      );
    } finally {
      setLoading(false);
    }
  }, [canRead]);

  useEffect(() => {
    void loadReports();
  }, [loadReports]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [
        item.nome,
        item.descricao,
        item.tipoRelatorio,
        item.querySql,
        item.parametros,
      ]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyReport });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: ReportRecord) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: ReportRecord) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil não possui permissão para atualizar relatórios."
          : "Seu perfil não possui permissão para criar relatórios.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("bi", "relatorios", selected.id, payload)
        : await createResource("bi", "relatorios", payload);

      const saved = normalizeReport(response.data as Record<string, unknown>);
      await loadReports();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Relatório atualizado com sucesso."
          : "Relatório criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Não foi possível atualizar o relatório."
            : "Não foi possível criar o relatório.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: ReportRecord) {
    if (!canDelete) {
      setError("Seu perfil não possui permissão para excluir relatórios.");
      return;
    }

    if (!item.id) {
      setError("Não foi possível identificar o relatório para exclusão.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o relatório "${item.nome || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("bi", "relatorios", item.id);
      await loadReports();
      setSuccess("Relatório excluído com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Não foi possível excluir o relatório."));
    } finally {
      setSaving(false);
    }
  }

  function handleExportCsv() {
    exportReportsCsv(filteredItems);
  }

  function handleExportPdf() {
    exportReportsPdf(filteredItems);
  }

  return (
    <div
      className={
        embedded ? "reports-page reports-page--embedded" : "reports-page"
      }
    >
      {!embedded ? (
        <header className="reports-page__header">
          <div>
            <span className="reports-page__eyebrow">BI</span>
            <h2 className="reports-page__title">Relatórios</h2>
            <p className="reports-page__subtitle">
              Estruture consultas gerenciais e exporte a listagem atual em CSV
              ou PDF.
            </p>
          </div>

          <div className="reports-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por nome, tipo ou SQL"
              className="reports-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="reports-page__ghost"
              onClick={handleExportCsv}
              disabled={!canRead || filteredItems.length === 0}
            >
              Exportar CSV
            </button>
            <button
              type="button"
              className="reports-page__ghost"
              onClick={handleExportPdf}
              disabled={!canRead || filteredItems.length === 0}
            >
              Exportar PDF
            </button>
            <button
              type="button"
              className="reports-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo relatório
            </button>
          </div>
        </header>
      ) : (
        <div className="reports-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por nome, tipo ou SQL"
            className="reports-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="reports-page__toolbar-actions">
            <button
              type="button"
              className="reports-page__ghost"
              onClick={() => void loadReports()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="reports-page__ghost"
              onClick={handleExportCsv}
              disabled={!canRead || filteredItems.length === 0}
            >
              CSV
            </button>
            <button
              type="button"
              className="reports-page__ghost"
              onClick={handleExportPdf}
              disabled={!canRead || filteredItems.length === 0}
            >
              PDF
            </button>
            <button
              type="button"
              className="reports-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo relatório
            </button>
          </div>
        </div>
      )}

      {error ? <div className="reports-page__alert">{error}</div> : null}
      {success ? (
        <div className="reports-page__alert reports-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="reports-page__alert reports-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criação desabilitada",
            canDelete ? null : "exclusão desabilitada",
            "exclusão não suportada pelo backend",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : (
        <div className="reports-page__alert reports-page__alert--info">
          Exclusão não suportada pelo backend.
        </div>
      )}

      <div className="reports-page__layout">
        <ReportsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={false}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <ReportForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          onChange={handleChange}
          onSave={() => void handleSave()}
          onReset={handleCreateNew}
        />
      </div>
    </div>
  );
}
