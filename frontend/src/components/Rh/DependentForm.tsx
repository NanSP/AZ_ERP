import type { Dependent } from "../../pages/Rh/DependentsPage";

type EmployeeOption = {
  id: number;
  label: string;
};

type EmployeeAccess = "idle" | "loaded" | "unavailable";

type DependentFormProps = {
  value: Dependent;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  employeeOptions: EmployeeOption[];
  employeeAccess: EmployeeAccess;
  onChange: (value: Dependent) => void;
  onSave: () => void;
  onReset: () => void;
};

const relationshipOptions = [
  { value: "", label: "Selecione" },
  { value: "filho", label: "Filho" },
  { value: "filha", label: "Filha" },
  { value: "conjuge", label: "Conjuge" },
  { value: "enteado", label: "Enteado" },
  { value: "enteada", label: "Enteada" },
  { value: "outro", label: "Outro" },
];

export default function DependentForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  employeeOptions,
  employeeAccess,
  onChange,
  onSave,
  onReset,
}: DependentFormProps) {
  const canSave =
    value.colaborador.trim() !== "" &&
    value.nome.trim() !== "";

  function update<K extends keyof Dependent>(
    field: K,
    fieldValue: Dependent[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="dependent-form">
      <div className="dependent-form__head">
        <div>
          <h3 className="dependent-form__title">
            {editing ? "Editar dependente" : "Novo dependente"}
          </h3>
          <p className="dependent-form__subtitle">
            Vincule o dependente ao colaborador e informe dados cadastrais
            basicos.
          </p>
          {editing && value.id ? (
            <p className="dependent-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="dependent-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="dependent-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="dependent-form__grid">
        <label className="dependent-form__field">
          <span>Colaborador</span>
          {employeeOptions.length > 0 ? (
            <select
              value={value.colaborador}
              onChange={(event) => update("colaborador", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione um colaborador</option>
              {employeeOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.colaborador}
              onChange={(event) =>
                update("colaborador", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do colaborador"
              disabled={!canEditFields}
            />
          )}
          {employeeAccess === "unavailable" ? (
            <small className="dependent-form__hint">
              Lista indisponivel. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="dependent-form__field">
          <span>Nome</span>
          <input
            value={value.nome}
            onChange={(event) => update("nome", event.target.value)}
            placeholder="Nome do dependente"
            disabled={!canEditFields}
          />
        </label>

        <label className="dependent-form__field">
          <span>Data de nascimento</span>
          <input
            type="date"
            value={value.dataNascimento}
            onChange={(event) => update("dataNascimento", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="dependent-form__field">
          <span>Parentesco</span>
          <select
            value={value.parentesco}
            onChange={(event) => update("parentesco", event.target.value)}
            disabled={!canEditFields}
          >
            {relationshipOptions.map((option) => (
              <option key={option.value || "empty"} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="dependent-form__field dependent-form__field--span-2">
          <span>CPF</span>
          <input
            value={value.cpf}
            onChange={(event) =>
              update("cpf", event.target.value.replace(/\D/g, "").slice(0, 11))
            }
            placeholder="Somente numeros"
            disabled={!canEditFields}
          />
        </label>
      </div>

      <button
        type="button"
        className="dependent-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar dependente"}
      </button>
    </aside>
  );
}
