import type { Permission } from "../../pages/Sys/PermissionsPage";

type PermissionFormProps = {
  value: Permission;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: Permission) => void;
  onSave: () => void;
  onReset: () => void;
};

const moduleOptions = [
  "sys",
  "core",
  "fi",
  "mm",
  "rh",
  "ps",
  "pp",
  "qm",
  "grc",
  "portal",
  "auditoria",
  "fiscal",
  "sd",
  "sm",
  "am",
  "bi",
];

const actionOptions = [
  { value: "create", label: "Create" },
  { value: "read", label: "Read" },
  { value: "update", label: "Update" },
  { value: "delete", label: "Delete" },
  { value: "execute", label: "Execute" },
];

export default function PermissionForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: PermissionFormProps) {
  const canSave =
    value.nome.trim() !== "" &&
    value.modulo.trim() !== "" &&
    value.recurso.trim() !== "" &&
    value.acao.trim() !== "";

  function update<K extends keyof Permission>(
    field: K,
    fieldValue: Permission[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="permission-form">
      <div className="permission-form__head">
        <div>
          <h3 className="permission-form__title">
            {editing ? "Editar permissao" : "Nova permissao"}
          </h3>
          <p className="permission-form__subtitle">
            Configure a chave funcional de autorizacao por modulo, recurso e acao.
          </p>
          {editing && value.id ? (
            <p className="permission-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="permission-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="permission-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="permission-form__grid">
        <label className="permission-form__field permission-form__field--span-2">
          <span>Nome</span>
          <input
            value={value.nome}
            onChange={(event) => update("nome", event.target.value)}
            placeholder="Ex.: sys:usuarios:read"
            disabled={!canEditFields}
          />
        </label>

        <label className="permission-form__field permission-form__field--span-2">
          <span>Descricao</span>
          <textarea
            value={value.descricao}
            onChange={(event) => update("descricao", event.target.value)}
            placeholder="Explique a finalidade operacional desta permissao"
            maxLength={255}
            disabled={!canEditFields}
          />
        </label>

        <label className="permission-form__field">
          <span>Modulo</span>
          <select
            value={value.modulo}
            onChange={(event) => update("modulo", event.target.value)}
            disabled={!canEditFields}
          >
            {moduleOptions.map((option) => (
              <option key={option} value={option}>
                {option.toUpperCase()}
              </option>
            ))}
          </select>
        </label>

        <label className="permission-form__field">
          <span>Recurso</span>
          <input
            value={value.recurso}
            onChange={(event) =>
              update("recurso", event.target.value.toLowerCase())
            }
            placeholder="usuarios"
            disabled={!canEditFields}
          />
        </label>

        <label className="permission-form__field">
          <span>Acao</span>
          <select
            value={value.acao}
            onChange={(event) => update("acao", event.target.value)}
            disabled={!canEditFields}
          >
            {actionOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>
      </div>

      <button
        type="button"
        className="permission-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar permissao"}
      </button>
    </aside>
  );
}
