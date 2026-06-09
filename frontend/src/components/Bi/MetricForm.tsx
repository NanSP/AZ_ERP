import type { MetricRecord } from "../../pages/Bi/MetricsPage";

type MetricFormProps = {
  value: MetricRecord;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: MetricRecord) => void;
  onSave: () => void;
  onReset: () => void;
};

const categoryOptions = [
  { value: "financeira", label: "Financeira" },
  { value: "operacional", label: "Operacional" },
  { value: "comercial", label: "Comercial" },
  { value: "qualidade", label: "Qualidade" },
  { value: "estrategica", label: "Estrategica" },
];

export default function MetricForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: MetricFormProps) {
  const formulaRequired =
    value.categoria === "financeira" || value.categoria === "operacional";
  const metaNumber =
    value.meta.trim() === "" ? 0 : Number(value.meta.replace(",", "."));
  const canSave =
    value.nome.trim() !== "" &&
    value.categoria.trim() !== "" &&
    (!formulaRequired || value.formula.trim() !== "") &&
    (value.meta.trim() === "" ||
      (!Number.isNaN(metaNumber) && metaNumber >= 0));

  function update<K extends keyof MetricRecord>(
    field: K,
    fieldValue: MetricRecord[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="metric-form">
      <div className="metric-form__head">
        <div>
          <h3 className="metric-form__title">
            {editing ? "Editar metrica" : "Nova metrica"}
          </h3>
          <p className="metric-form__subtitle">
            Estruture o indicador com contexto, fórmula, unidade e meta.
          </p>
          {editing && value.id ? (
            <p className="metric-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="metric-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="metric-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="metric-form__grid">
        <label className="metric-form__field">
          <span>Nome</span>
          <input
            value={value.nome}
            onChange={(event) => update("nome", event.target.value)}
            placeholder="Receita liquida"
            disabled={!canEditFields}
          />
        </label>

        <label className="metric-form__field">
          <span>Categoria</span>
          <select
            value={value.categoria}
            onChange={(event) => update("categoria", event.target.value)}
            disabled={!canEditFields}
          >
            {categoryOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="metric-form__field metric-form__field--span-2">
          <span>Descrição</span>
          <input
            value={value.descricao}
            onChange={(event) => update("descricao", event.target.value)}
            placeholder="Explique o objetivo e a leitura da metrica"
            disabled={!canEditFields}
          />
        </label>

        <label className="metric-form__field metric-form__field--span-2">
          <span>Fórmula</span>
          <textarea
            value={value.formula}
            onChange={(event) => update("formula", event.target.value)}
            placeholder="receita_total - descontos"
            disabled={!canEditFields}
            rows={6}
          />
          {formulaRequired ? (
            <small className="metric-form__hint">
              Fórmula obrigatoria para categorias financeira e operacional.
            </small>
          ) : null}
        </label>

        <label className="metric-form__field">
          <span>Unidade de medida</span>
          <input
            value={value.unidadeMedida}
            onChange={(event) => update("unidadeMedida", event.target.value)}
            placeholder="R$, %, un"
            disabled={!canEditFields}
          />
        </label>

        <label className="metric-form__field">
          <span>Meta</span>
          <input
            value={value.meta}
            onChange={(event) =>
              update("meta", event.target.value.replace(/[^0-9,.-]/g, ""))
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>
      </div>

      <button
        type="button"
        className="metric-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar metrica"}
      </button>
    </aside>
  );
}
