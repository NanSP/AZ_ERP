import type {
  PartnerAccess,
  PartnerOption,
  Project,
  UserAccess,
  UserOption,
} from "../../pages/Ps/ProjectsPage";

type ProjectFormProps = {
  value: Project;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  partnerOptions: PartnerOption[];
  partnerAccess: PartnerAccess;
  userOptions: UserOption[];
  userAccess: UserAccess;
  onChange: (value: Project) => void;
  onSave: () => void;
  onReset: () => void;
};

const statusOptions = [
  { value: "planejado", label: "Planejado" },
  { value: "em_andamento", label: "Em andamento" },
  { value: "concluido", label: "Concluido" },
  { value: "cancelado", label: "Cancelado" },
];

const priorityOptions = [
  { value: "1", label: "1 - Baixa" },
  { value: "2", label: "2" },
  { value: "3", label: "3 - Media" },
  { value: "4", label: "4" },
  { value: "5", label: "5 - Alta" },
];

export default function ProjectForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  partnerOptions,
  partnerAccess,
  userOptions,
  userAccess,
  onChange,
  onSave,
  onReset,
}: ProjectFormProps) {
  const canSave = value.nome.trim() !== "" && value.gerente.trim() !== "";

  function update<K extends keyof Project>(field: K, fieldValue: Project[K]) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="project-form">
      <div className="project-form__head">
        <div>
          <h3 className="project-form__title">
            {editing ? "Editar projeto" : "Novo projeto"}
          </h3>
          <p className="project-form__subtitle">
            Organize cliente, gerente, cronograma, status e orçamento do
            projeto.
          </p>
          {editing && value.id ? (
            <p className="project-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="project-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="project-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="project-form__grid">
        <label className="project-form__field">
          <span>Código</span>
          <input
            value={value.codigo}
            onChange={(event) => update("codigo", event.target.value)}
            placeholder="PRJ-001"
            disabled={!canEditFields}
          />
        </label>

        <label className="project-form__field">
          <span>Nome</span>
          <input
            value={value.nome}
            onChange={(event) => update("nome", event.target.value)}
            placeholder="Nome do projeto"
            disabled={!canEditFields}
          />
        </label>

        <label className="project-form__field project-form__field--span-2">
          <span>Descrição</span>
          <input
            value={value.descricao}
            onChange={(event) => update("descricao", event.target.value)}
            placeholder="Escopo ou descrição resumida"
            disabled={!canEditFields}
          />
        </label>

        <label className="project-form__field">
          <span>Cliente</span>
          {partnerOptions.length > 0 ? (
            <select
              value={value.cliente}
              onChange={(event) => update("cliente", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Não vincular</option>
              {partnerOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.cliente}
              onChange={(event) =>
                update("cliente", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do cliente"
              disabled={!canEditFields}
            />
          )}
          {partnerAccess === "unavailable" ? (
            <small className="project-form__hint">
              Lista indisponível. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="project-form__field">
          <span>Gerente</span>
          {userOptions.length > 0 ? (
            <select
              value={value.gerente}
              onChange={(event) => update("gerente", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione</option>
              {userOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.gerente}
              onChange={(event) =>
                update("gerente", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do gerente"
              disabled={!canEditFields}
            />
          )}
          {userAccess === "unavailable" ? (
            <small className="project-form__hint">
              Lista indisponível. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="project-form__field">
          <span>Data inicio</span>
          <input
            type="date"
            value={value.dataInicio}
            onChange={(event) => update("dataInicio", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="project-form__field">
          <span>Data fim</span>
          <input
            type="date"
            value={value.dataFim}
            onChange={(event) => update("dataFim", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="project-form__field">
          <span>Inicio previsto</span>
          <input
            type="date"
            value={value.dataPrevistaInicio}
            onChange={(event) =>
              update("dataPrevistaInicio", event.target.value)
            }
            disabled={!canEditFields}
          />
        </label>

        <label className="project-form__field">
          <span>Fim previsto</span>
          <input
            type="date"
            value={value.dataPrevistaFim}
            onChange={(event) => update("dataPrevistaFim", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="project-form__field">
          <span>Orçamento total</span>
          <input
            value={value.orcamentoTotal}
            onChange={(event) =>
              update(
                "orcamentoTotal",
                event.target.value.replace(/[^0-9.,]/g, ""),
              )
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        <label className="project-form__field">
          <span>Orçamento gasto</span>
          <input
            value={value.orcamentoGasto}
            onChange={(event) =>
              update(
                "orcamentoGasto",
                event.target.value.replace(/[^0-9.,]/g, ""),
              )
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        <label className="project-form__field">
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

        <label className="project-form__field">
          <span>Prioridade</span>
          <select
            value={value.prioridade}
            onChange={(event) => update("prioridade", event.target.value)}
            disabled={!canEditFields}
          >
            {priorityOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>
      </div>

      <button
        type="button"
        className="project-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alterações"
            : "Criar projeto"}
      </button>
    </aside>
  );
}
