import type { InventoryRecord } from "../../pages/Mm/InventoriesPage";

type InventoryFormProps = {
  value: InventoryRecord;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: InventoryRecord) => void;
  onSave: () => void;
  onReset: () => void;
};

const inventoryTypeOptions = [
  { value: "anual", label: "Anual" },
  { value: "rotativo", label: "Rotativo" },
  { value: "amostragem", label: "Amostragem" },
];

const statusOptions = [
  { value: "planejado", label: "Planejado" },
  { value: "em_andamento", label: "Em andamento" },
  { value: "concluido", label: "Concluido" },
  { value: "cancelado", label: "Cancelado" },
];

export default function InventoryForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: InventoryFormProps) {
  const today = new Date().toISOString().slice(0, 10);
  const needsEndDate =
    value.status === "concluido" || value.status === "cancelado";
  const canSave =
    value.dataInicio.trim() !== "" &&
    value.tipoInventario.trim() !== "" &&
    value.status.trim() !== "" &&
    (!value.dataFim || value.dataFim >= value.dataInicio) &&
    (!needsEndDate || value.dataFim.trim() !== "") &&
    (value.status !== "planejado" || value.dataFim.trim() === "") &&
    (value.status !== "em_andamento" || value.dataInicio <= today) &&
    (value.status !== "concluido" ||
      value.dataFim.trim() === "" ||
      value.dataFim <= today);

  function update<K extends keyof InventoryRecord>(
    field: K,
    fieldValue: InventoryRecord[K],
  ) {
    const nextValue = {
      ...value,
      [field]: fieldValue,
    };

    if (field === "status" && fieldValue === "planejado") {
      nextValue.dataFim = "";
    }

    onChange(nextValue);
  }

  return (
    <aside className="inventory-form">
      <div className="inventory-form__head">
        <div>
          <h3 className="inventory-form__title">
            {editing ? "Editar inventario" : "Novo inventario"}
          </h3>
          <p className="inventory-form__subtitle">
            Organize o periodo da contagem, defina o tipo de inventario e
            acompanhe a etapa operacional do ciclo.
          </p>
          {editing && value.id ? (
            <p className="inventory-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="inventory-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="inventory-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="inventory-form__grid">
        <label className="inventory-form__field">
          <span>Data de inicio</span>
          <input
            type="date"
            value={value.dataInicio}
            onChange={(event) => update("dataInicio", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="inventory-form__field">
          <span>Data de fim</span>
          <input
            type="date"
            value={value.dataFim}
            onChange={(event) => update("dataFim", event.target.value)}
            disabled={!canEditFields || value.status === "planejado"}
          />
        </label>

        <label className="inventory-form__field">
          <span>Tipo de inventario</span>
          <select
            value={value.tipoInventario}
            onChange={(event) =>
              update("tipoInventario", event.target.value)
            }
            disabled={!canEditFields}
          >
            {inventoryTypeOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="inventory-form__field">
          <span>Status</span>
          <select
            value={value.status}
            onChange={(event) => update("status", event.target.value)}
            disabled={!canEditFields}
          >
            {statusOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="inventory-form__field inventory-form__field--span-2">
          <span>Observacoes</span>
          <textarea
            value={value.observacoes}
            onChange={(event) => update("observacoes", event.target.value)}
            placeholder="Escopo da contagem, areas impactadas, criterio amostral ou observacoes operacionais"
            disabled={!canEditFields}
            rows={5}
          />
        </label>
      </div>

      <button
        type="button"
        className="inventory-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar inventario"}
      </button>
    </aside>
  );
}
