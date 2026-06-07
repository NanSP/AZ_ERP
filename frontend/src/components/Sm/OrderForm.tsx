import type {
  EmployeeOption,
  Order,
  PartnerOption,
  ProductOption,
  RelatedAccess,
} from "../../pages/Sm/OrdersPage";

type OrderFormProps = {
  value: Order;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  partnerOptions: PartnerOption[];
  partnerAccess: RelatedAccess;
  productOptions: ProductOption[];
  productAccess: RelatedAccess;
  employeeOptions: EmployeeOption[];
  employeeAccess: RelatedAccess;
  onChange: (value: Order) => void;
  onSave: () => void;
  onReset: () => void;
};

const priorityOptions = [
  { value: "baixa", label: "Baixa" },
  { value: "normal", label: "Normal" },
  { value: "alta", label: "Alta" },
  { value: "critica", label: "Critica" },
];

const statusOptions = [
  { value: "aberta", label: "Aberta" },
  { value: "em_andamento", label: "Em andamento" },
  { value: "concluida", label: "Concluida" },
  { value: "cancelada", label: "Cancelada" },
];

export default function OrderForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  partnerOptions,
  partnerAccess,
  productOptions,
  productAccess,
  employeeOptions,
  employeeAccess,
  onChange,
  onSave,
  onReset,
}: OrderFormProps) {
  const canSave =
    value.cliente.trim() !== "" &&
    value.produto.trim() !== "" &&
    value.tecnico.trim() !== "" &&
    (value.status !== "concluida" || value.dataFim.trim() !== "");

  function update<K extends keyof Order>(field: K, fieldValue: Order[K]) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="order-form">
      <div className="order-form__head">
        <div>
          <h3 className="order-form__title">
            {editing ? "Editar ordem" : "Nova ordem"}
          </h3>
          <p className="order-form__subtitle">
            Organize atendimento, agenda, tecnico responsavel e status operacional.
          </p>
          {editing && value.id ? (
            <p className="order-form__meta">Registro selecionado: #{value.id}</p>
          ) : !canEditFields ? (
            <p className="order-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="order-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="order-form__grid">
        <label className="order-form__field">
          <span>Numero OS</span>
          <input
            value={value.numeroOs}
            onChange={(event) => update("numeroOs", event.target.value)}
            placeholder="OS-001"
            disabled={!canEditFields}
          />
        </label>

        <label className="order-form__field">
          <span>Tipo de servico</span>
          <input
            value={value.tipoServico}
            onChange={(event) => update("tipoServico", event.target.value)}
            placeholder="Instalacao, manutencao, visita..."
            disabled={!canEditFields}
          />
        </label>

        <label className="order-form__field order-form__field--span-2">
          <span>Descricao do problema</span>
          <input
            value={value.descricaoProblema}
            onChange={(event) => update("descricaoProblema", event.target.value)}
            placeholder="Detalhe o chamado ou problema reportado"
            disabled={!canEditFields}
          />
        </label>

        <label className="order-form__field">
          <span>Cliente</span>
          {partnerOptions.length > 0 ? (
            <select
              value={value.cliente}
              onChange={(event) => update("cliente", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione</option>
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
            <small className="order-form__hint">
              Lista indisponivel. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="order-form__field">
          <span>Produto</span>
          {productOptions.length > 0 ? (
            <select
              value={value.produto}
              onChange={(event) => update("produto", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione</option>
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
            <small className="order-form__hint">
              Lista indisponivel. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="order-form__field">
          <span>Tecnico</span>
          {employeeOptions.length > 0 ? (
            <select
              value={value.tecnico}
              onChange={(event) => update("tecnico", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione</option>
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
            <small className="order-form__hint">
              Lista indisponivel. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="order-form__field">
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

        <label className="order-form__field">
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

        <label className="order-form__field">
          <span>Data de abertura</span>
          <input
            type="datetime-local"
            value={value.dataAbertura}
            onChange={(event) => update("dataAbertura", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="order-form__field">
          <span>Agendamento</span>
          <input
            type="date"
            value={value.dataAgendamento}
            onChange={(event) => update("dataAgendamento", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="order-form__field">
          <span>Inicio</span>
          <input
            type="datetime-local"
            value={value.dataInicio}
            onChange={(event) => update("dataInicio", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="order-form__field">
          <span>Fim</span>
          <input
            type="datetime-local"
            value={value.dataFim}
            onChange={(event) => update("dataFim", event.target.value)}
            disabled={!canEditFields || value.status !== "concluida"}
          />
        </label>
      </div>

      <button
        type="button"
        className="order-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar ordem"}
      </button>
    </aside>
  );
}
