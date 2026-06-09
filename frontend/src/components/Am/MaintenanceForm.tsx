import type {
  AssetAccess,
  AssetOption,
  EmployeeAccess,
  EmployeeOption,
  MaintenanceEntry,
} from "../../pages/Am/MaintenancesPage";

type MaintenanceFormProps = {
  value: MaintenanceEntry;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  assetOptions: AssetOption[];
  assetAccess: AssetAccess;
  employeeOptions: EmployeeOption[];
  employeeAccess: EmployeeAccess;
  onChange: (value: MaintenanceEntry) => void;
  onSave: () => void;
  onReset: () => void;
};

const maintenanceTypeOptions = [
  { value: "", label: "Selecione" },
  { value: "preventiva", label: "Preventiva" },
  { value: "corretiva", label: "Corretiva" },
  { value: "preditiva", label: "Preditiva" },
];

export default function MaintenanceForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  assetOptions,
  assetAccess,
  employeeOptions,
  employeeAccess,
  onChange,
  onSave,
  onReset,
}: MaintenanceFormProps) {
  const canSave =
    value.ativo.trim() !== "" &&
    value.tipoManutencao.trim() !== "" &&
    !(value.dataExecucao.trim() !== "" && value.tecnico.trim() === "");

  function update<K extends keyof MaintenanceEntry>(
    field: K,
    fieldValue: MaintenanceEntry[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="maintenance-form">
      <div className="maintenance-form__head">
        <div>
          <h3 className="maintenance-form__title">
            {editing ? "Editar manutenção" : "Nova manutenção"}
          </h3>
          <p className="maintenance-form__subtitle">
            Registre a manutenção do ativo com custos, técnico e cronologia de
            execução.
          </p>
          {editing && value.id ? (
            <p className="maintenance-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="maintenance-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="maintenance-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="maintenance-form__grid">
        <label className="maintenance-form__field">
          <span>Ativo</span>
          {assetOptions.length > 0 ? (
            <select
              value={value.ativo}
              onChange={(event) => update("ativo", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione um ativo</option>
              {assetOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.ativo}
              onChange={(event) =>
                update("ativo", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do ativo"
              disabled={!canEditFields}
            />
          )}
          {assetAccess === "unavailable" ? (
            <small className="maintenance-form__hint">
              Lista indisponível. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="maintenance-form__field">
          <span>Tipo de manutenção</span>
          <select
            value={value.tipoManutencao}
            onChange={(event) => update("tipoManutencao", event.target.value)}
            disabled={!canEditFields}
          >
            {maintenanceTypeOptions.map((option) => (
              <option key={option.value || "empty"} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="maintenance-form__field">
          <span>Data de solicitação</span>
          <input
            type="date"
            value={value.dataSolicitacao}
            onChange={(event) => update("dataSolicitacao", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="maintenance-form__field">
          <span>Data de execução</span>
          <input
            type="date"
            value={value.dataExecucao}
            onChange={(event) => update("dataExecucao", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="maintenance-form__field maintenance-form__field--span-2">
          <span>Descrição</span>
          <input
            value={value.descricao}
            onChange={(event) => update("descricao", event.target.value)}
            placeholder="Descreva a manutenção"
            disabled={!canEditFields}
          />
        </label>

        <label className="maintenance-form__field">
          <span>Custo de mao de obra</span>
          <input
            value={value.custoMaoObra}
            onChange={(event) =>
              update(
                "custoMaoObra",
                event.target.value.replace(/[^0-9.,]/g, ""),
              )
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        <label className="maintenance-form__field">
          <span>Custo de material</span>
          <input
            value={value.custoMaterial}
            onChange={(event) =>
              update(
                "custoMaterial",
                event.target.value.replace(/[^0-9.,]/g, ""),
              )
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        <label className="maintenance-form__field">
          <span>Técnico</span>
          {employeeOptions.length > 0 ? (
            <select
              value={value.tecnico}
              onChange={(event) => update("tecnico", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Não vincular</option>
              {employeeOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.tecnico}
              onChange={(event) =>
                update("tecnico", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do tecnico"
              disabled={!canEditFields}
            />
          )}
          {employeeAccess === "unavailable" ? (
            <small className="maintenance-form__hint">
              Lista indisponível. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <div className="maintenance-form__summary">
          <span>Custo total: {value.custoTotal || "0"}</span>
          <span>
            Execução {value.dataExecucao ? "informada" : "não informada"}
          </span>
        </div>
      </div>

      <button
        type="button"
        className="maintenance-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alterações"
            : "Criar manutenção"}
      </button>
    </aside>
  );
}
