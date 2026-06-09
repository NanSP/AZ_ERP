import type { ReportRecord } from "../../pages/Bi/ReportsPage";

type ReportFormProps = {
  value: ReportRecord;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: ReportRecord) => void;
  onSave: () => void;
  onReset: () => void;
};

const reportTypeOptions = [
  { value: "tabela", label: "Tabela" },
  { value: "grafico", label: "Grafico" },
  { value: "indicador", label: "Indicador" },
  { value: "customizado", label: "Customizado" },
];

export default function ReportForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: ReportFormProps) {
  const sqlRequired =
    value.tipoRelatorio === "tabela" ||
    value.tipoRelatorio === "grafico" ||
    value.tipoRelatorio === "customizado";
  const canSave =
    value.nome.trim() !== "" &&
    value.tipoRelatorio.trim() !== "" &&
    (!sqlRequired || value.querySql.trim() !== "");

  function update<K extends keyof ReportRecord>(
    field: K,
    fieldValue: ReportRecord[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="report-form">
      <div className="report-form__head">
        <div>
          <h3 className="report-form__title">
            {editing ? "Editar relatorio" : "Novo relatorio"}
          </h3>
          <p className="report-form__subtitle">
            Defina o tipo, a query SQL e os parametros operacionais do
            relatório.
          </p>
          {editing && value.id ? (
            <p className="report-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="report-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="report-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="report-form__grid">
        <label className="report-form__field">
          <span>Nome</span>
          <input
            value={value.nome}
            onChange={(event) => update("nome", event.target.value)}
            placeholder="Relatorio de performance"
            disabled={!canEditFields}
          />
        </label>

        <label className="report-form__field">
          <span>Tipo</span>
          <select
            value={value.tipoRelatorio}
            onChange={(event) => update("tipoRelatorio", event.target.value)}
            disabled={!canEditFields}
          >
            {reportTypeOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="report-form__field report-form__field--span-2">
          <span>Descrição</span>
          <input
            value={value.descricao}
            onChange={(event) => update("descricao", event.target.value)}
            placeholder="Explique o objetivo do relatório"
            disabled={!canEditFields}
          />
        </label>

        <label className="report-form__field report-form__field--span-2">
          <span>Query SQL</span>
          <textarea
            value={value.querySql}
            onChange={(event) => update("querySql", event.target.value)}
            placeholder="SELECT * FROM bi.metricas LIMIT 100"
            disabled={!canEditFields}
            rows={8}
          />
          {sqlRequired ? (
            <small className="report-form__hint">
              Query SQL obrigatória para tabela, gráfico e customizado. O
              backend aceita apenas comandos iniciados por SELECT.
            </small>
          ) : null}
        </label>

        <label className="report-form__field report-form__field--span-2">
          <span>Parametros (JSON)</span>
          <textarea
            value={value.parametros}
            onChange={(event) => update("parametros", event.target.value)}
            placeholder='{"periodo":"2026-06"}'
            disabled={!canEditFields}
            rows={8}
          />
        </label>
      </div>

      <button
        type="button"
        className="report-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar relatorio"}
      </button>
    </aside>
  );
}
