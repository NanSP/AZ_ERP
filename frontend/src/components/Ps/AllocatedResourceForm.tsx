import type {
  AllocatedResource,
  ProjectAccess,
  ProjectOption,
  TaskAccess,
  TaskOption,
} from "../../pages/Ps/AllocatedResourcesPage";

type AllocatedResourceFormProps = {
  value: AllocatedResource;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  projectOptions: ProjectOption[];
  projectAccess: ProjectAccess;
  taskOptions: TaskOption[];
  taskAccess: TaskAccess;
  onChange: (value: AllocatedResource) => void;
  onSave: () => void;
  onReset: () => void;
};

const resourceTypeOptions = [
  { value: "humano", label: "Humano" },
  { value: "material", label: "Material" },
  { value: "financeiro", label: "Financeiro" },
];

export default function AllocatedResourceForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  projectOptions,
  projectAccess,
  taskOptions,
  taskAccess,
  onChange,
  onSave,
  onReset,
}: AllocatedResourceFormProps) {
  const canSave =
    (value.projeto.trim() !== "" || value.tarefa.trim() !== "") &&
    value.recursoId.trim() !== "";

  function update<K extends keyof AllocatedResource>(
    field: K,
    fieldValue: AllocatedResource[K],
  ) {
    const nextValue = {
      ...value,
      [field]: fieldValue,
    };

    if (field === "quantidade" || field === "valorUnitario") {
      const quantidade = Number(String(nextValue.quantidade).replace(",", "."));
      const valorUnitario = Number(
        String(nextValue.valorUnitario).replace(",", "."),
      );
      const total =
        Number.isFinite(quantidade) && Number.isFinite(valorUnitario)
          ? String(quantidade * valorUnitario)
          : "";
      nextValue.valorTotal = total;
    }

    onChange(nextValue);
  }

  return (
    <aside className="allocated-resource-form">
      <div className="allocated-resource-form__head">
        <div>
          <h3 className="allocated-resource-form__title">
            {editing ? "Editar recurso" : "Novo recurso"}
          </h3>
          <p className="allocated-resource-form__subtitle">
            Cadastre a alocação com projeto, tarefa, tipo e valor total
            calculado.
          </p>
          {editing && value.id ? (
            <p className="allocated-resource-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="allocated-resource-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="allocated-resource-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="allocated-resource-form__grid">
        <label className="allocated-resource-form__field">
          <span>Projeto</span>
          {projectOptions.length > 0 ? (
            <select
              value={value.projeto}
              onChange={(event) => {
                onChange({
                  ...value,
                  projeto: event.target.value,
                  tarefa: "",
                });
              }}
              disabled={!canEditFields}
            >
              <option value="">Não vincular</option>
              {projectOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.projeto}
              onChange={(event) =>
                update("projeto", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do projeto"
              disabled={!canEditFields}
            />
          )}
          {projectAccess === "unavailable" ? (
            <small className="allocated-resource-form__hint">
              Lista indisponível. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="allocated-resource-form__field">
          <span>Tarefa</span>
          {taskOptions.length > 0 ? (
            <select
              value={value.tarefa}
              onChange={(event) => update("tarefa", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Não vincular</option>
              {taskOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.tarefa}
              onChange={(event) =>
                update("tarefa", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID da tarefa"
              disabled={!canEditFields}
            />
          )}
          {taskAccess === "unavailable" ? (
            <small className="allocated-resource-form__hint">
              Lista indisponível. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="allocated-resource-form__field">
          <span>Tipo de recurso</span>
          <select
            value={value.tipoRecurso}
            onChange={(event) => update("tipoRecurso", event.target.value)}
            disabled={!canEditFields}
          >
            {resourceTypeOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="allocated-resource-form__field">
          <span>Recurso ID</span>
          <input
            value={value.recursoId}
            onChange={(event) =>
              update("recursoId", event.target.value.replace(/\D/g, ""))
            }
            placeholder="ID do recurso"
            disabled={!canEditFields}
          />
        </label>

        <label className="allocated-resource-form__field">
          <span>Quantidade</span>
          <input
            value={value.quantidade}
            onChange={(event) =>
              update("quantidade", event.target.value.replace(/[^0-9.,]/g, ""))
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        <label className="allocated-resource-form__field">
          <span>Valor unitário</span>
          <input
            value={value.valorUnitario}
            onChange={(event) =>
              update(
                "valorUnitario",
                event.target.value.replace(/[^0-9.,]/g, ""),
              )
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        <label className="allocated-resource-form__field">
          <span>Valor total</span>
          <input value={value.valorTotal} disabled />
        </label>

        <label className="allocated-resource-form__field">
          <span>Data da alocação</span>
          <input
            type="date"
            value={value.dataAlocacao}
            onChange={(event) => update("dataAlocacao", event.target.value)}
            disabled={!canEditFields}
          />
        </label>
      </div>

      <button
        type="button"
        className="allocated-resource-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alterações"
            : "Criar recurso"}
      </button>
    </aside>
  );
}
