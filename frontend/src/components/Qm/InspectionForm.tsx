import type {
  EmployeeAccess,
  EmployeeOption,
  InspectionEntry,
  ProductAccess,
  ProductOption,
} from "../../pages/Qm/InspectionsPage";

type InspectionFormProps = {
  value: InspectionEntry;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  productOptions: ProductOption[];
  productAccess: ProductAccess;
  employeeOptions: EmployeeOption[];
  employeeAccess: EmployeeAccess;
  onChange: (value: InspectionEntry) => void;
  onSave: () => void;
  onReset: () => void;
};

const inspectionTypeOptions = [
  { value: "", label: "Selecione" },
  { value: "recebimento", label: "Recebimento" },
  { value: "processo", label: "Processo" },
  { value: "final", label: "Final" },
  { value: "expedicao", label: "Expedicao" },
];

const resultOptions = [
  { value: "", label: "Selecione" },
  { value: "aprovado", label: "Aprovado" },
  { value: "reprovado", label: "Reprovado" },
  { value: "em_analise", label: "Em analise" },
];

export default function InspectionForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  productOptions,
  productAccess,
  employeeOptions,
  employeeAccess,
  onChange,
  onSave,
  onReset,
}: InspectionFormProps) {
  const canSave =
    value.tipoInspecao.trim() !== "" &&
    value.dataInspecao.trim() !== "" &&
    value.resultado.trim() !== "" &&
    value.quantidadeInspecionada.trim() !== "";

  function update<K extends keyof InspectionEntry>(
    field: K,
    fieldValue: InspectionEntry[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="inspection-form">
      <div className="inspection-form__head">
        <div>
          <h3 className="inspection-form__title">
            {editing ? "Editar inspeção" : "Nova inspeção"}
          </h3>
          <p className="inspection-form__subtitle">
            Registre a inspeção com resultado, quantidades e rastreabilidade do
            lote.
          </p>
          {editing && value.id ? (
            <p className="inspection-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="inspection-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="inspection-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="inspection-form__grid">
        <label className="inspection-form__field">
          <span>Produto</span>
          {productOptions.length > 0 ? (
            <select
              value={value.produto}
              onChange={(event) => update("produto", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Não vincular</option>
              {productOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.produto}
              onChange={(event) =>
                update("produto", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do produto"
              disabled={!canEditFields}
            />
          )}
          {productAccess === "unavailable" ? (
            <small className="inspection-form__hint">
              Lista indisponível. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="inspection-form__field">
          <span>Tipo de inspeção</span>
          <select
            value={value.tipoInspecao}
            onChange={(event) => update("tipoInspecao", event.target.value)}
            disabled={!canEditFields}
          >
            {inspectionTypeOptions.map((option) => (
              <option key={option.value || "empty"} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="inspection-form__field">
          <span>Lote</span>
          <input
            value={value.lote}
            onChange={(event) => update("lote", event.target.value)}
            placeholder="Lote do produto"
            disabled={!canEditFields}
          />
        </label>

        <label className="inspection-form__field">
          <span>Data da inspeção</span>
          <input
            type="date"
            value={value.dataInspecao}
            onChange={(event) => update("dataInspecao", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="inspection-form__field">
          <span>Quantidade inspecionada</span>
          <input
            value={value.quantidadeInspecionada}
            onChange={(event) =>
              update(
                "quantidadeInspecionada",
                event.target.value.replace(/[^0-9.,]/g, ""),
              )
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        <label className="inspection-form__field">
          <span>Quantidade aprovada</span>
          <input
            value={value.quantidadeAprovada}
            onChange={(event) =>
              update(
                "quantidadeAprovada",
                event.target.value.replace(/[^0-9.,]/g, ""),
              )
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        <label className="inspection-form__field">
          <span>Quantidade reprovada</span>
          <input
            value={value.quantidadeReprovada}
            onChange={(event) =>
              update(
                "quantidadeReprovada",
                event.target.value.replace(/[^0-9.,]/g, ""),
              )
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        <label className="inspection-form__field">
          <span>Resultado</span>
          <select
            value={value.resultado}
            onChange={(event) => update("resultado", event.target.value)}
            disabled={!canEditFields}
          >
            {resultOptions.map((option) => (
              <option key={option.value || "empty"} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="inspection-form__field">
          <span>Inspetor</span>
          {employeeOptions.length > 0 ? (
            <select
              value={value.inspetor}
              onChange={(event) => update("inspetor", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Nao vincular</option>
              {employeeOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.inspetor}
              onChange={(event) =>
                update("inspetor", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do inspetor"
              disabled={!canEditFields}
            />
          )}
          {employeeAccess === "unavailable" ? (
            <small className="inspection-form__hint">
              Lista indisponível. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="inspection-form__field inspection-form__field--span-2">
          <span>Observações</span>
          <input
            value={value.observacoes}
            onChange={(event) => update("observacoes", event.target.value)}
            placeholder="Observações da inspeção"
            disabled={!canEditFields}
          />
        </label>
      </div>

      <button
        type="button"
        className="inspection-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar inspecao"}
      </button>
    </aside>
  );
}
