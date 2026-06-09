import type {
  EmployeeAccess,
  EmployeeOption,
  InspectionAccess,
  InspectionOption,
  NonConformityEntry,
} from "../../pages/Qm/NonConformitiesPage";

type NonConformityFormProps = {
  value: NonConformityEntry;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  inspectionOptions: InspectionOption[];
  inspectionAccess: InspectionAccess;
  employeeOptions: EmployeeOption[];
  employeeAccess: EmployeeAccess;
  onChange: (value: NonConformityEntry) => void;
  onSave: () => void;
  onReset: () => void;
};

const statusOptions = [
  { value: "", label: "Selecione" },
  { value: "aberta", label: "Aberta" },
  { value: "em_tratamento", label: "Em tratamento" },
  { value: "resolvida", label: "Resolvida" },
  { value: "cancelada", label: "Cancelada" },
];

export default function NonConformityForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  inspectionOptions,
  inspectionAccess,
  employeeOptions,
  employeeAccess,
  onChange,
  onSave,
  onReset,
}: NonConformityFormProps) {
  const canSave =
    value.inspecao.trim() !== "" && value.responsavel.trim() !== "";

  function update<K extends keyof NonConformityEntry>(
    field: K,
    fieldValue: NonConformityEntry[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="non-conformity-form">
      <div className="non-conformities-page__panel-head">
        <div>
          <h3 className="non-conformities-page__panel-title">
            {editing ? "Editar nao conformidade" : "Nova nao conformidade"}
          </h3>
          <p className="non-conformities-page__panel-subtitle">
            Registre a ocorrência, causa, ações e andamento da tratativa.
          </p>
          {editing && value.id ? (
            <p className="non-conformities-page__panel-meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="non-conformities-page__panel-meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="non-conformities-page__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="non-conformities-page__form-grid">
        <label className="non-conformities-page__field">
          <span>Inspeção</span>
          {inspectionOptions.length > 0 ? (
            <select
              value={value.inspecao}
              onChange={(event) => update("inspecao", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione a inspeção</option>
              {inspectionOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.inspecao}
              onChange={(event) =>
                update("inspecao", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID da inspeção"
              disabled={!canEditFields}
            />
          )}
          {inspectionAccess === "unavailable" ? (
            <small className="non-conformities-page__hint">
              Lista indisponível. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="non-conformities-page__field">
          <span>Responsável</span>
          {employeeOptions.length > 0 ? (
            <select
              value={value.responsavel}
              onChange={(event) => update("responsavel", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione o responsável</option>
              {employeeOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.responsavel}
              onChange={(event) =>
                update("responsavel", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do responsável"
              disabled={!canEditFields}
            />
          )}
          {employeeAccess === "unavailable" ? (
            <small className="non-conformities-page__hint">
              Lista indisponível. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="non-conformities-page__field">
          <span>Tipo de não conformidade</span>
          <input
            value={value.tipoNaoConformidade}
            onChange={(event) =>
              update("tipoNaoConformidade", event.target.value)
            }
            placeholder="Ex.: dimensional, acabamento, processo"
            disabled={!canEditFields}
          />
        </label>

        <label className="non-conformities-page__field">
          <span>Status</span>
          <select
            value={value.status}
            onChange={(event) => update("status", event.target.value)}
            disabled={!canEditFields}
          >
            {statusOptions.map((option) => (
              <option key={option.value || "empty"} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="non-conformities-page__field">
          <span>Data de identificação</span>
          <input
            type="date"
            value={value.dataIdentificacao}
            onChange={(event) =>
              update("dataIdentificacao", event.target.value)
            }
            disabled={!canEditFields}
          />
        </label>

        <label className="non-conformities-page__field">
          <span>Data de resolução</span>
          <input
            type="date"
            value={value.dataResolucao}
            onChange={(event) => update("dataResolucao", event.target.value)}
            disabled={!canEditFields || value.status !== "resolvida"}
          />
        </label>

        <label className="non-conformities-page__field non-conformities-page__field--span-2">
          <span>Descrição</span>
          <textarea
            value={value.descricao}
            onChange={(event) => update("descricao", event.target.value)}
            placeholder="Descreva a não conformidade encontrada"
            disabled={!canEditFields}
          />
        </label>

        <label className="non-conformities-page__field non-conformities-page__field--span-2">
          <span>Causa raiz</span>
          <textarea
            value={value.causaRaiz}
            onChange={(event) => update("causaRaiz", event.target.value)}
            placeholder="Informe a causa raiz, quando conhecida"
            disabled={!canEditFields}
          />
        </label>

        <label className="non-conformities-page__field non-conformities-page__field--span-2">
          <span>Ação imediata</span>
          <textarea
            value={value.acaoImediata}
            onChange={(event) => update("acaoImediata", event.target.value)}
            placeholder="Ação tomada para conter o problema"
            disabled={!canEditFields}
          />
        </label>

        <label className="non-conformities-page__field non-conformities-page__field--span-2">
          <span>Ação corretiva</span>
          <textarea
            value={value.acaoCorretiva}
            onChange={(event) => update("acaoCorretiva", event.target.value)}
            placeholder="Plano de correcao ou prevencao"
            disabled={!canEditFields}
          />
        </label>
      </div>

      <button
        type="button"
        className="non-conformities-page__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar nao conformidade"}
      </button>
    </aside>
  );
}
